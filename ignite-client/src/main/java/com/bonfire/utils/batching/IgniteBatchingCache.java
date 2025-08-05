package com.bonfire.utils.batching;

import com.bonfire.cache.utils.BinaryObjectMapper;
import com.bonfire.cache.utils.BinaryObjectPairMapper;
import com.softwaremill.jox.Channel;
import com.softwaremill.jox.ChannelClosedException;
import com.softwaremill.jox.structured.Scope;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Functions;
import io.smallrye.mutiny.tuples.Tuple2;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.client.ClientCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static com.softwaremill.jox.Select.defaultClause;
import static com.softwaremill.jox.Select.select;
import static com.softwaremill.jox.structured.Scopes.supervised;

public class IgniteBatchingCache<K, V> {
    private static final Logger log = LoggerFactory.getLogger(IgniteBatchingCache.class);
    private final ClientCache<BinaryObject, BinaryObject> cache;
    private final ConcurrentMap<K, V> in_mem_cache = new ConcurrentHashMap<>();
    private final int batchSize;
    private final BinaryObjectMapper<K> keyMapper;
    private final BinaryObjectMapper<V> valueMapper;

    private final BinaryObjectPairMapper<K, V> pairMapper;

    private final Channel<BatchCommand<K, V>> getBatchChannel;
    private final Channel<BatchCommand<Tuple2<K, V>, Void>> putBatchChannel;

    class QUOATA {
    }

    private final Channel<QUOATA> getParallelismChannel;
    private final Channel<QUOATA> putParallelismChannel;

    public IgniteBatchingCache(ClientCache<BinaryObject, BinaryObject> cache, int batchSize, int parallelism, BinaryObjectMapper<K> keyMapper, BinaryObjectMapper<V> valueMapper) {
        this.cache = cache;
        this.batchSize = batchSize;
        this.keyMapper = keyMapper;
        this.valueMapper = valueMapper;
        this.pairMapper = new BinaryObjectPairMapper<>(this.keyMapper, this.valueMapper);
        this.getBatchChannel = Channel.newUnlimitedChannel();
        this.putBatchChannel = Channel.newUnlimitedChannel();
        this.getParallelismChannel = Channel.newBufferedChannel(parallelism);
        this.putParallelismChannel = Channel.newBufferedChannel(parallelism);
        Thread.ofVirtual().start(() -> {
            processBatchStream(getBatchChannel, this::processGetBatch);
        });
        Thread.ofVirtual().start(() -> {
            processBatchStream(putBatchChannel, this::processPutBatch);
        });
    }

    public ClientCache<BinaryObject, BinaryObject> unwrap() {
        return this.cache;
    }

    private <Req, Resp> void processBatchStream(Channel<BatchCommand<Req, Resp>> channel, Functions.TriConsumer<Scope, List<BatchCommand.Request<Req, Resp>>, Long> processBatch) {
        try {
            supervised(scope -> {
                val buffer = new ArrayList<BatchCommand.Request<Req, Resp>>();
                var startGather = System.currentTimeMillis();
                while (true) {
                    try {
                        BatchCommand<Req, Resp> command = select(channel.receiveClause(), defaultClause(BatchCommandSingleton.pause()));
                        if (command instanceof BatchCommand.Pause<Req, Resp> _) {
                            if (!buffer.isEmpty()) {
                                processBatch.accept(scope, buffer, startGather);
                                startGather = System.currentTimeMillis();
                            } else {
                                buffer.add((BatchCommand.Request<Req, Resp>) channel.receive());
                                startGather = System.currentTimeMillis();
                            }
                        } else if (command instanceof BatchCommand.Request<Req, Resp> request) {
                            buffer.add(request);
                            if (buffer.size() >= this.batchSize) {
                                processBatch.accept(scope, buffer, startGather);
                                startGather = System.currentTimeMillis();
                            }
                        }

                    } catch (ChannelClosedException channelClosed) {
                        if (!buffer.isEmpty()) {
                            processBatch.accept(scope, buffer, startGather);
                            startGather = System.currentTimeMillis();
                        }
                        break;
                    } catch (InterruptedException e) {
                        propagateException(buffer, e);
                        break;
                    }
                    Thread.yield();

                }
                return null;
            });
        } catch (InterruptedException e) {
            Log.info(STR."Batch Cache `\{this.getName()}` failed \{e}");
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return this.cache.getName();
    }

    @SneakyThrows
    private void processGetBatch(Scope scope, List<BatchCommand.Request<K, V>> buffer, Long startGather) {
        val requests = buffer.stream().collect(Collectors.groupingBy(BatchCommand.Request::req, Collectors.mapping(BatchCommand.Request::resp, Collectors.toList())));
        val gathredBy = System.currentTimeMillis() - startGather;
        val beforeLock = System.currentTimeMillis();
        getParallelismChannel.send(new QUOATA());
        val awaitLock = System.currentTimeMillis() - beforeLock;
        scope.<Void>fork(() -> {
            Map<K, V> resultsMap;
            val startGet = System.currentTimeMillis();
            try {
                if (requests.isEmpty()) {
                    resultsMap = new HashMap<>();
                } else {
                    if (requests.keySet().size() == 1) {
                        resultsMap = new HashMap<>();
                        val key = requests.keySet().iterator().next();
                        resultsMap.put(key, valueMapper.from(this.cache.get(keyMapper.to(key))));
                    } else {
                        resultsMap = pairMapper.from(this.cache.getAll(keyMapper.to(requests.keySet())));
                    }
                }
            } catch (Throwable ex) {
                requests.values().forEach(v -> v.forEach(c -> c.completeExceptionally(ex)));
                return null;
            } finally {
//                Log.info(STR."CACHE \{getName()} Loaded \{requests.size()} items (gathered by \{gathredBy}ms, await lock \{awaitLock}ms) in \{System.currentTimeMillis() - startGet}ms");
                getParallelismChannel.receive();
            }
            for (val entry : requests.entrySet()) {
                val result = resultsMap.get(entry.getKey());
                entry.getValue().forEach(c -> c.complete(result));
            }

            return null;
        });
        buffer.clear();
    }

    @SneakyThrows
    private void processPutBatch(Scope scope, List<BatchCommand.Request<Tuple2<K, V>, Void>> buffer, Long startGather) {
        val requests = buffer.stream().collect(Collectors.groupingBy(x -> x.req().getItem1(), Collectors.mapping(x -> x.req().getItem2(), Collectors.reducing(null, (_, second) -> second))));
        val responses = buffer.stream().map(BatchCommand.Request::resp).toList();
        val gathredBy = System.currentTimeMillis() - startGather;
        val beforeLock = System.currentTimeMillis();
        putParallelismChannel.send(new QUOATA());
        val awaitLock = System.currentTimeMillis() - beforeLock;
        scope.<Void>fork(() -> {
            val startPut = System.currentTimeMillis();
            try {
                if (requests.isEmpty()) {
                } else {
                    if (requests.size() == 1) {
                        val element = requests.entrySet().iterator().next();
                        this.cache.put(keyMapper.to(element.getKey()), valueMapper.to(element.getValue()));
                    } else {
                        this.cache.putAll(pairMapper.to(requests));
                    }
                }
            } catch (Throwable ex) {
                responses.forEach(r -> r.completeExceptionally(ex));
                return null;
            } finally {
//                Log.info(STR."CACHE \{getName()} PUT \{requests.size()} items (gathered by \{gathredBy}ms, await lock \{awaitLock}ms) in \{System.currentTimeMillis() - startPut}ms");
                putParallelismChannel.receive();
            }
            for (val r : responses) {
                r.complete(null);
            }

            return null;
        });
        buffer.clear();
    }

    private <Req, Resp> void propagateException(List<BatchCommand.Request<Req, Resp>> buffer, Throwable ex) {
        buffer.forEach(c -> c.resp().completeExceptionally(ex));
    }

    public final V get(K key) {
        val promise = new CompletableFuture<V>();
        try {
            getBatchChannel.send(new BatchCommand.Request<>(key, promise));
        } catch (Throwable e) {
            promise.completeExceptionally(e);
        }
        return promise.join();
    }

    public final Map<K, V> getAll(Set<K> keys) {
        try {
            return pairMapper.from(cache.getAll(keyMapper.to(keys)));

        } catch (Throwable ex) {
            log.warn(STR."getAll error \{ex}");
            throw ex;
        }
    }

    public final Uni<V> getAsync(K key) {
        if (this.batchSize == 1) {
            return Uni.createFrom().future(() -> cache.getAsync(keyMapper.to(key))).map(valueMapper::from);
        }
        val promise = new CompletableFuture<V>();
        try {
            getBatchChannel.send(new BatchCommand.Request<>(key, promise));
        } catch (Throwable e) {
            promise.completeExceptionally(e);
        }
        return Uni.createFrom().completionStage(promise);
    }

    public void put(K key, V value) {
        cache.put(keyMapper.to(key), valueMapper.to(value));
    }

    public Uni<Void> putAsync(K key, V value) {
        if (this.batchSize == 1) {
            return Uni.createFrom().future(() -> cache.putAsync(keyMapper.to(key), valueMapper.to(value)));
        }
        val promise = new CompletableFuture<Void>();
        try {
            putBatchChannel.send(new BatchCommand.Request<>(Tuple2.of(key, value), promise));
        } catch (Throwable e) {
            promise.completeExceptionally(e);
        }
        return Uni.createFrom().completionStage(promise);
    }
}

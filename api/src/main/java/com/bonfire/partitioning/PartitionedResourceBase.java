package com.bonfire.partitioning;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.annotation.PreDestroy;
import lombok.val;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class PartitionedResourceBase<K, T> implements PartitionedResource<K, T> {

    private final Partitioner<K> partitioner;

    protected abstract Logger getLog();

    protected final ConcurrentMap<Integer, T> resources = new ConcurrentHashMap<>();

    protected PartitionedResourceBase(Partitioner<K> partitioner) {
        this.partitioner = partitioner;
    }

    @Override
    final public int partition(K partitionKey) {
        return this.partitioner.partition(partitionKey);
    }

    @Override
    public int partitions() {
        return this.partitioner.partitions();
    }

    protected abstract T initResource(Optional<Integer> partitionNumber);

    @Override
    final public T getResource(K partitionKey) {
        val partition = this.partition(partitionKey);
        return getResourceByPartition(partition);
    }

    @Override
    final public <R> R withResource(K partitionKey, Function<T,R> f) {
        val partition = this.partition(partitionKey);
        return f.apply(getResourceByPartition(partition));
    }

    @Override
    public <R> Uni<R> withResourceAsync(K partitionKey, Function<T, Uni<R>> f) {
        val partition = this.partition(partitionKey);
        return getResourceByPartitionAsync(partition).flatMap(f::apply);
    }

    @Override
    public <R> Multi<R> withResourceMulti(K partitionKey, Function<T, Multi<R>> f) {
        val partition = this.partition(partitionKey);
        return getResourceByPartitionAsync(partition).toMulti().flatMap(f);
    }

    @Override
    final public <R> List<R> withResources(Set<K> partitionKeys, BiFunction<T, Set<K>, R> f) {
        return partitionKeys.stream().collect(Collectors.groupingBy(this::partition, Collectors.toSet()))
                .entrySet()
                .stream().map(e -> f.apply(getResourceByPartition(e.getKey()), e.getValue()))
                .toList();
    }

    final public T getResourceByPartition(int partition) {
        val client = this.resources.computeIfAbsent(partition, p -> this.partitioner.partitions() < 1 ? initResource(Optional.empty()) : initResource(Optional.of(p)));
        if (client == null) {
            getLog().warn("Partition " + partition + " not found for key " + partition);
        }
        return client;
    }

    final public Uni<T> getResourceByPartitionAsync(int partition) {
        var t = this.resources.get(partition);
        if (t != null) {
            return Uni.createFrom().item(t);
        }
        return Uni.createFrom().deferred(() -> Uni.createFrom().item(getResourceByPartition(partition))).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    protected abstract void closeResource(int partition, T res) throws Exception;

    @PreDestroy
    public void shutdown() {
        resources.forEach((k, v) -> {
            try {
                closeResource(k, v);
            } catch (Exception e) {
                getLog().warn("Failed to close resource " + k);
            }
        });
    }

}

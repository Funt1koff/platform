package com.bonfire.grpc;

import com.bonfire.partitioning.PartitionedResourceBase;
import com.bonfire.partitioning.Partitioner;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.quarkus.grpc.runtime.supports.Channels;
import lombok.val;
import org.apache.commons.text.StringSubstitutor;
import org.jboss.logging.Logger;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class PartitionedGrpcClient<K, T> extends PartitionedResourceBase<K, T> {
    private final PartitionedGrpcClientConfig config;
    private final ConcurrentMap<Integer, ManagedChannel> channels;

    protected PartitionedGrpcClient(PartitionedGrpcClientConfig config, Partitioner<K> partitioner) {
        super(partitioner);
        this.channels = new ConcurrentHashMap<>(Math.max(1, config.nodes()));
        this.config = config;
    }

    protected abstract Logger getLog();

    protected abstract T getClientByPartitionedChannel(Channel channel);

    @Override
    final protected T initResource(Optional<Integer> partitionNumber) {
        val nodeIdx = partitionNumber.orElse(0) % Math.max(1, this.config.nodes());
        val managedChannel = channels.computeIfAbsent(nodeIdx, nodeIdx_ -> {
            val target = StringSubstitutor.replace(config.target(), Map.of("partition", nodeIdx_));
            try {
                return (ManagedChannel) Channels.createChannel(target, Set.of());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        getLog().info(STR."Creating resource node: \{nodeIdx} partition: \{partitionNumber}");
        return getClientByPartitionedChannel(managedChannel);
    }

    @Override
    protected void closeResource(int partition, T res) throws Exception {
        if(res instanceof AutoCloseable closeable) {
            closeable.close();
        }
    }

    @PreDestroy
    @Override
    public void shutdown() {
        super.shutdown();
        channels.forEach((k, c) -> {
            try {
                c.shutdown();
            } catch (Exception e) {
                getLog().warn(STR."Fauled to close channel for partition: \{k}");
            }
        });
    }
}

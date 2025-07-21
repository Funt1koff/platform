package com.bonfire.partitioning;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface PartitionedResource<K, T> extends Partitioner<K> {
    T getResource(K partitionKey);

    <R> R withResource(K partitionKey, Function<T, R> f);
    <R> Uni<R> withResourceAsync(K partitionKey, Function<T, Uni<R>> f);
    <R> Multi<R> withResourceMulti(K partitionKey, Function<T, Multi<R>> f);

    <R> List<R> withResources(Set<K> partitionKeys, BiFunction<T, Set<K>, R> f);

    default <U> PartitionedResource<K, U> mapResource(Function<T, U> mapper) {
        return new PartitionedResource<K, U>() {
            @Override
            public int partition(K partitionKey) {
                return PartitionedResource.this.partition(partitionKey);
            }

            @Override
            public U getResource(K partitionKey) {
                return mapper.apply(PartitionedResource.this.getResource(partitionKey));
            }

            @Override
            public <R> R withResource(K partitionKey, Function<U, R> f) {
                return PartitionedResource.this.withResource(partitionKey, t -> f.apply(mapper.apply(t)));
            }

            @Override
            public <R> Uni<R> withResourceAsync(K partitionKey, Function<U, Uni<R>> f) {
                return PartitionedResource.this.withResourceAsync(partitionKey, t -> f.apply(mapper.apply(t)));
            }

            @Override
            public <R> Multi<R> withResourceMulti(K partitionKey, Function<U, Multi<R>> f) {
                return PartitionedResource.this.withResourceMulti(partitionKey, t -> f.apply(mapper.apply(t)));
            }

            @Override
            public <R> List<R> withResources(Set<K> partitionKeys, BiFunction<U, Set<K>, R> f) {
                return PartitionedResource.this.withResources(partitionKeys, (t, keys) -> f.apply(mapper.apply(t), keys));
            }

            @Override
            public int partitions() {
                return PartitionedResource.this.partitions();
            }
        };
    }

    default <K2> PartitionedResource<K2, T> comapKey(Function<K2, K> mapper) {
        return new PartitionedResource<K2, T>() {
            @Override
            public int partition(K2 partitionKey) {
                return PartitionedResource.this.partition(mapper.apply(partitionKey));
            }

            @Override
            public T getResource(K2 partitionKey) {
                return PartitionedResource.this.getResource(mapper.apply(partitionKey));
            }

            @Override
            public <R> R withResource(K2 partitionKey, Function<T, R> f) {
                return PartitionedResource.this.withResource(mapper.apply(partitionKey), f);
            }

            @Override
            public <R> Uni<R> withResourceAsync(K2 partitionKey, Function<T, Uni<R>> f) {
                return PartitionedResource.this.withResourceAsync(mapper.apply(partitionKey), f);
            }

            @Override
            public <R> Multi<R> withResourceMulti(K2 partitionKey, Function<T, Multi<R>> f) {
                return PartitionedResource.this.withResourceMulti(mapper.apply(partitionKey), f);
            }

            @Override
            public <R> List<R> withResources(Set<K2> partitionKeys, BiFunction<T, Set<K2>, R> f) {
                Map<K, K2> keyMapping = partitionKeys.stream().collect(Collectors.toMap(mapper, k -> k));
                return PartitionedResource.this.withResources(
                        keyMapping.keySet(),
                        (t, keys) -> f.apply(t, keys.stream().map(keyMapping::get).collect(Collectors.toSet())));
            }

            @Override
            public int partitions() {
                return PartitionedResource.this.partitions();
            }
        };
    }
}

package com.bonfire.partitioning;

public interface Partitioner<K> {

    int partition(K partitionKey);

    int partitions();
}

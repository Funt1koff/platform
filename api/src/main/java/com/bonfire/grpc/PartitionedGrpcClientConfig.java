package com.bonfire.grpc;

public interface PartitionedGrpcClientConfig {
    String target();
    int nodes();
}

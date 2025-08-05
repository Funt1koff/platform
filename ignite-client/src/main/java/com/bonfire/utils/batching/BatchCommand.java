package com.bonfire.utils.batching;

import java.util.concurrent.CompletableFuture;

public sealed interface BatchCommand<K, V> {
    record Request<K, V>(K req, CompletableFuture<V> resp) implements BatchCommand<K, V> {
    }

    record Pause<K, V>() implements BatchCommand<K, V> {
    }

}

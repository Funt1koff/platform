package com.bonfire.ignite.node.common.utils;

import io.smallrye.mutiny.Uni;
import lombok.val;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class UniHelper {

    public static <T> Uni<T> runOnVirtualThread(Supplier<T> supplier) {
        return Uni.createFrom()
                .deferred(() -> {
                    val promise = new CompletableFuture<T>();
                    val thread = Thread.ofVirtual().start(() -> {
                        try {
                            promise.complete(supplier.get());
                        } catch (Exception e) {
                            promise.completeExceptionally(e);
                        }
                    });
                    return Uni.createFrom()
                            .completionStage(promise)
                            .onCancellation()
                            .invoke(thread::interrupt);
                });
    }

    public static Uni<Void> runOnVirtualThread(Runnable runnable) {
        return Uni.createFrom().deferred(() -> {
            val promise = new CompletableFuture<Void>();
            val thread = Thread.ofVirtual().start(() -> {
                try {
                    runnable.run();
                    promise.complete(null);
                } catch (Exception e) {
                    promise.completeExceptionally(e);
                }
            });
            return Uni.createFrom()
                    .completionStage(promise)
                    .onCancellation()
                    .invoke(thread::interrupt);
        });
    }
}

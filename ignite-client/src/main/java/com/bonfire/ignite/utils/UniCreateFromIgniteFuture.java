package com.bonfire.ignite.utils;

import io.smallrye.mutiny.helpers.EmptyUniSubscription;
import io.smallrye.mutiny.operators.AbstractUni;
import io.smallrye.mutiny.subscription.UniSubscriber;
import org.apache.ignite.IgniteException;
import org.apache.ignite.lang.IgniteFuture;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class UniCreateFromIgniteFuture<T> extends AbstractUni<T> {
    private final Supplier<? extends IgniteFuture<? extends T>> supplier;
    private final Duration timeout;

    public UniCreateFromIgniteFuture(Supplier<? extends IgniteFuture<? extends T>> supplier) {
        this(supplier, null);
    }

    public UniCreateFromIgniteFuture(Supplier<? extends IgniteFuture<? extends T>> supplier, Duration timeout) {
        this.supplier = supplier;
        this.timeout = timeout;
    }

    public void subscribe(UniSubscriber<? super T> downstream) {
        final var future = this.obtainFuture(downstream);
        if (future != null) {
            if (future.isDone()) {
                this.dispatchImmediateResult(future, downstream);
            } else {
                this.dispatchDeferredResult(future, downstream);
            }

        }
    }

    private IgniteFuture<? extends T> obtainFuture(UniSubscriber<? super T> downstream) {
        try {
            final var future = this.supplier.get();
            if (future != null) return future;

            downstream.onSubscribe(EmptyUniSubscription.DONE);
            downstream.onFailure(new NullPointerException("The produced Future is `null`"));
        } catch (Throwable err) {
            downstream.onSubscribe(EmptyUniSubscription.DONE);
            downstream.onFailure(err);
        }
        return null;
    }

    private void dispatchImmediateResult(IgniteFuture<? extends T> future, UniSubscriber<? super T> downstream) {
        try {
            final var item = future.get();
            downstream.onSubscribe(EmptyUniSubscription.DONE);
            downstream.onItem(item);
        } catch (IgniteException e) {
            downstream.onSubscribe(EmptyUniSubscription.DONE);
            downstream.onFailure(e.getCause());
        }
    }

    private void dispatchDeferredResult(IgniteFuture<? extends T> future, UniSubscriber<? super T> downstream) {
        AtomicBoolean cancelled = new AtomicBoolean(false);
        downstream.onSubscribe(() -> {
            cancelled.set(true);
            future.cancel();
        });
        future.listen(f -> {
            try {
                T item;
                if (this.timeout != null) {
                    item = f.get(this.timeout.toMillis(), TimeUnit.MILLISECONDS);
                } else {
                    item = f.get();
                }

                if (!cancelled.get()) {
                    downstream.onItem(item);
                }
            } catch (IgniteException e) {
                if (!cancelled.get()) {
                    downstream.onFailure(e.getCause());
                }
            } catch (Exception ex) {
                if (!cancelled.get()) {
                    downstream.onFailure(ex);
                }
            }
        });
    }
}

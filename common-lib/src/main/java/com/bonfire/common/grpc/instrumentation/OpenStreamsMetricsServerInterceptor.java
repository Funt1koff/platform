package com.bonfire.common.grpc.instrumentation;

import io.grpc.*;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.grpc.GlobalInterceptor;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.val;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
@GlobalInterceptor
public class OpenStreamsMetricsServerInterceptor {
    private static final String METRIC_NAME_SERVER_PROCESSING_DURATION = "grpc.server.open_streams";

    private final Map<MethodDescriptor<?, ?>, AtomicInteger> metricsForMethods;
    private final MeterRegistry registry;

    public OpenStreamsMetricsServerInterceptor(MeterRegistry registry) {
        this.registry = registry;
        this.metricsForMethods = new ConcurrentHashMap<>();
    }

    protected AtomicInteger newOpenStreamsGaugeFor(MethodDescriptor<?, ?> method) {
        var counter = new AtomicInteger(0);
        Gauge.builder(METRIC_NAME_SERVER_PROCESSING_DURATION, counter, AtomicInteger::doubleValue)
                .description("Current number of open stream calls")
                .tag("service", method.getServiceName())
                .tag("method", method.getBareMethodName())
                .tag("methodType", method.getType().name())
                .register(registry);
        return counter;
    }

    public <Q, A> ServerCall.Listener<Q> intercepterCall(ServerCall<Q, A> call, Metadata requestHeaders, ServerCallHandler<Q, A> next) {
        val counter = this.metricsForMethods.computeIfAbsent(call.getMethodDescriptor(), this::newOpenStreamsGaugeFor);
        val listener = next.startCall(call, requestHeaders);
        counter.incrementAndGet();

        return new OpenStreamsMetricsCollectingServerCallListener<>(listener, counter);
    }

    static class OpenStreamsMetricsCollectingServerCallListener<Q> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<Q> {

        private final AtomicInteger openStreamsCounter;

        protected OpenStreamsMetricsCollectingServerCallListener(ServerCall.Listener<Q> delegate, AtomicInteger openStreamsCounter) {
            super(delegate);
            this.openStreamsCounter = openStreamsCounter;
        }

        public void onComplete() {
            this.openStreamsCounter.decrementAndGet();
            super.onComplete();
        }

        public void onCancel() {
            this.openStreamsCounter.decrementAndGet();
            super.onCancel();
        }
    }
}

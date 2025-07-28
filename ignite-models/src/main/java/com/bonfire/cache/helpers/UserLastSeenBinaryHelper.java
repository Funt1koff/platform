package com.bonfire.cache.helpers;

import com.bonfire.cache.UserLastSeen;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.apache.ignite.client.IgniteClient;

import java.util.function.Supplier;

public class UserLastSeenBinaryHelper {
    private final Supplier<BinaryObjectBuilder> builderSupplier;

    public UserLastSeenBinaryHelper(IgniteClient ignite) {
        builderSupplier = () -> ignite.binary().builder(UserLastSeen.class.getName());
    }

    public BinaryObjectBuilder initBuilder() {
        return builderSupplier.get();
    }
}

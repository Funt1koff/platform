package com.bonfire.cache.helpers;

import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.binary.BinaryObjectBuilder;
import com.bonfire.cache.UserSubscriptions;

import java.util.function.Supplier;

public class UserSubscriptionsBinaryHelper {

    private final Supplier<BinaryObjectBuilder> builderSupplier;

    public UserSubscriptionsBinaryHelper(IgniteClient ignite) {
        builderSupplier = () -> ignite.binary().builder(UserSubscriptions.class.getName());
    }

    public BinaryObjectBuilder initBuilder() {
        return builderSupplier.get();
    }
}

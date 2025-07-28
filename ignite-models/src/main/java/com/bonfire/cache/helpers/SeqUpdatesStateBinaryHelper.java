package com.bonfire.cache.helpers;

import com.bonfire.cache.SeqUpdatesState;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.apache.ignite.client.IgniteClient;

import java.util.function.Supplier;

public class SeqUpdatesStateBinaryHelper {

    private final Supplier<BinaryObjectBuilder> builderSupplier;

    public SeqUpdatesStateBinaryHelper(IgniteClient ignite) {
        builderSupplier = () -> ignite.binary().builder(SeqUpdatesState.class.getName());
    }

    public BinaryObjectBuilder initBuilder() {
        return builderSupplier.get();
    }
}

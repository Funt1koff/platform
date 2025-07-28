package com.bonfire.cache.utils;

import org.apache.ignite.binary.BinaryObject;

import java.util.Set;
import java.util.stream.Collectors;

public interface BinaryObjectMapper<V> {

    BinaryObjectMapper<BinaryObject> DEFAULT = new BinaryObjectMapper<BinaryObject>() {
        @Override
        public BinaryObject to(BinaryObject b) {
            return b;
        }

        @Override
        public BinaryObject from(BinaryObject b) {
            return b;
        }
    };

    BinaryObject to(V v);

    V from(BinaryObject binaryObject);

    default Set<BinaryObject> to(Set<V> set) {
        return set.stream().map(this::to).collect(Collectors.toSet());
    }
}

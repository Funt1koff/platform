package com.bonfire.cache.utils;

import org.apache.ignite.binary.BinaryObject;

import java.util.Map;
import java.util.stream.Collectors;

public class BinaryObjectPairMapper<K, V> {
    private final BinaryObjectMapper<K> keyMapper;
    private final BinaryObjectMapper<V> valueMapper;

    public BinaryObjectPairMapper(BinaryObjectMapper<K> keyMapper, BinaryObjectMapper<V> valueMapper) {
        this.keyMapper = keyMapper;
        this.valueMapper = valueMapper;
    }

    public Map<BinaryObject, BinaryObject> to(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> keyMapper.to(entry.getKey()),
                        entry -> valueMapper.to(entry.getValue()))
                );
    }

    public Map<K, V> from(Map<BinaryObject, BinaryObject> map) {
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> keyMapper.from(entry.getKey()),
                        entry -> valueMapper.from(entry.getValue()))
                );
    }
}

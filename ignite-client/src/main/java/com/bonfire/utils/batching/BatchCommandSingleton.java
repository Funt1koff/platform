package com.bonfire.utils.batching;

public class BatchCommandSingleton {

    private static final BatchCommand<?, ?> instance = new BatchCommand.Pause<>();

    public static <K, V> BatchCommand<K, V> pause() {
        return (BatchCommand<K, V>)instance;
    }
}

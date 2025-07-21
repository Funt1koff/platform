package com.bonfire.partitioning;

public abstract class PartitionerBase<K> implements Partitioner<K> {
    protected final PartitionerConfig config;

    protected PartitionerBase(PartitionerConfig config) {
        this.config = config;
    }

    @Override
    public int partition(K partitionKey) {
        if (this.config.partitions() < 1) {
            return 0;
        }
        return partition(partitionKey, this.config.partitions());
    }

    @Override
    public int partitions() {
        return 0;
    }

    protected abstract int partition(K partitionKey, int partitions);

    public static int partitionByUserId(Long userId, int partitions) {
        return (int) Math.abs(userId % partitions);
    }

    public static int partitionByBotId(Long botId, int partitions) {
        return (int) Math.abs(botId % partitions);
    }

//    public static int partitionByInitiator(Initiator initiator, int partitions) {
//        return switch (initiator.getCallerCase()) {
//            case USER -> partitionByUserId(initiator.getUser().getUserId(), partitions);
//            case BOT -> partitionByBotId(initiator.getBot.getBotId(), partitions);
//            case SYSTEM -> throw new IllegalStateException("Cannot partition by system initiator");
//            case CALLER_NOT_SET -> throw new IllegalStateException("Empty initiator");
//        };
//    }
}

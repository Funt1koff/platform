package com.bonfire.ignite.node.internal.api.serde.deserialize;

import com.bonfire.internal.api.commands.BotUpdatePersistCommand;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;

public final class BotUpdatePersistCommandDeserializer implements Deserializer<BotUpdatePersistCommand> {

    @Override
    public BotUpdatePersistCommand deserialize(String s, byte[] bytes) {
        try {
            return BotUpdatePersistCommand.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }
}

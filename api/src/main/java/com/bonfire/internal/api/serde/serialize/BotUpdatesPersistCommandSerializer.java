package com.bonfire.internal.api.serde.serialize;

import com.bonfire.internal.api.commands.BotUpdatePersistCommand;
import org.apache.kafka.common.serialization.Serializer;

public final class BotUpdatesPersistCommandSerializer implements Serializer<BotUpdatePersistCommand> {

    @Override
    public byte[] serialize(String s, BotUpdatePersistCommand botUpdatePersistCommand) {
        return botUpdatePersistCommand.toByteArray();
    }
}

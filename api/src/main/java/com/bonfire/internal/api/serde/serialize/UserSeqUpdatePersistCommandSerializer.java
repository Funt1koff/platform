package com.bonfire.internal.api.serde.serialize;

import com.bonfire.internal.api.commands.UserSeqUpdatePersistCommand;
import org.apache.kafka.common.serialization.Serializer;

public class UserSeqUpdatePersistCommandSerializer implements Serializer<UserSeqUpdatePersistCommand> {
    @Override
    public byte[] serialize(String s, UserSeqUpdatePersistCommand userSeqUpdatePersistCommand) {
        return userSeqUpdatePersistCommand.toByteArray();
    }
}

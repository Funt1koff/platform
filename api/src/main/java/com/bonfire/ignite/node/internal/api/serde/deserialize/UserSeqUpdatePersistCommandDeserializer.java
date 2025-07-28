package com.bonfire.ignite.node.internal.api.serde.deserialize;

import com.bonfire.internal.api.commands.UserSeqUpdatePersistCommand;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;

public class UserSeqUpdatePersistCommandDeserializer implements Deserializer<UserSeqUpdatePersistCommand> {
    @Override
    public UserSeqUpdatePersistCommand deserialize(String s, byte[] bytes) {
        try {
            return UserSeqUpdatePersistCommand.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }
}

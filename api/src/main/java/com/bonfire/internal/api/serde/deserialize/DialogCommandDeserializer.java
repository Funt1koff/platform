package com.bonfire.internal.api.serde.deserialize;

import com.bonfire.internal.api.commands.DialogCommand;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;

public final class DialogCommandDeserializer implements Deserializer<DialogCommand> {
    @Override
    public DialogCommand deserialize(String s, byte[] bytes) {
        try {
            return DialogCommand.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }
}

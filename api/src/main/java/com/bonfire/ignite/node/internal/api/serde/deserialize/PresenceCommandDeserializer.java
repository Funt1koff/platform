package com.bonfire.ignite.node.internal.api.serde.deserialize;

import com.bonfire.internal.api.models.PresenceCommand;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;

public class PresenceCommandDeserializer implements Deserializer<PresenceCommand> {
    @Override
    public PresenceCommand deserialize(String s, byte[] bytes) {
        try {
            return PresenceCommand.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }
}

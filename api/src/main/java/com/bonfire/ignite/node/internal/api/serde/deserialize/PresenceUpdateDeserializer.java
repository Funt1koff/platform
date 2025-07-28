package com.bonfire.ignite.node.internal.api.serde.deserialize;

import com.bonfire.internal.api.models.PresenceUpdate;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;

public class PresenceUpdateDeserializer implements Deserializer<PresenceUpdate> {
    @Override
    public PresenceUpdate deserialize(String s, byte[] bytes) {
        try {
            return PresenceUpdate.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }
}

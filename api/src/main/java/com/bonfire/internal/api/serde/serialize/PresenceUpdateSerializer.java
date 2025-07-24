package com.bonfire.internal.api.serde.serialize;

import com.bonfire.internal.api.models.PresenceUpdate;
import org.apache.kafka.common.serialization.Serializer;

public class PresenceUpdateSerializer implements Serializer<PresenceUpdate> {
    @Override
    public byte[] serialize(String s, PresenceUpdate presenceUpdate) {
        return presenceUpdate.toByteArray();
    }
}

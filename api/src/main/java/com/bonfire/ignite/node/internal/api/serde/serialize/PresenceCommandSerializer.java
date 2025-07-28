package com.bonfire.ignite.node.internal.api.serde.serialize;

import com.bonfire.internal.api.models.PresenceCommand;
import org.apache.kafka.common.serialization.Serializer;

public class PresenceCommandSerializer implements Serializer<PresenceCommand> {
    @Override
    public byte[] serialize(String s, PresenceCommand presenceCommand) {
        return presenceCommand.toByteArray();
    }
}

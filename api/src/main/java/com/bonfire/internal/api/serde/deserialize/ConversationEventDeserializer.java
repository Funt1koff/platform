package com.bonfire.internal.api.serde.deserialize;

import com.bonfire.internal.api.events.ConversationEvent;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;

public final class ConversationEventDeserializer implements Deserializer<ConversationEvent> {
    @Override
    public ConversationEvent deserialize(String s, byte[] bytes) {
        try {
            return ConversationEvent.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.bonfire.internal.api.serde.serialize;

import com.bonfire.internal.api.events.ConversationEvent;
import org.apache.kafka.common.serialization.Serializer;

public final class ConversationEventSerializer implements Serializer<ConversationEvent> {

    @Override
    public byte[] serialize(String s, ConversationEvent conversationEvent) {
        return conversationEvent.toByteArray();
    }
}

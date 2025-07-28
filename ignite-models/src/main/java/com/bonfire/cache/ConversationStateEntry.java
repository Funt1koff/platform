package com.bonfire.cache;

import lombok.Data;

import java.util.Map;

@Data
public class ConversationStateEntry {
    private int maxMessageId;
    private int topMessageId;
    private int maxReadMessageId;
    private long maxSequenceNumber;
    Map<Long, Integer> senders;
}

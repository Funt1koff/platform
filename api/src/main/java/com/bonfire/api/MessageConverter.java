package com.bonfire.api;


import com.bonfire.internal.api.models.Message;
import com.bonfire.peers.Peer;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class MessageConverter {

    private static com.bonfire.messages.Message.Builder toPublic(Message message) {
        val builder = com.bonfire.messages.Message.newBuilder();
        builder.setId(message.getId());
        builder.setAuthor(message.getCreatedBy());
        if (message.hasEditedAt()) builder.setEditDate(message.getEditedAt());
        builder.setContent(message.getContent());
        builder.setDateTime(message.getCreatedAt());
        return builder;
    }

    public static com.bonfire.messages.Message toPublic(Peer peer, Message message) {
        return toPublic(message)
                .setFromPeer(peer)
                .build();
    }
}

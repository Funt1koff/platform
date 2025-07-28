package com.bonfire.ignite.node.internal.api;

import com.bonfire.internal.api.models.ConversationPeer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ConversationPeerHelpers {

    public static ConversationPeer.User2User createU2u(long uid1, long uid2) {
        if (Long.compareUnsigned(uid1, uid2) == 0) {
            return ConversationPeer.User2User.newBuilder()
                    .setLesserUid(uid1)
                    .setGreaterUid(uid2)
                    .build();
        } else {
            return ConversationPeer.User2User.newBuilder()
                    .setLesserUid(uid2)
                    .setGreaterUid(uid1)
                    .build();
        }
    }

    public static long getChatMateUid(ConversationPeer.User2User peer, long selfUid) {
        return peer.getLesserUid() == selfUid ? peer.getGreaterUid() : peer.getLesserUid();
    }

    public static long getChatMateUid(ConversationPeer.User2Bot peer) {
        return peer.getBotId();
    }
}

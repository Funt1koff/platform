package com.bonfire.internal.api;

import com.bonfire.peers.BotPeer;
import com.bonfire.peers.UserPeer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PeerHelpers {
    public static UserPeer userPeer(long userId) {
        return UserPeer.newBuilder()
                .setUserId(userId)
                .build();
    }

    public static BotPeer botPeer(long botId) {
        return BotPeer.newBuilder()
                .setBotId(botId)
                .build();
    }
}

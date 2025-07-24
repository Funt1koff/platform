package com.bonfire.internal.api;

import com.bonfire.common.Member;
import com.bonfire.internal.api.models.Initiator;
import com.bonfire.peers.BotPeer;
import com.bonfire.peers.UserPeer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MemberHelpers {

    public static Member user(long userId) {
        return Member.newBuilder()
                .setUser(PeerHelpers.userPeer(userId))
                .build();
    }

    public static Member user(UserPeer user) {
        return Member.newBuilder()
                .setUser(user)
                .build();
    }

    public static Member bot(long botId) {
        return Member.newBuilder()
                .setBot(PeerHelpers.botPeer(botId))
                .build();
    }

    public static Member bot(BotPeer bot) {
        return Member.newBuilder()
                .setBot(bot)
                .build();
    }

    public static Member fromInitiator(Initiator initiator) {
        return switch (initiator.getCallerCase()) {
            case USER -> user(initiator.getUser().getUserId());
            case BOT -> bot(initiator.getBot().getBotId());
            case SYSTEM -> throw new IllegalArgumentException("System initiator is not supported");
            case CALLER_NOT_SET -> throw new IllegalArgumentException("Unknown initiator type");
        };
    }
}

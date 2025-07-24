package com.bonfire.internal.api;

import com.bonfire.internal.api.models.ConversationPeer;
import com.bonfire.internal.api.models.Initiator;
import com.bonfire.peers.*;
import com.google.common.io.BaseEncoding;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class PeerConverter {

    public static Peer toActorPeer(Initiator.User userInitiator) {
        return Peer.newBuilder()
                .setUser(UserPeer.newBuilder().setUserId(userInitiator.getUserId()))
                .build();
    }

    public static Peer toActorPeer(Initiator.Bot botInitiator) {
        return Peer.newBuilder()
                .setBot(BotPeer.newBuilder().setBotId(botInitiator.getBotId()))
                .build();
    }

    public static Peer toActorPeer(Initiator initiator) {
        return switch (initiator.getCallerCase()) {
            case USER:
                yield toActorPeer(initiator.getUser());
            case BOT:
                yield toActorPeer(initiator.getBot());
            case SYSTEM, CALLER_NOT_SET:
                throw new IllegalArgumentException(STR."Unexpected value: \{initiator.getCallerCase()}");
        };
    }

    public static ConversationPeer toConversationPeer(Peer peer, Initiator.User userInitiator) {
        val selfUserId = userInitiator.getUserId();
        return switch (peer.getValueCase()) {
            case USER -> ConversationPeer.newBuilder()
                    .setUser2User(
                            ConversationPeerHelpers.createU2u(peer.getUser().getUserId(), selfUserId)
                    )
                    .build();
            case BOT -> ConversationPeer.newBuilder()
                    .setUser2Bot(
                            ConversationPeer.User2Bot
                                    .newBuilder()
                                    .setUserId(selfUserId)
                                    .setBotId(peer.getBot().getBotId()))
                    .build();
            case SELF -> ConversationPeer.newBuilder()
                    .setSelf(
                            ConversationPeer.Self
                                    .newBuilder()
                                    .setSelfUserId(selfUserId))
                    .build();
            case GROUP -> ConversationPeer.newBuilder()
                    .setGroup(
                            ConversationPeer.Group.newBuilder().setId(peer.getGroup().getGroupId()))
                    .build();
            case CHANNEL -> ConversationPeer.newBuilder()
                    .setChannel(
                            ConversationPeer.Channel.newBuilder().setId(peer.getChannel().getChannelId())
                    )
                    .build();
            case VALUE_NOT_SET -> throw new IllegalArgumentException(STR."Unexpected value: \{peer.getValueCase()}");
        };
    }

    public static ConversationPeer toConversationPeer(Peer peer, Initiator.Bot botInitiator) {
        val selfBotId = botInitiator.getBotId();
        return switch (peer.getValueCase()) {
            case USER -> ConversationPeer.newBuilder()
                    .setUser2Bot(
                            ConversationPeer.User2Bot.newBuilder().setUserId(peer.getUser().getUserId()).setBotId(selfBotId)
                    )
                    .build();
            case GROUP -> ConversationPeer.newBuilder()
                    .setGroup(
                            ConversationPeer.Group.newBuilder().setId(peer.getGroup().getGroupId())
                    )
                    .build();
            case CHANNEL -> ConversationPeer.newBuilder()
                    .setChannel(
                            ConversationPeer.Channel.newBuilder().setId(peer.getChannel().getChannelId())
                    )
                    .build();
            case BOT, SELF ->
                    throw new IllegalArgumentException(STR."Conversation peer cannot be initiated from peer \{peer.getValueCase()} for Initiator.Bot");
            case VALUE_NOT_SET -> throw new IllegalArgumentException(STR."Unexpected value: \{peer.getValueCase()}");
        };
    }

    public static ConversationPeer toConversationPeer(Peer peer, Initiator.System systemInitiator) {
        return switch (peer.getValueCase()) {
            case GROUP -> ConversationPeer.newBuilder()
                    .setGroup(
                            ConversationPeer.Group.newBuilder().setId(peer.getGroup().getGroupId())
                    )
                    .build();
            case CHANNEL -> ConversationPeer.newBuilder()
                    .setChannel(
                            ConversationPeer.Channel.newBuilder().setId(peer.getChannel().getChannelId())
                    )
                    .build();
            case USER, BOT, SELF ->
                    throw new IllegalArgumentException(STR."Conversation peer cannot be initiated from peer \{peer.getValueCase()} for Initiator.Bot");
            case VALUE_NOT_SET -> throw new IllegalArgumentException(STR."Unexpected value: \{peer.getValueCase()}");
        };
    }

    public static ConversationPeer toConversationPeer(Peer peer, Initiator initiator) {
        return switch (initiator.getCallerCase()) {
            case USER -> toConversationPeer(peer, initiator.getUser());
            case BOT -> toConversationPeer(peer, initiator.getBot());
            case SYSTEM -> toConversationPeer(peer, initiator.getSystem());
            case CALLER_NOT_SET ->
                    throw new IllegalArgumentException(STR."Unexpected initiator value: \{peer.getValueCase()}");
        };
    }

    public static Peer asPeer(UserPeer peer) {
        return Peer.newBuilder().setUser(peer).build();
    }

    public static Peer asPeer(GroupPeer peer) {
        return Peer.newBuilder().setGroup(peer).build();
    }

    public static Peer toPeer(ConversationPeer peer, long userId) {
        return switch (peer.getBodyCase()) {
            case USER2USER -> Peer.newBuilder()
                    .setUser(UserPeer.newBuilder()
                            .setUserId(ConversationPeerHelpers.getChatMateUid(peer.getUser2User(), userId))
                    )
                    .build();
            case USER2BOT -> Peer.newBuilder()
                    .setBot(
                            BotPeer.newBuilder()
                                    .setBotId(ConversationPeerHelpers.getChatMateUid(peer.getUser2Bot()))
                    )
                    .build();
            case SELF -> Peer.newBuilder()
                    .setUser(
                            UserPeer.newBuilder()
                                    .setUserId(userId)
                    )
                    .build();
            case GROUP -> Peer.newBuilder()
                    .setGroup(
                            GroupPeer.newBuilder()
                                    .setGroupId(peer.getGroup().getId())
                    )
                    .build();
            case CHANNEL -> Peer.newBuilder()
                    .setChannel(
                            ChannelPeer.newBuilder().setChannelId(peer.getChannel().getId())
                    )
                    .build();
            default -> throw new IllegalStateException(STR."Unexpected value: \{peer.getBodyCase()}");
        };
    }

    public static byte[] conversationBinaryKey(ConversationPeer peer) {
        return peer.toByteArray();
    }

    public static String conversationKey(ConversationPeer peer) {
        return BaseEncoding.base64().encode(conversationBinaryKey(peer));
    }
}

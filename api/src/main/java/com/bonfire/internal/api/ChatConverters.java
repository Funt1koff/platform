package com.bonfire.internal.api;

import com.bonfire.common.Channel;
import com.bonfire.common.Chat;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class ChatConverters {

    public static Chat fromGroup(long groupId, com.bonfire.internal.api.models.GroupWithSelfMemberInfo groupWithSelfMemberInfo) {
        return Chat.newBuilder()
                .setGroup(GroupConverters.toAPI(groupId, groupWithSelfMemberInfo))
                .build();
    }

    public static Chat fromChannel(long channelId) {
        val channelChat = Channel.newBuilder()
                .setChannelId(channelId)
                .setTitle(STR."Channel#\{channelId}");

        return Chat.newBuilder().setChannel(channelChat).build();
    }
}

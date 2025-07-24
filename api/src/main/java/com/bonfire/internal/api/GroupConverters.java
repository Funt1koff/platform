package com.bonfire.internal.api;

import com.bonfire.internal.api.models.GroupMemberInfo;
import com.bonfire.internal.api.models.GroupMemberWithInfo;
import com.bonfire.internal.api.models.GroupWithSelfMemberInfo;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.Optional;

@UtilityClass
public class GroupConverters {

    public static com.bonfire.common.MemberWithInfo toAPI(GroupMemberWithInfo groupMemberWithInfo) {
        val builder = com.bonfire.common.MemberWithInfo.newBuilder();
        builder.setMember(groupMemberWithInfo.getMember());
        if (groupMemberWithInfo.hasInfo()) {
            toAPI(groupMemberWithInfo.getInfo()).ifPresent(builder::setInfo);
        }
        return builder.build();
    }

    public static com.bonfire.common.Group toAPI(long groupId, GroupWithSelfMemberInfo groupWithSelfMemberInfo) {
        val groupBuilder = com.bonfire.common.Group.newBuilder();
        val group = groupWithSelfMemberInfo.getGroup();
        groupBuilder.setId(groupId);
        groupBuilder.setTitle(group.getTitle());
        groupBuilder.setMembersCount(group.getMembersCount());
        if (groupWithSelfMemberInfo.hasSelfMemberInfo()) {
            toAPI(groupWithSelfMemberInfo.getSelfMemberInfo()).ifPresent(groupBuilder::setSelfMemberInfo);
        }
        return groupBuilder.build();
    }

    public static Optional<com.bonfire.common.MemberInfo> toAPI(GroupMemberInfo memberInfo) {
        if (memberInfo.hasKickedAt() || memberInfo.hasLeftAt()) return Optional.empty();
        return Optional.of(
                com.bonfire.common.MemberInfo.newBuilder()
                        .setIsAdmin(memberInfo.getIsAdmin())
                        .setIsOwner(memberInfo.getIsOwner())
                        .build());
    }
}

package com.bonfire.cache.groups;

import com.bonfire.cache.helpers.ProtoBinaryWrapper;
import com.bonfire.internal.api.models.GroupMemberInfo;
import com.google.protobuf.Parser;

public class GroupMemberWrapper extends ProtoBinaryWrapper<GroupMemberInfo> {

    public GroupMemberWrapper() {
    }

    public GroupMemberWrapper(GroupMemberInfo value) {
        super(value);
    }

    @Override
    public Parser<GroupMemberInfo> parser() {
        return GroupMemberInfo.parser();
    }
}

package com.bonfire.cache.groups;

import com.bonfire.cache.helpers.ProtoBinaryWrapper;
import com.bonfire.internal.api.models.GroupMembers;
import com.google.protobuf.Parser;

public class GroupMembersWrapper extends ProtoBinaryWrapper<GroupMembers> {

    public GroupMembersWrapper() {

    }

    public GroupMembersWrapper(GroupMembers groupMembers) {
        super(groupMembers);
    }

    @Override
    public Parser<GroupMembers> parser() {
        return GroupMembers.parser();
    }
}

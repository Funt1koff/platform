package com.bonfire.cache.groups;

import com.bonfire.cache.helpers.ProtoBinaryWrapper;
import com.bonfire.internal.api.models.Group;
import com.google.protobuf.Parser;

public class GroupEntityWrapper extends ProtoBinaryWrapper<Group> {
    public GroupEntityWrapper() {
    }

    public GroupEntityWrapper(Group group) {
        super(group);
    }

    @Override
    public Parser<Group> parser() {
        return Group.parser();
    }
}

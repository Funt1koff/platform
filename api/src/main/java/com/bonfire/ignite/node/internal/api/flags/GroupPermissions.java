package com.bonfire.ignite.node.internal.api.flags;

import com.bonfire.internal.api.models.GroupPermission;
import com.google.protobuf.ByteString;
import lombok.val;

public class GroupPermissions extends IntFlagSet<GroupPermission> {

    public GroupPermissions(byte[] bytes) {
        super(bytes);
    }

    public GroupPermissions(ByteString bytes) {
        super(bytes);
    }

    public GroupPermissions(IntFlagSet.Builder<GroupPermission> builder) {
        super(builder);
    }

    public static GroupPermissions of(GroupPermission... values) {
        val builder = new IntFlagSet.Builder<GroupPermission>();
        for (val value : values) {
            builder.add(value);
        }
        return new GroupPermissions(builder);
    }

    public static GroupPermissions of(ByteString bytes) {
        return new GroupPermissions(bytes);
    }

    public static final GroupPermissions FULL = new GroupPermissions(
            new GroupPermissions.Builder<GroupPermission>().full(GroupPermission.class));
}

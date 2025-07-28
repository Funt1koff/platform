package com.bonfire.internal.api;


import com.bonfire.ignite.node.internal.api.flags.GroupPermissions;
import com.bonfire.internal.api.models.GroupMemberInfo;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GroupPermissionsHelper {

    public static GroupPermissions caclPermissions(GroupPermissions basePermissions, GroupMemberInfo member) {
        if (member.getIsAdmin()) {
            return GroupPermissions.FULL;
        }

        return new GroupPermissions(
                basePermissions.toBuilder()
                        .addAll(GroupPermissions.of(member.getAddedPermissions()))
                        .removeAll(GroupPermissions.of(member.getRemovedPermissions()))
        );
    }
}

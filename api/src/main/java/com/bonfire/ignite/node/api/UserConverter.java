package com.bonfire.ignite.node.api;

import lombok.experimental.UtilityClass;
import lombok.val;

import com.bonfire.common.User;

@UtilityClass
public class UserConverter {
    public static User toGeneralPublic(com.bonfire.internal.api.models.User user, long accessHash) {
        return User.newBuilder().setGeneral(toGeneralBuilderPublic(user).setAccessHash(accessHash)).build();
    }

    public static User.Self toSelfPublic(com.bonfire.internal.api.models.User user) {
        return toSelfBuilderPublic(user).build();
    }

    public static User.General.Builder toGeneralBuilderPublic(com.bonfire.internal.api.models.User user) {
        val generalUserBuilder = User.General.newBuilder()
                .setUserId(user.getUid())
                .setName(user.getName());
        if (user.hasUsername()) {
            generalUserBuilder.setUsername(user.getUsername());
        }
        if (user.hasProfilePhoto()) {
            generalUserBuilder.setProfilePhoto(ProfilePhotoConverter.toPublic(user.getProfilePhoto()));
        }
        return generalUserBuilder;
    }

    public static User.Self.Builder toSelfBuilderPublic(com.bonfire.internal.api.models.User user) {
        val generalUserBuilder = User.Self.newBuilder()
                .setUserId(user.getUid())
                .setRegistrationIncomplete(user.getLifeCycleStatus()
                        == com.bonfire.internal.api.models.User.LifeCycleStatus.INCOMPLETE_REGISTRATION)
                .setName(user.getName());
        if (user.hasUsername()) {
            generalUserBuilder.setUsername(user.getUsername());
        }
        if (user.hasProfilePhoto()) {
            generalUserBuilder.setProfilePhoto(ProfilePhotoConverter.toPublic(user.getProfilePhoto()));
        }
        return generalUserBuilder;
    }

    public static User toMinPublic(long userId, long accessHash) {
        val generalUserBuilder = User.Min.newBuilder()
                .setUserId(userId)
                .setAccessHash(accessHash);
        return User.newBuilder().setMin(generalUserBuilder).build();
    }

}

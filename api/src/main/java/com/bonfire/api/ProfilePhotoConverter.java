package com.bonfire.api;

import com.google.protobuf.BytesValue;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Status;
import lombok.experimental.UtilityClass;
import lombok.val;

import com.bonfire.account.AccountProfilePhoto;
import com.bonfire.account.GetProfilePhotosResponse;
import com.bonfire.internal.api.models.ProfilePhoto;
import com.bonfire.internal.api.services.GetUserProfilePhotosRequest;
import com.bonfire.internal.api.services.ProfilePhotoPageable;
import com.bonfire.users.GetUserProfilePhotosResponse;

@UtilityClass
public class ProfilePhotoConverter {

    public GetUserProfilePhotosResponse toPublic(com.bonfire.internal.api.services.GetUserProfilePhotosResponse source) {
        val publicRes = GetUserProfilePhotosResponse.newBuilder()
                .addAllProfilePhoto(source.getProfilePhotosList().stream()
                        .map(ProfilePhotoConverter::toPublic)
                        .toList());
        if (source.hasNext()) {
            publicRes.setLoadMoreState(BytesValue.of(source.getNext().toByteString()));
        }
        return publicRes.build();
    }

    public com.bonfire.common.ProfilePhoto toPublic(ProfilePhoto source) {
        val result = com.bonfire.common.ProfilePhoto.newBuilder();
        if (source.hasFull()) {
            result.setFull(source.getFull());
        }
        if (source.hasLarge()) {
            result.setLarge(source.getLarge());
        }
        if (source.hasSmall()) {
            result.setSmall(source.getSmall());
        }
        return result.build();
    }

    public GetProfilePhotosResponse toAccountPublic(com.bonfire.internal.api.services.GetUserProfilePhotosResponse source) {
        val publicRes = GetProfilePhotosResponse.newBuilder()
                .addAllAccountProfilePhoto(source.getProfilePhotosList().stream()
                        .map(ProfilePhotoConverter::toAccountPublic)
                        .toList());
        if (source.hasNext()) {
            publicRes.setLoadMoreState(BytesValue.of(source.getNext().toByteString()));
        }
        return publicRes.build();
    }

    public AccountProfilePhoto toAccountPublic(ProfilePhoto source) {
        return AccountProfilePhoto.newBuilder()
                .setProfilePhoto(toPublic(source))
                .setEditedAt(source.getEditedAt())
                .setId(source.getId())
                .build();
    }

    public GetUserProfilePhotosRequest toInternal(com.bonfire.users.GetUserProfilePhotosRequest source, long userId) {
        val reqBuilder = GetUserProfilePhotosRequest.newBuilder()
                .setUserId(userId)
                .setLimit(source.getLimit());
        if (source.hasLoadMoreState()) {
            try {
                reqBuilder.setFrom(ProfilePhotoPageable.parseFrom(source.getLoadMoreState().getValue()));
            } catch (InvalidProtocolBufferException e) {
                throw Status.INVALID_ARGUMENT.withDescription(
                        STR."Invalid LoadMoreState \{source.getLoadMoreState().getValue()}").asRuntimeException();
            }
        }
        return reqBuilder.build();
    }

    public GetUserProfilePhotosRequest toInternal(com.bonfire.account.GetProfilePhotosRequest source, long userId) {
        val reqBuilder = GetUserProfilePhotosRequest.newBuilder()
                .setUserId(userId)
                .setLimit(source.getLimit());
        if (source.hasLoadMoreState()) {
            try {
                reqBuilder.setFrom(ProfilePhotoPageable.parseFrom(source.getLoadMoreState().getValue()));
            } catch (InvalidProtocolBufferException e) {
                throw Status.INVALID_ARGUMENT.withDescription(
                        STR."Invalid LoadMoreState \{source.getLoadMoreState().getValue()}").asRuntimeException();
            }
        }
        return reqBuilder.build();
    }
}

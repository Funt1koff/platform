package com.bonfire.cache.helpers;

import com.bonfire.cache.ProfilePhotos;
import com.google.protobuf.Parser;

public class ProfilePhotosBinaryWrapper extends ProtoBinaryWrapper<ProfilePhotos> {

    public ProfilePhotosBinaryWrapper(ProfilePhotos value) {
        super(value);
    }

    @Override
    public Parser<ProfilePhotos> parser() {
        return ProfilePhotos.parser();
    }
}

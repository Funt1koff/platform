package com.bonfire.cache.helpers;

import com.bonfire.cache.UserEntry;
import com.google.protobuf.Parser;

public class UserEntryBinaryWrapper extends ProtoBinaryWrapper<UserEntry> {

    public UserEntryBinaryWrapper(UserEntry entry) {
        super(entry);
    }

    @Override
    public Parser<UserEntry> parser() {
        return UserEntry.parser();
    }
}

package com.bonfire.cache.helpers;

import com.bonfire.cache.UserLastSeen;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;

public class UserLastSeenBinaryWrapper {
    private final BinaryObjectBuilder b;

    public UserLastSeenBinaryWrapper(BinaryObject object) {
        this.b = object.toBuilder();
    }

    public UserLastSeenBinaryWrapper(BinaryObjectBuilder builder) {
        this.b = builder;
    }

    public static UserLastSeenBinaryWrapper init(BinaryObjectBuilder builder) {
        return new UserLastSeenBinaryWrapper(builder);
    }

    public BinaryObject build() {
        return b.build();
    }

    public long getLastSeen() {
        return b.getField(UserLastSeen.Fields.last_seen);
    }

    public void setLastSeen(long value) {
        b.setField(UserLastSeen.Fields.last_seen, value);
    }

}

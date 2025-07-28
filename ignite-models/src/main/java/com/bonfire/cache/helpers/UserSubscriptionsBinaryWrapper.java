package com.bonfire.cache.helpers;

import com.bonfire.cache.UserSubscriptions;
import com.bonfire.cache.utils.SortedLongArray;
import lombok.val;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;

public class UserSubscriptionsBinaryWrapper {
    private final BinaryObjectBuilder b;

    public UserSubscriptionsBinaryWrapper(BinaryObject object) {
        this.b = object.toBuilder();
    }

    public UserSubscriptionsBinaryWrapper(BinaryObjectBuilder builder) {
        this.b = builder;
    }

    public static UserSubscriptionsBinaryWrapper init(BinaryObjectBuilder builder) {
        return new UserSubscriptionsBinaryWrapper(builder);
    }

    public BinaryObject build() {
        return b.build();
    }

    public long[] getSubscriptions() {
        return b.getField(UserSubscriptions.Fields.subscriptions);
    }

    public void setSubscriptions(long[] val) {
        b.setField(UserSubscriptions.Fields.subscriptions, val);
    }

    public void addLastSeen(long value) {
        val inserted = SortedLongArray.insert(getSubscriptions(), value);
        inserted.ifPresent(this::setSubscriptions);
    }
}

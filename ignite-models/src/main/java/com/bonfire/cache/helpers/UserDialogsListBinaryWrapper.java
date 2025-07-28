package com.bonfire.cache.helpers;

import com.bonfire.cache.UserDialogsList;
import com.bonfire.cache.utils.SortedLongArray;
import lombok.val;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;

public class UserDialogsListBinaryWrapper implements ReadOnlyUserDialogsListBinaryWrapper {
    private final BinaryObjectBuilder b;

    public UserDialogsListBinaryWrapper(BinaryObject object) {
        this.b = object.toBuilder();
    }

    public UserDialogsListBinaryWrapper(BinaryObjectBuilder builder) {
        this.b = builder;
    }

    public static UserDialogsListBinaryWrapper init(BinaryObjectBuilder builder) {
        val wrapper = new UserDialogsListBinaryWrapper(builder);
        wrapper.setSize(0);
        return wrapper;
    }

    public BinaryObject build() {
        return b.build();
    }

    public ReadOnlyUserDialogsListBinaryWrapper ReadOnly() {
        return this;
    }

    public int getSize(BinaryObjectBuilder o) {
        return o.getField(UserDialogsList.Fields.size);
    }

    public void setSize(int value) {
        b.setField(UserDialogsList.Fields.size, value);
    }

    public long[] getP2pIds() {
        long[] field = b.getField(UserDialogsList.Fields.p2pUids);
        if (field == null) {
            return new long[0];
        }
        return field;
    }

    private void setP2pIds(long[] value) {
        b.setField(UserDialogsList.Fields.p2pUids, value);
    }

    public long[] getGroupIds() {
        long[] field = b.getField(UserDialogsList.Fields.groupIds);
        if (field == null) {
            return new long[0];
        }
        return field;
    }

    private void setGroupIds(long[] value) {
        b.setField(UserDialogsList.Fields.groupIds, value);
    }

    public boolean addGroupId(long groupId) {
        val inserted = SortedLongArray.insert(getGroupIds(), groupId);
        inserted.ifPresent(this::setGroupIds);
        return inserted.isPresent();
    }

    public long[] getChannelIds() {
        long[] field = b.getField(UserDialogsList.Fields.channelIds);
        if (field == null) {
            return new long[0];
        }
        return field;
    }

    private void setChannelIds(long[] value) {
        b.setField(UserDialogsList.Fields.channelIds, value);
    }

    public boolean addChannelId(long channelId) {
        val inserted = SortedLongArray.insert(getChannelIds(), channelId);
        inserted.ifPresent(this::setChannelIds);
        return inserted.isPresent();
    }

    public boolean addP2pId(long p2pId) {
        val inserted = SortedLongArray.insert(getP2pIds(), p2pId);
        inserted.ifPresent(this::setP2pIds);
        return inserted.isPresent();
    }
}

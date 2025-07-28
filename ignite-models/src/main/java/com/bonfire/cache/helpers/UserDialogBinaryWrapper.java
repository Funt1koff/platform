package com.bonfire.cache.helpers;

import com.bonfire.cache.dialogs.UserDialog;
import com.bonfire.internal.api.models.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;

import java.util.Arrays;
import java.util.Optional;

public class UserDialogBinaryWrapper implements ReadonlyUserDialogBinaryWrapper {
    private final BinaryObjectBuilder b;

    public UserDialogBinaryWrapper(BinaryObject object) {
        this.b = object.toBuilder();
    }

    public UserDialogBinaryWrapper(BinaryObjectBuilder builder) {
        this.b = builder;
    }

    public static UserDialogBinaryWrapper init(BinaryObjectBuilder builder, int initMsgId) {
        val wrapper = new UserDialogBinaryWrapper(builder);
        wrapper.setTopMsgId_(initMsgId);
        wrapper.setReadInboxMaxMsgId_(initMsgId);
        wrapper.setReadOutboxMaxMsgId(initMsgId);
        wrapper.recalcUnreadMsgs();
        return wrapper;
    }

    public BinaryObject build() {
        return b.build();
    }

    public ReadonlyUserDialogBinaryWrapper ReadOnly() {
        return this;
    }

    @Override
    public Integer getReadInboxMaxMsgId() {
        return b.getField(UserDialog.Fields.readInboxMaxMsgId);
    }

    public void setReadInboxMaxMsgId(int readInboxMsgId) {
        setReadInboxMaxMsgId_(readInboxMsgId);
        recalcUnreadMsgs();
    }

    public void setReadInboxMaxMsgId_(int readInboxMsgId) {
        b.setField(UserDialog.Fields.readInboxMaxMsgId, readInboxMsgId);
    }

    @Override
    public Integer getReadOutboxMaxMsgId() {
        return b.getField(UserDialog.Fields.readOutboxMaxMsgId);
    }

    public void setReadOutboxMaxMsgId(int readOutboxMsgId) {
        b.setField(UserDialog.Fields.readOutboxMaxMsgId, readOutboxMsgId);
    }

    @Override
    public int[] getUnreadDeletedMsgIds() {

        int[] data = b.getField(UserDialog.Fields.unreadDeletedMsgsIds);
        if (data == null) {
            return new int[0];
        }
        if (!ArrayUtils.isSorted(data)) {
            Arrays.sort(data);
        }
        return data;
    }

    public void setUnreadDeletedMsgIds(int[] newUnreadDeletedMsgIds) {
        b.setField(UserDialog.Fields.unreadDeletedMsgsIds, newUnreadDeletedMsgIds);
        recalcUnreadMsgs();
    }

    @Override
    public int getTopMsgId() {
        return b.getField(UserDialog.Fields.topMsgId);
    }

    public void setTopMsgId(int value) {
        setTopMsgId_(value);
        recalcUnreadMsgs();
    }

    private void setTopMsgId_(int value) {
        b.setField(UserDialog.Fields.topMsgId, value);
    }

    @Override
    public int getUnreadMsgsCount() {
        return b.getField(UserDialog.Fields.unreadMsgsCount);
    }

    private void setUnreadMsgsCount(int value) {
        b.setField(UserDialog.Fields.unreadMsgsCount, value);
    }

    @Override
    public Optional<Message> getTopMessage() {
        byte[] data = b.getField(UserDialog.Fields.topMessageProto);
        if (data == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Message.parseFrom(data));
        } catch (InvalidProtocolBufferException e) {
            return Optional.empty();
        }
    }

    public void setTopMessage(Optional<Message> value) {
        if (value.isEmpty()) {
            b.setField(UserDialog.Fields.topMessageProto, null);
        } else {
            b.setField(UserDialog.Fields.topMessageProto, value.get().toByteArray());
        }
    }

    private void recalcUnreadMsgs() {
        int currentTopMsgId = getTopMsgId();
        int readInboxMaxMsgId = getReadInboxMaxMsgId();
        int[] deletedMsgIds = getUnreadDeletedMsgIds();
        int deletedMsgIdsCount = deletedMsgIds == null ? 0 : deletedMsgIds.length;
        setUnreadMsgsCount(Math.max(0, currentTopMsgId - readInboxMaxMsgId - deletedMsgIdsCount));
    }

}

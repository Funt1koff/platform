package com.bonfire.cache.helpers;

import com.bonfire.cache.SeqUpdatesState;
import lombok.val;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;

public class SeqUpdatesStateBinaryWrapper {

    private final BinaryObjectBuilder b;

    public SeqUpdatesStateBinaryWrapper(BinaryObject object) {
        this.b = object.toBuilder();
    }

    public SeqUpdatesStateBinaryWrapper(BinaryObjectBuilder b) {
        this.b = b;
    }

    public static SeqUpdatesStateBinaryWrapper init(BinaryObjectBuilder builder) {
        val wrapper = new SeqUpdatesStateBinaryWrapper(builder);
        wrapper.setLastSeqNum(0);
        wrapper.setMinSeqNum(0);
        return wrapper;
    }

    public BinaryObject build() {
        return b.build();
    }

    public int getLastSeqNum() {
        return b.getField(SeqUpdatesState.Fields.lastSeqNum);
    }

    public void setLastSeqNum(int lastSeqNum) {
        b.setField(SeqUpdatesState.Fields.lastSeqNum, lastSeqNum);
    }

    public void incLastSeqNum() {
        setLastSeqNum(getLastSeqNum() + 1);
    }

    public int getMinSeqNum() {
        return b.getField(SeqUpdatesState.Fields.minSeqNum);
    }

    public void setMinSeqNum(int minSeqNum) {
        b.setField(SeqUpdatesState.Fields.minSeqNum, minSeqNum);
    }
}

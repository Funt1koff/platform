package com.bonfire.cache.helpers;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.val;
import org.apache.ignite.binary.BinaryObjectException;
import org.apache.ignite.binary.BinaryReader;
import org.apache.ignite.binary.BinaryWriter;
import org.apache.ignite.binary.Binarylizable;

public abstract class ProtoBinaryWrapper<T extends GeneratedMessageV3> implements Binarylizable {

    private T value;

    public final T getValue() {
        return value;
    }

    protected ProtoBinaryWrapper() {
    }

    protected ProtoBinaryWrapper(T value) {
        this.value = value;
    }

    @Override
    public void writeBinary(BinaryWriter binaryWriter) throws BinaryObjectException {
        binaryWriter.rawWriter().writeByteArray(value.toByteArray());
    }

    @Override
    public void readBinary(BinaryReader binaryReader) throws BinaryObjectException {
        val bytes = binaryReader.rawReader().readByteArray();
        try {
            this.value = parser().parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            throw new BinaryObjectException(e);
        }
    }

    public abstract com.google.protobuf.Parser<T> parser();
}

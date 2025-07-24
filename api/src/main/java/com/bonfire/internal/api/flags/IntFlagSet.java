package com.bonfire.internal.api.flags;

import com.google.protobuf.ByteString;

import java.util.BitSet;

public class IntFlagSet<T extends Enum<T>> {

    private final BitSet bits;

    protected IntFlagSet(byte[] bytes) {
        this.bits = BitSet.valueOf(bytes);
    }

    protected IntFlagSet(ByteString bytes) {
        this.bits = BitSet.valueOf(bytes.asReadOnlyByteBuffer());
    }

    protected IntFlagSet(Builder<T> builder) {
        this.bits = BitSet.valueOf(bytes());
    }

    public boolean has(T permission) {
        return bits.get(1 << permission.ordinal());
    }

    public Builder<T> toBuilder() {
        return new Builder<>(BitSet.valueOf(bits.toByteArray()));
    }

    public byte[] bytes() {
        return bits.toByteArray();
    }

    public ByteString ByteString() {
        return ByteString.copyFrom(bits.toByteArray());
    }

    public static class Builder<T extends Enum<T>> {
        private final BitSet bits;

        public Builder() {
            bits = new BitSet(32);
        }

        public Builder(BitSet bitSet) {
            this.bits = bitSet;
        }

        public boolean has(T permission) {
            return bits.get(1 << permission.ordinal());
        }

        public Builder<T> add(T permission) {
            bits.set(1 << permission.ordinal());
            return this;
        }

        public Builder<T> addAll(IntFlagSet<T> flags) {
            bits.or(flags.bits);
            return this;
        }

        public Builder<T> removeAll(IntFlagSet<T> flags) {
            bits.andNot(flags.bits);
            return this;
        }

        public Builder<T> remove(T permission) {
            bits.clear(1 << permission.ordinal());
            return this;
        }

        public byte[] bytes() {
            return bits.toByteArray();
        }

        public ByteString toByteString() {
            return ByteString.copyFrom(bits.toByteArray());
        }

        public Builder<T> full(Class<T> clazz) {
            for (T option : clazz.getEnumConstants()) {
                this.add(option);
            }
            return this;
        }
    }
}

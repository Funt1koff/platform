package com.bonfire.cache;

import com.bonfire.cache.utils.BinaryObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.apache.ignite.IgniteBinary;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.client.IgniteClient;

@Data
@FieldNameConstants
public class UserDialogsKey {

    @AffinityKeyMapped
    private final long selfUId;

    public static class BinaryHelper {
        private final IgniteBinary binary;

        public BinaryHelper(IgniteClient igniteClient) {
            binary = igniteClient.binary();
        }

        public BinaryObjectBuilder initBuilder() {
            return this.binary.builder(UserDialogsKey.class.getName());
        }
    }

    public static class Wrapper {
        private final BinaryObjectBuilder binary;

        @Getter
        private final long selfUId;

        public Wrapper(BinaryHelper binaryHelper, long selfUId) {
            this.binary = binaryHelper.initBuilder();
            this.binary.setField(Fields.selfUId, selfUId);
            this.selfUId = selfUId;
        }

        public Wrapper(BinaryObjectBuilder binaryObjectBuilder) {
            this.binary = binaryObjectBuilder;
            this.selfUId = binaryObjectBuilder.getField(Fields.selfUId);
        }

        public Wrapper(BinaryObject binaryObject) {
            this(binaryObject.toBuilder());
        }

        public BinaryObject build() {
            return binary.build();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Wrapper wrapper = (Wrapper) o;
            return selfUId == wrapper.selfUId;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(selfUId);
        }

        @Override
        public String toString() {
            return STR."UserDialogsKey.Wrapper(\{selfUId})";
        }
    }

    public BinaryObjectMapper<Wrapper> Mapper = new BinaryObjectMapper<Wrapper>() {
        @Override
        public BinaryObject to(Wrapper wrapper) {
            return wrapper.build();
        }

        @Override
        public Wrapper from(BinaryObject binaryObject) {
            return new Wrapper(binaryObject);
        }
    };
}

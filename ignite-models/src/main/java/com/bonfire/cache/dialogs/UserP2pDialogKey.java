package com.bonfire.cache.dialogs;

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
public class UserP2pDialogKey {
    @AffinityKeyMapped
    private final long selfUId;
    private final long mateUid;

    public static class BinaryHelper {
        private final IgniteBinary binary;

        public BinaryHelper(IgniteClient ignite) {
            binary = ignite.binary();
        }

        public BinaryObjectBuilder initBuilder() {
            return this.binary.builder(UserP2pDialogKey.class.getName());
        }
    }

    public static class Wrapper {
        private final BinaryObjectBuilder b;
        @Getter
        private final long selfUid;
        @Getter
        private final long mateUid;

        public Wrapper(BinaryHelper helper, long selfUid, long mateUid) {

            this.selfUid = selfUid;
            this.mateUid = mateUid;
            this.b = helper.initBuilder();
            this.b.setField(Fields.selfUId, selfUid);
            this.b.setField(Fields.mateUid, mateUid);
        }

        public Wrapper(BinaryObject object) {
            this(object.toBuilder());
        }

        public Wrapper(BinaryObjectBuilder builder) {
            this.b = builder;
            this.selfUid = b.getField(Fields.selfUId);
            this.mateUid = b.getField(Fields.mateUid);
        }

        public BinaryObject build() {
            return b.build();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Wrapper wrapper = (Wrapper) o;
            return selfUid == wrapper.selfUid && mateUid == wrapper.mateUid;
        }

        @Override
        public int hashCode() {
            int result = Long.hashCode(selfUid);
            result = 31 * result + Long.hashCode(mateUid);
            return result;
        }

        @Override
        public String toString() {
            return STR."UserP2pDialogKey.Wrapper(\{selfUid}, \{mateUid})";
        }
    }

    public static BinaryObjectMapper<Wrapper> Mapper = new BinaryObjectMapper<Wrapper>() {
        @Override
        public BinaryObject to(Wrapper wrapper) {
            return wrapper.build();
        }

        @Override
        public Wrapper from(BinaryObject b) {
            return new Wrapper(b);
        }
    };
}

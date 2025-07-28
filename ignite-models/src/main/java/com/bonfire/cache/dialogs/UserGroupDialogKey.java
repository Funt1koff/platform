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
public class UserGroupDialogKey {
    @AffinityKeyMapped
    private final long selfUId;
    private final long groupId;

    public static class BinaryHelper {
        private final IgniteBinary binary;

        public BinaryHelper(IgniteClient ignite) {
            binary = ignite.binary();
        }

        public BinaryObjectBuilder initBuilder() {
            return this.binary.builder(UserGroupDialogKey.class.getName());
        }
    }

    public static class Wrapper {
        private final BinaryObjectBuilder b;
        @Getter
        private final long selfUid;
        @Getter
        private final long groupId;

        public Wrapper(BinaryHelper helper, long selfUid, long groupId) {
            this.b = helper.initBuilder();
            this.selfUid = selfUid;
            this.groupId = groupId;
            b.setField(UserGroupDialogKey.Fields.selfUId, selfUid);
            b.setField(Fields.groupId, groupId);
        }

        public Wrapper(BinaryObject object) {
            this(object.toBuilder());
        }

        public Wrapper(BinaryObjectBuilder builder) {
            this.b = builder;
            this.selfUid = b.getField(UserGroupDialogKey.Fields.selfUId);
            this.groupId = b.getField(UserGroupDialogKey.Fields.groupId);
        }

        public BinaryObject build() {
            return b.build();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Wrapper wrapper = (Wrapper) o;
            return selfUid == wrapper.selfUid && groupId == wrapper.groupId;
        }

        @Override
        public int hashCode() {
            int result = Long.hashCode(selfUid);
            result = 31 * result + Long.hashCode(groupId);
            return result;
        }

        @Override
        public String toString() {
            return STR."UserGroupDialogKey.Wrapper(\{selfUid}, \{groupId})";
        }
    }

    public static BinaryObjectMapper<Wrapper> Mapper = new BinaryObjectMapper<>() {
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

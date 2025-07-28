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
public class UserChannelDialogKey {

    @AffinityKeyMapped
    private final long selfUId;
    private final long channelId;

    public static class BinaryHelper {
        private final IgniteBinary binary;

        public BinaryHelper(IgniteClient ignite) {
            binary = ignite.binary();
        }

        public BinaryObjectBuilder igniteBuilder() {
            return this.binary.builder(UserChannelDialogKey.class.getName());
        }
    }

    public static class Wrapper {
        private final BinaryObjectBuilder b;
        @Getter
        private final long selfUId;
        @Getter
        private final long channelId;

        public Wrapper(BinaryHelper helper, long selfUId, long channelId) {
            this.b = helper.igniteBuilder();
            this.b.setField(Fields.selfUId, selfUId);
            this.b.setField(Fields.channelId, channelId);
            this.selfUId = selfUId;
            this.channelId = channelId;
        }

        public Wrapper(BinaryObjectBuilder b) {
            this.b = b;
            this.selfUId = b.getField(Fields.selfUId);
            this.channelId = b.getField(Fields.channelId);
        }

        public Wrapper(BinaryObject object) {
            this(object.toBuilder());
        }

        public BinaryObject build() {
            return b.build();
        }

        @Override
        public int hashCode() {
            int result = Long.hashCode(selfUId);
            result = 31 * result + Long.hashCode(channelId);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Wrapper wrapper = (Wrapper) obj;
            return selfUId == wrapper.selfUId && channelId == wrapper.channelId;
        }

        @Override
        public String toString() {
            return STR."UserChannelDialogKey.Wrapper(\{selfUId}, \{channelId})";
        }
    }

    public static BinaryObjectMapper<Wrapper> Mapper = new BinaryObjectMapper<Wrapper>() {
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

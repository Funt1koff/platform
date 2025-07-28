package com.bonfire.cache;

import com.bonfire.cache.utils.BinaryObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.apache.ignite.IgniteBinary;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.apache.ignite.cache.affinity.AffinityKeyMapped;

import java.util.Objects;

@Data
@FieldNameConstants
public class FileUploadKey {
    @AffinityKeyMapped
    private final long userId;
    private final String uploadKey;

    public static class BinaryHelper {
        private final IgniteBinary binary;

        public BinaryHelper(IgniteBinary binary) {
            this.binary = binary;
        }

        public BinaryObjectBuilder initBuilder() {
            return this.binary.builder(FileUploadKey.class.getName());
        }
    }

    public static class Wrapper {
        private final BinaryObjectBuilder binaryObjectBuilder;
        @Getter
        private final long userId;
        @Getter
        private final String uploadKey;

        public Wrapper(BinaryHelper helper, long userId, String uploadKey) {
            this.binaryObjectBuilder = helper.initBuilder();
            this.binaryObjectBuilder.setField(Fields.userId, userId);
            this.binaryObjectBuilder.setField(Fields.uploadKey, uploadKey);
            this.userId = userId;
            this.uploadKey = uploadKey;
        }

        public Wrapper(BinaryObjectBuilder binaryObjectBuilder) {
            this.binaryObjectBuilder = binaryObjectBuilder;
            this.userId = binaryObjectBuilder.getField(Fields.userId);
            this.uploadKey = binaryObjectBuilder.getField(Fields.uploadKey);
        }

        public Wrapper(BinaryObject binaryObject) {
            this(binaryObject.toBuilder());
        }

        public BinaryObject build() {
            return binaryObjectBuilder.build();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Wrapper wrapper = (Wrapper) o;
            return userId == wrapper.getUserId() && uploadKey.equals(wrapper.uploadKey);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, uploadKey);
        }

        @Override
        public String toString() {
            return STR."FileUploadKey.Wrapper(\{userId}, \{uploadKey})";
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

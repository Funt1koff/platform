package com.bonfire.exception;

import com.bonfire.errors.ErrorInfo;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.SneakyThrows;
import lombok.val;

public class ApiException extends StatusRuntimeException {
    private static final Metadata.Key<ErrorInfo> ERROR_INFO_METADATA_KEY = Metadata.Key.of("error-info-bin", new Metadata.BinaryMarshaller<ErrorInfo>() {
        @Override
        public byte[] toBytes(ErrorInfo errorInfo) {
            return errorInfo.toByteArray();
        }

        @SneakyThrows
        @Override
        public ErrorInfo parseBytes(byte[] bytes) {
            return ErrorInfo.parseFrom(bytes);
        }
    });

    private static Metadata toMetadata(ErrorInfo errorInfo) {
        val metadata = new Metadata();
        metadata.put(ERROR_INFO_METADATA_KEY, errorInfo);
        return metadata;
    }

    public ApiException(Status status, ErrorInfo errorInfo) {
        super(status, toMetadata(errorInfo));
    }
}

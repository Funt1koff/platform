package com.bonfire.files.api;

import com.google.protobuf.Timestamp;
import io.smallrye.mutiny.Uni;
import proto.FileInfoChanges;
import proto.HTTPResponse;
import proto.HookResponse;
import com.bonfire.cache.FileUploadEntry;

import java.time.Instant;

public class Mappers {

    public static final HookResponse OK = HookResponse.newBuilder()
            .setHttpResponse(HTTPResponse.newBuilder().setStatusCode(200))
            .build();

    private static final HookResponse UNAUTHORIZED = HookResponse.newBuilder()
            .setHttpResponse(HTTPResponse.newBuilder().setStatusCode(400).setBody("Unauthorized"))
            .build();

    public static Uni<HookResponse> toResponse(TUSHookException.Unauthorized e) {
        return Uni.createFrom().item(UNAUTHORIZED);
    }

    public static Uni<HookResponse> toResponse(TUSHookException.BadRequest ex) {
        return Uni.createFrom().item(
                HookResponse.newBuilder()
                        .setHttpResponse(
                                HTTPResponse.newBuilder()
                                        .setStatusCode(400)
                                        .setBody(ex.getMessage())
                        ).build()
        );
    }

    public static HookResponse toResponse(FileUploadEntry upload) {
        return HookResponse.newBuilder()
                .setHttpResponse(
                        HTTPResponse.newBuilder()
                                .setStatusCode(200)
                )
                .setChangeFileInfo(
                        FileInfoChanges.newBuilder()
                                .setId(upload.getFileId())
                )
                .build();
    }

    public static Timestamp toTimestamp(Instant instant) {
        return toTimestampBuilder(instant).build();
    }

    public static Timestamp.Builder toTimestampBuilder(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano());
    }

    public static Instant toInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}

package com.bonfire.files.api;

import com.bonfire.files.*;
import com.bonfire.files.service.FilesService;
import com.bonfire.files.util.InitiatorUtil;
import com.bonfire.internal.api.extensions.InitiatorExtensionHelper;
import com.bonfire.internal.api.models.Initiator;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.val;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@GrpcService
@RunOnVirtualThread
@Singleton
public class FilesPublicGrpc extends MutinyFilesGrpc.FilesImplBase {

    @Inject
    private FilesService filesService;

    @ConfigProperty(name = "files.max.name")
    private Long maxNameLength;

    @ConfigProperty(name = "files.max.batch")
    private Integer maxBatchSize;

    @Override
    public Uni<GetFileUrlBatchResponse> getFileUrls(GetFileUrlBatchRequest request) {
        authorized(request);

        if (request.getFilesCount() > maxBatchSize) {
            throw Status.INVALID_ARGUMENT.withDescription("Batch size to big").asRuntimeException();
        }

        val locations = request.getFilesList();
        val urls = locations.stream()
                .map(l -> Tuple2.of(l.getFileId(), filesService.getFileUrlDescriptor(l.getFileId(), l.getAccessHash())))
                .toList();

        val response = GetFileUrlBatchResponse.newBuilder();

        for (val u : urls) {
            val id = u.getItem1();
            val url = u.getItem2();
            if (url.isEmpty()) {
                response.addErrors(FileUrlError.newBuilder().setFileId(id).setTag(FileUrlErrorTag.FILE_URL_ERROR_TAG_STORAGE_ERROR));
            } else {
                response.addFileUrls(FileUrlDescriptor.newBuilder().setUrl(url.get().getUrl()).setTimeout(url.get().getTimeout()).build());
            }
        }

        return Uni.createFrom().item(response.build());
    }

    @Override
    public Uni<GetFileUrlResponse> getFileUrl(GetFileUrlRequest request) {
        authorized(request);

        val fileId = request.getFile().getFileId();
        val accessHash = request.getFile().getAccessHash();

        val fileUrlDescriptor = filesService
                .getFileUrlDescriptor(fileId, accessHash)
                .orElseThrow(() -> Status.NOT_FOUND.withDescription("File Not Found").asRuntimeException());

        val res = GetFileUrlResponse.newBuilder()
                .setFileUrl(fileUrlDescriptor)
                .build();

        return Uni.createFrom().item(res);
    }

    @Override
    public Uni<CommitFileUploadResponse> commitFileUpload(CommitFileUploadRequest request) {
        val initiator = authorized(request);
        val initiatorId = InitiatorUtil.callerId(initiator);

        val uploadKey = request.getUploadKey();
        val checkSum = request.getHash();
        val fileName = request.getFileName();
        val mime = request.getMimeType();

        if (fileName.length() > maxNameLength) {
            throw Status.INVALID_ARGUMENT.withDescription("File name too long").asRuntimeException();
        }

        InitiatorUtil.callerId(initiator);
        CommitFileUploadResponse response = filesService
                .completeUpload(
                        initiatorId,
                        uploadKey,
                        fileName,
                        mime,
                        checkSum);

        return Uni.createFrom().item(response);
    }

    private <T extends GeneratedMessageV3> Initiator authorized(T request) {
        return InitiatorExtensionHelper
                .getFromMessage(request)
                .orElseThrow(Status.UNAUTHENTICATED::asRuntimeException);
    }
}

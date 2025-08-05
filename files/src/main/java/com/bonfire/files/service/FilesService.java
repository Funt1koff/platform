package com.bonfire.files.service;

import com.bonfire.cache.FileUploadEntry;
import com.bonfire.cache.FileUploadKey;
import com.bonfire.cache.helpers.FileUploadBinaryWrapper;
import com.bonfire.files.*;
import com.bonfire.files.s3.S3ClientWrapper;
import com.bonfire.files.s3.S3Config;
import com.bonfire.files.storage.FilesRepo;
import com.bonfire.files.util.InitiatorUtil;
import com.bonfire.ignite.Caches;
import com.bonfire.internal.api.models.Initiator;
import com.bonfire.internal.api.services.AvpoStatus;
import com.bonfire.internal.api.services.File;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.val;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;

import java.time.Instant;
import java.util.Optional;
import java.util.Random;

import static com.bonfire.files.api.Mappers.toTimestampBuilder;

@ApplicationScoped
public class FilesService {
    private static final CommitFileUploadError UPLOAD_NOT_FOUND = CommitFileUploadError.newBuilder().setTag(CommitFileUploadErrorTag.COMMIT_FILE_UPLOAD_NOT_FOUND).build();
    private static final CommitFileUploadError UPLOAD_NOT_READY = CommitFileUploadError.newBuilder().setTag(CommitFileUploadErrorTag.COMMIT_FILE_UPLOAD_NOT_READY).build();

    private final S3ClientWrapper s3ClientWrapper;
    private final FilesRepo filesRepo;
    private final S3Config s3Config;

    private final Random random = new Random();
    private final ClientCache<FileUploadKey, FileUploadBinaryWrapper> fileUploadsCache;

    @Inject
    public FilesService(S3ClientWrapper s3ClientWrapper, FilesRepo filesRepo, S3Config s3Config, IgniteClient igniteClient) {
        this.s3ClientWrapper = s3ClientWrapper;
        this.filesRepo = filesRepo;
        this.s3Config = s3Config;
        this.fileUploadsCache = Caches.uploadsCache(igniteClient);
    }

    public Optional<FileUrlDescriptor> getFileUrlDescriptor(String fileId, Long accessHash) {
        val storedFile = filesRepo.get(fileId);

        return storedFile.filter(f -> f.getAccessHash() == accessHash)
                .map(f -> Tuple2.of(f, s3ClientWrapper.getDownloadUrl(f.getId())))
                .map(t -> toDescriptor(t.getItem1(), t.getItem2()));
    }

    public CommitFileUploadResponse completeUpload(Long userId, String uploadKey, String fileName, String mime, ByteString checksum) {
        val key = new FileUploadKey(userId, uploadKey);
        val uploadWrapper = fileUploadsCache.get(key);

        if (uploadWrapper == null) {
            return CommitFileUploadResponse.newBuilder().setError(UPLOAD_NOT_FOUND).build();
        }

        val upload = uploadWrapper.getValue();
        if (!upload.getReady()) {
            return CommitFileUploadResponse.newBuilder().setError(UPLOAD_NOT_READY).build();
        }

        val accessHash = random.nextLong();
        val fileId = upload.getFileId();
        val now = Instant.now();

        val file = File.newBuilder()
                .setId(fileId)
                .setFileName(fileName)
                .setMimeType(mime)
                .setUploadedAt(toTimestampBuilder(now))
                .setAccessHash(accessHash)
                .setCheckSum(checksum)
                .setDcId(1)
                .setAvpoStatus(AvpoStatus.AVPO_STATUS_NOT_REQUIRED)
                .setServerCheckSum(ByteString.EMPTY)
                .setReferenceCount(0)
                .build();

        filesRepo.insert(file);
        fileUploadsCache.remove(key);

        val location = FileLocation.newBuilder()
                .setFileId(fileId)
                .setAccessHash(accessHash)
                .build();

        return CommitFileUploadResponse.newBuilder().setUploadedFileLocation(location).buildPartial();
    }

    public Uni<FileUploadEntry> createUpload(Initiator initiator, String uploadKey) {
        val initiatorId = InitiatorUtil.callerId(initiator);
        val key = new FileUploadKey(initiatorId, uploadKey);
        val fileId = Uuids.timeBased().toString();
        val upload = FileUploadEntry.newBuilder()
                .setFileId(fileId)
                .build();

        return Uni.createFrom().future(() ->
                        fileUploadsCache.putAsync(key, new FileUploadBinaryWrapper(upload)))
                .replaceWith(upload);
    }

    public Uni<Void> updateUploadStatus(String uploadKey, boolean ready, Initiator initiator) {
        return Uni.createFrom().future(() -> {
            val initiatorId = InitiatorUtil.callerId(initiator);
            val userKey = new FileUploadKey(initiatorId, uploadKey);
            val uploadEntry = fileUploadsCache.get(userKey); //async?

            if (uploadEntry == null) {
                throw Status.NOT_FOUND.withDescription("Not Found").asRuntimeException();
            }
            val upload = uploadEntry.getValue();

            val updated = upload.toBuilder().setReady(ready).build();
            return fileUploadsCache.putAsync(userKey, new FileUploadBinaryWrapper(updated));
        });
    }

    private FileUrlDescriptor toDescriptor(File file, String url) {
        return FileUrlDescriptor.newBuilder()
                .setUrl(url)
                .setHash(file.getCheckSum())
                .setTimeout((int) s3Config.ttl().toSeconds())
                .build();
    }
}

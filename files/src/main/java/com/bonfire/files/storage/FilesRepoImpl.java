package com.bonfire.files.storage;

import com.bonfire.internal.api.services.AvpoStatus;
import com.bonfire.internal.api.services.File;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.quarkus.runtime.api.session.QuarkusCqlSession;
import com.google.protobuf.ByteString;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import lombok.val;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.bonfire.files.api.Mappers.toInstant;
import static com.bonfire.files.api.Mappers.toTimestampBuilder;

@Singleton
public class FilesRepoImpl implements FilesRepo {
    private static final String FILES_TABLE = "bonfire.files";

    private static final String ID = "id";
    private static final String FILE_NAME = "file_name";
    private static final String FILE_SIZE = "file_size";
    private static final String MIME_TYPE = "mime_type";
    private static final String UPLOADED_AT = "uploaded_at";
    private static final String ACCESS_HASH = "access_hash";
    private static final String CHECK_SUM = "check_sum";
    private static final String DC_ID = "dc_id";
    private static final String AVPO_STATUS = "avpo_status";
    private static final String SERVER_CHECKSUM = "server_check_sum";
    private static final String REFERENCE_COUNT = "reference_count";

    private static final String FILES_FIELDS = STR."\{ID}, \{FILE_NAME}, \{FILE_SIZE}, \{MIME_TYPE}, \{UPLOADED_AT}, \{ACCESS_HASH}, \{CHECK_SUM}, \{DC_ID}, \{AVPO_STATUS}, \{SERVER_CHECKSUM}, \{REFERENCE_COUNT}";

    private final QuarkusCqlSession session;

    private static final String INSERT = "insert";
    private static final String DELETE = "delete";
    private static final String GET = "get";

    private final Map<String, PreparedStatement> preparedStatements = new ConcurrentHashMap<>();

    public FilesRepoImpl(QuarkusCqlSession session) {
        this.session = session;
    }

    @Override
    public void insert(File file) {
        val bind = insert().bind(
                file.getId(),
                file.getFileName(),
                file.getFileSize(),
                file.getMimeType(),
                toInstant(file.getUploadedAt()),
                file.getAccessHash(),
                ByteBuffer.wrap(file.getCheckSum().toByteArray()),
                file.getDcId(),
                file.getAvpoStatus().getNumber(),
                ByteBuffer.wrap(file.getServerCheckSum().toByteArray()),
                file.getReferenceCount()
        );
        session.execute(bind);
    }

    @Override
    public Optional<File> get(String fileId) {
        val bind = get().bind(fileId);
        return Optional.ofNullable(session.execute(bind).one()).map(this::toFile);
    }

    @Override
    public void delete(String fileId) {
        val bind = delete().bind(fileId); //todo: executeAsync?
        session.execute(bind);
    }

    @SneakyThrows
    private File toFile(Row row) {
        val builder = File.newBuilder()
                .setId(row.getString(ID))
                .setFileName(row.getString(FILE_NAME))
                .setFileSize(row.getInt(FILE_SIZE))
                .setMimeType(row.getString(MIME_TYPE))
                .setUploadedAt(toTimestampBuilder(row.getInstant(UPLOADED_AT)))
                .setAccessHash(row.getLong(ACCESS_HASH))
                .setCheckSum(ByteString.copyFrom(row.getByteBuffer(CHECK_SUM)))
                .setDcId(row.getInt(DC_ID))
                .setAvpoStatus(AvpoStatus.forNumber(row.getInt(AVPO_STATUS)))
                .setServerCheckSum(ByteString.copyFrom(row.getByteBuffer(SERVER_CHECKSUM)))
                .setReferenceCount(row.getInt(REFERENCE_COUNT));
        return builder.build();
    }

    private PreparedStatement insert() {
        return preparedStatements.computeIfAbsent(INSERT, _ -> session
                .prepare(STR."INSERT INTO \{FILES_TABLE} (\{FILES_FIELDS}) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
        );
    }

    private PreparedStatement get() {
        return preparedStatements.computeIfAbsent(GET, _ -> session
                .prepare(STR."SELECT * FROM \{FILES_TABLE} WHERE ID = ?")
        );
    }

    private PreparedStatement delete() {
        return preparedStatements.computeIfAbsent(DELETE, _ -> session
                .prepare(STR."DELETE FROM \{FILES_TABLE} WHERE ID = ?")
        );
    }
}

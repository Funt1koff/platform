package com.bonfire.files;

import com.bonfire.files.storage.FilesRepo;
import com.bonfire.files.storage.FilesRepoImpl;
import com.bonfire.internal.api.services.AvpoStatus;
import com.bonfire.internal.api.services.File;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.quarkus.runtime.internal.session.DefaultQuarkusCqlSession;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.cassandra.CassandraContainer;

import java.time.Instant;

import static com.bonfire.files.api.Mappers.toTimestamp;

public class FilesRepoTest {
    final static CassandraContainer cassandraContainer = new CassandraContainer("cassandra:3.11.11")
            .withEnv("CASSANDRA_AUTHENTICATOR", "AllowAllAuthenticator");
    static CqlSession cqlSession;

    final FilesRepo repo = new FilesRepoImpl(new DefaultQuarkusCqlSession(cqlSession));

    @BeforeAll
    static void setUp() {
        cassandraContainer.start();
        cqlSession = CqlSession
                .builder()
                .addContactPoint(cassandraContainer.getContactPoint())
                .withLocalDatacenter(cassandraContainer.getLocalDatacenter())
                .build();
        cqlSession.execute("CREATE KEYSPACE bonfire WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};");
        createTables();
    }

    @AfterAll
    static void tearDown() {
        cassandraContainer.stop();
    }

    //    @BeforeEach
    static void createTables() {
        cqlSession.execute("CREATE TABLE bonfire.files (" +
                "  id TEXT," +
                "  file_name TEXT," +
                "  file_size INT," +
                "  mime_type TEXT," +
                "  uploaded_at TIMESTAMP," +
                "  access_hash BIGINT," +
                "  check_sum BLOB," +
                "  dc_id INT," +
                "  avpo_status INT," +
                "  server_check_sum BLOB," +
                "  reference_count INT, " +
                "  PRIMARY KEY (id)" +
                ")");
    }

    @AfterEach
    void dropTables() {
        cqlSession.execute("DROP TABLE bonfire.files;");
    }

    @Test
    public void testFileRepo() {
        File file = File.newBuilder()
                .setId("42L")
                .setFileName("test.txt")
                .setMimeType("text/plain")
                .setUploadedAt(toTimestamp(Instant.now()))
                .setAccessHash(123L)
                .setCheckSum(ByteString.copyFrom("0xDEADBEEF".getBytes()))
                .setDcId(1)
                .setAvpoStatus(AvpoStatus.AVPO_STATUS_NOT_REQUIRED)
                .setServerCheckSum(ByteString.copyFrom("0xBADFEED".getBytes()))
                .setReferenceCount(1)
                .build();

        repo.insert(file);

    }
}

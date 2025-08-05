package com.bonfire.files;

import com.bonfire.files.s3.S3ClientWrapper;
import com.bonfire.files.storage.FilesRepo;
import com.bonfire.files.storage.FilesRepoImpl;
import com.bonfire.internal.api.extensions.InitiatorExtensionHelper;
import com.bonfire.internal.api.models.Initiator;
import com.bonfire.util.JWTUtil;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.quarkus.runtime.internal.session.DefaultQuarkusCqlSession;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.tus.java.client.*;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.val;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

//@QuarkusTest
/*
 * To run this test:
 *
 * * Run Minio:
 *              docker pull minio/minio:RELEASE.2025-03-12T18-04-18Z-cpuv1
 *              docker run -p 9000:9000 -d -p 9001:9001 -it -e "MINIO_ROOT_USER=miniominio" \
 *                      -e "MINIO_ROOT_PASSWORD=miniominio" minio/minio:RELEASE.2025-03-12T18-04-18Z-cpuv1 \
 *                      server /data --console-address ":9001"
 * * Create bucket:
 *              bonfire
 *
 * * Export S3 envs:
 *              AWS_REGION=ru-central-1
 *              AWS_SECRET_ACCESS_KEY=*****
 *              AWS_ACCESS_KEY_ID=*****
 *
 * * Set S3 properties to files/src/test/resources/application.properties
 *
 * * Run TUSD:
 *              ./tusd -disable-download -hooks-grpc localhost:9100 \
 *                      -hooks-enabled-events pre-create,post-finish \
 *                      -s3-bucket=bonfire -s3-endpoint http://localhost:9000

 * * Run Cassandra
 *
 * * Enjoy!
 */
public class UploadTest {
    private static final Logger log = Logger.getLogger(UploadTest.class);
    private static final String fileName = "file.txt";
    private static final int serverPort = 9100;
    private static final int tusPort = 8080;
    private final Random random = new Random();

//    final static CassandraContainer cassandraContainer = new CassandraContainer("cassandra:3.11.11")
//            .withEnv("CASSANDRA_AUTHENTICATOR", "AllowAllAuthenticator");

    static CqlSession cqlSession;

    final FilesRepo repo = new FilesRepoImpl(new DefaultQuarkusCqlSession(cqlSession));

    @Inject
    S3ClientWrapper s3ClientWrapper;

    @BeforeAll
    static void setUp() {
//        cassandraContainer.start();
        cqlSession = CqlSession
                .builder()
                .addContactPoint(InetSocketAddress.createUnresolved("localhost", 9042))
//                .addContactPoint(cassandraContainer.getContactPoint())
                .withLocalDatacenter("datacenter1")
//                .withLocalDatacenter(cassandraContainer.getLocalDatacenter())
                .build();
//        cqlSession.execute("CREATE KEYSPACE bonfire WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};");
//        createTables();
    }

//    @AfterAll
//    static void tearDown() {
//        cassandraContainer.stop();
//    }

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
                "  reference_count INT, " + "  PRIMARY KEY (id)" +
                ")");
    }

    //    @AfterEach
//    void dropTables() {
//        cqlSession.execute("DROP TABLE bonfire.files;");
//    }
//    @Test
    public void testPresigner() {
        val url = s3ClientWrapper.getDownloadUrl("05bcffe0-4a8a-11f0-6c3d-9c01f93079f7");
        System.out.println(url);
    }

    @SneakyThrows
//    @Test
    public void testUpload() {
        val uploadKey = String.valueOf(random.nextLong());
        doUpload(uploadKey);

        Thread.sleep(5000L);

        val location = commitFileUpload(uploadKey);
        getUrl(location);
    }

    private void getUrl(FileLocation location) {
        val channel = getClientChannel();
        val credentials = getCallCredentials();
        val initiator = Initiator.newBuilder().setUser(Initiator.User.newBuilder().setUserId(123L)).build();

        val request = GetFileUrlRequest.newBuilder()
                .setFile(
                        FileLocation.newBuilder()
                                .setFileId(location.getFileId())
                                .setAccessHash(location.getAccessHash())
                ).build();
        val enrichedRequest = InitiatorExtensionHelper.setToMessage(request, initiator);
        val res = FilesGrpc.newBlockingStub(channel)
                .withCallCredentials(credentials)
                .withDeadlineAfter(30, TimeUnit.SECONDS)
                .getFileUrl(enrichedRequest);

        System.out.println(res);
    }

//    private void testHook() {
//        val channel = getClientChannel();
//        val credentials = getCallCredentials();
//        val res = HookHandlerGrpc.newBlockingStub(channel)
//                .withCallCredentials(credentials)
//                .invokeHook(
//                        HookRequest.newBuilder()
//                                .setType("pre-create")
//                                .build()
//                );
//        System.out.println("###### before upload hook test: " + res);
//    }

    private ManagedChannel getClientChannel() {
        return ManagedChannelBuilder.forAddress("localhost", serverPort).usePlaintext().build();
    }

    private CallCredentials getCallCredentials() {
        return new CallCredentials() {
            @Override
            public void applyRequestMetadata(CallCredentials.RequestInfo
                                                     requestInfo, Executor executor, CallCredentials.MetadataApplier metadataApplier) {
                final var metadata = new Metadata();
                metadata.put(Metadata.Key.of("x-user-id", Metadata.ASCII_STRING_MARSHALLER), "123");
                metadataApplier.apply(metadata);
            }
        };
    }

    public void doUpload(String uploadKey) throws Exception {
        TusClient client = new TusClient();
        val token = JWTUtil.generateJwtToken("123");
        System.out.println(token);
        client.setHeaders(Map.of("Authorization", "Bearer " + token));

        client.setUploadCreationURL(new URL("http://localhost:" + tusPort + "/files"));

        client.enableResuming(new TusURLMemoryStore());

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        val uri = classloader.getResource(fileName).toURI();
        File file = new File(uri);

        final TusUpload upload = new TusUpload(file);
        upload.setMetadata(Map.of("upload_key", uploadKey));

        System.out.println("Starting upload...");

        TusExecutor executor = new TusExecutor() {

            @Override
            protected void makeAttempt() throws ProtocolException, IOException {
                TusUploader uploader = client.resumeOrCreateUpload(upload);
                uploader.setChunkSize(1024);
                do {
                    long totalBytes = upload.getSize();
                    long bytesUploaded = uploader.getOffset();
                    double progress = (double) bytesUploaded / totalBytes * 100;

                    System.out.printf("Upload at %06.2f%%.\n", progress);
                } while (uploader.uploadChunk() > -1);

                uploader.finish();
            }
        };
        executor.makeAttempts();
    }

    private FileLocation commitFileUpload(String uploadKey) {
        val channel = getClientChannel();
        val credentials = getCallCredentials();
        val initiator = Initiator.newBuilder().setUser(Initiator.User.newBuilder().setUserId(123L)).build();

        val request = CommitFileUploadRequest.newBuilder()
                .setUploadKey(uploadKey)
                .build();
        val enrichedRequest = InitiatorExtensionHelper.setToMessage(request, initiator);
        val res = FilesGrpc.newBlockingStub(channel)
                .withCallCredentials(credentials)
                .withDeadlineAfter(30, TimeUnit.SECONDS)
                .commitFileUpload(enrichedRequest);

        return res.getUploadedFileLocation();
    }
}

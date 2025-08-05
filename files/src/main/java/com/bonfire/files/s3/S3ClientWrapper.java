package com.bonfire.files.s3;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.val;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Base64;

@ApplicationScoped
public class S3ClientWrapper {

    @Inject
    S3Config s3Config;

    private S3Presigner s3Presigner;
    private S3Client s3Client;
    private AwsCredentialsProvider credentialsProvider;

    @PostConstruct
    public void init() {
        credentialsProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(s3Config.accessKey(), s3Config.secretKey())
        );

        s3Presigner = buildS3Presigner(s3Config);
        s3Client = buildS3Client(s3Config);
    }

    private S3Client buildS3Client(S3Config config) {
        return S3Client.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(config.region()))
                .endpointOverride(config.endpointURI())
                .build();
    }

    private S3Presigner buildS3Presigner(S3Config config) {
        val pathStyle = S3Configuration.builder()
                .pathStyleAccessEnabled(config.pathStyle())
                .build();

        return S3Presigner.builder()
                .serviceConfiguration(pathStyle)
                .credentialsProvider(credentialsProvider)
                .region(Region.of(config.region()))
                .endpointOverride(config.endpointURI())
                .build();
    }

    public void upload(InputStream file, String contentType, Long contentLength, String s3FileKey) throws Exception {
        val bytes = file.readAllBytes();
        val digester = MessageDigest.getInstance("SHA-256");
        val digest = digester.digest(bytes);
        val hash = Base64.getEncoder().encodeToString(digest);

        val putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Config.bucket())
                .key(s3FileKey)
                .contentType(contentType)
                .contentLength(contentLength)
                .checksumSHA256(hash)
                .build();

        val requestBody = RequestBody.fromBytes(bytes);
        s3Client.putObject(putObjectRequest, requestBody);
    }

    public String getDownloadUrl(String s3FileKey) {
        val objectRequest = GetObjectRequest.builder()
                .bucket(s3Config.bucket())
                .key(s3FileKey)
                .build();

        val presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(s3Config.ttl())
                .getObjectRequest(objectRequest)
                .build();

        val presignedResponse = s3Presigner.presignGetObject(presignRequest);
        return presignedResponse.url().toExternalForm();

    }

    public void delete(String s3FileKey) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(s3Config.bucket())
                        .key(s3FileKey)
                        .build()
        );
    }

    @PreDestroy
    void close() {
        s3Client.close();
        s3Presigner.close();
    }
}

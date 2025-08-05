package com.bonfire.files.s3;

import io.smallrye.config.ConfigMapping;
import jakarta.enterprise.context.ApplicationScoped;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

@ApplicationScoped
@ConfigMapping(prefix = "files.s3")
public interface S3Config {
    String accessKey();
    String secretKey();
    String region();
    String bucket();
    String endpoint();
    boolean pathStyle();
    Duration ttl();

    default URI endpointURI() {
        try {
            return new URI(endpoint());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.bonfire.ignite;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.util.List;
import java.util.Optional;

@ConfigMapping(prefix = "ignite")
public interface IgniteClientConfig {
    @WithDefault("localhost:47500..47509")
    List<String> addresses();

    @WithDefault("true")
    boolean create();

    @WithDefault("2")
    int cacheBackups();

    @WithDefault("false")
    boolean useSsl();

    Optional<String> sslKeyStorePath();

    Optional<String> sslKeyStorePassword();

    @WithDefault("false")
    boolean sslTrustAll();

    Optional<String> sslTrustStorePath();

    Optional<String> sslTrustStorePassword();

    @ConfigMapping(prefix = "auth")
    interface Auth {
        String username();

        String password();
    }

    @WithDefault("false")
    boolean authEnabled();

    Optional<Auth> auth();
}

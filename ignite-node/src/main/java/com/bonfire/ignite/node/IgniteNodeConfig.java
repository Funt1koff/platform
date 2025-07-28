package com.bonfire.ignite.node;

import io.smallrye.config.ConfigMapping;

import java.util.Optional;

@ConfigMapping(prefix = "ignite")
public interface IgniteNodeConfig {

    Optional<String> workingDirectory();

    Optional<String> persistenceWalPath();

    Optional<String> persistenceWalArchivePath();

    Optional<String> persistenceStoragePath();

}

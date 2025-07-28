package com.bonfire.ignite.node;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.val;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterState;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.jboss.logging.Logger;

@ApplicationScoped
public class IgniteProvider {
    private static final Logger log = Logger.getLogger(IgniteProvider.class);

    @Inject
    IgniteNodeConfig config;

    MeterRegistry registry;

    @Getter(lazy = true, onMethod = @__({@Produces}))
    private final Ignite ignite = initIgnite(config, registry);

    private Ignite initIgnite(IgniteNodeConfig config, MeterRegistry registry) {
        val ignite = Ignition.start(fromConfig(config, registry));
        ignite.cluster().state(ClusterState.ACTIVE);
        return ignite;
    }

    private IgniteConfiguration fromConfig(IgniteNodeConfig config, MeterRegistry registry) {
        var configuration = new IgniteConfiguration();

        log.info("Configuring Ignite as server node");
        try {
            val contextClassLoader = Thread.currentThread().getContextClassLoader();
            log.info(STR."Configuring Ignite with ClssLoader: \{contextClassLoader.getName()}");

            configuration.setClientMode(false);

            configuration.setPeerClassLoadingEnabled(false);
            configuration.setClassLoader(contextClassLoader);

            if (config.workingDirectory().isPresent()) {
                configuration.setWorkDirectory(config.workingDirectory().get());
            }

            val storageConfig = new DataStorageConfiguration();
            storageConfig.getDefaultDataRegionConfiguration().setPersistenceEnabled(false);
            configuration.setDataStorageConfiguration(storageConfig);

            if (config.persistenceWalPath().isPresent()) {
                storageConfig.setWalPath(config.persistenceWalPath().get());
            }

            if (config.persistenceWalArchivePath().isPresent()) {
                storageConfig.setWalArchivePath(config.persistenceWalArchivePath().get());
            }

            if (config.persistenceStoragePath().isPresent()) {
                storageConfig.setStoragePath(config.persistenceStoragePath().get());
            }
        } catch (Throwable ex) {
            log.error("Failed to config Ignite", ex);
            throw ex;
        }
        return configuration;
    }
}

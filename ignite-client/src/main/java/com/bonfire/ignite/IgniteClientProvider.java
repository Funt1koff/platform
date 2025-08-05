package com.bonfire.ignite;

import com.bonfire.cache.CacheNames;
import com.bonfire.cache.DeduplicationEntity;
import com.bonfire.cache.FileUploadKey;
import com.bonfire.cache.UserLastSeenKey;
import io.quarkus.logging.Log;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import lombok.val;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheKeyConfiguration;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.affinity.AffinityFunction;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.client.*;
import org.apache.ignite.configuration.ClientConfiguration;

import java.util.ArrayList;

public class IgniteClientProvider {

    @Produces
    @Singleton
    public static IgniteClient initIgniteClient(IgniteClientConfig config) {
        ClientConfiguration clientCfg = new ClientConfiguration()
                .setAddresses(config.addresses().toArray(String[]::new))
                .setPartitionAwarenessEnabled(true)
                .setPartitionAwarenessMapperFactory(new ClientPartitionAwarenessMapperFactory() {
                    /** {@inheritDoc} */
                    @Override
                    public ClientPartitionAwarenessMapper create(String cacheName, int partitions) {
                        AffinityFunction aff = new RendezvousAffinityFunction(false, partitions);
                        return aff::partition;
                    }
                });

        if (config.useSsl()) {
            clientCfg
                    .setSslMode(SslMode.REQUIRED)
                    .setSslClientCertificateKeyStorePath(config.sslKeyStorePath()
                            .orElseThrow(() -> new RuntimeException("ignite.ssl-key-store-path is required")))
                    .setSslClientCertificateKeyStorePassword(config.sslKeyStorePassword()
                            .orElseThrow(() -> new RuntimeException("ignite.ssl-key-store-password is required")));
            if (config.sslTrustAll()) {
                clientCfg
                        .setSslTrustAll(true);
            } else {
                clientCfg
                        .setSslTrustCertificateKeyStorePath(config.sslTrustStorePath()
                                .orElseThrow(() -> new RuntimeException("ignite.ssl-trust-store-path is required")))
                        .setSslTrustCertificateKeyStorePassword(config.sslTrustStorePassword()
                                .orElseThrow(() -> new RuntimeException("ignite.ssl-trust-store-password is required")));
            }

        } else {
            clientCfg.setSslMode(SslMode.DISABLED);
        }
        if (config.authEnabled()) {
            val auth = config.auth().orElseThrow(() -> new RuntimeException("ignite.auth.* is required"));
            clientCfg
                    .setUserName(auth.username())
                    .setUserPassword(auth.password());
        }

        Log.info(STR."ContextClassLoader: \{Thread.currentThread().getContextClassLoader().getName()}");
        val igniteClient = Ignition.startClient(clientCfg);
        migrate(config, igniteClient);
        return igniteClient;
    }

    private static void migrate(IgniteClientConfig config, IgniteClient client) {
        val cacheConfigurations = new ArrayList<ClientCacheConfiguration>();

        // Cache<DeduplicationEntity.Key, DeduplicationEntity>
        cacheConfigurations.add(new ClientCacheConfiguration()
                .setName(CacheNames.DEDUPLICATION)
                .setKeyConfiguration(new CacheKeyConfiguration(DeduplicationEntity.KeyV2.class)).setName(CacheNames.USER_SEQ_UPDATES_STATE)
                .setAtomicityMode(CacheAtomicityMode.ATOMIC)
                .setCacheMode(CacheMode.PARTITIONED));

        // Cache<UserLastSeenKey,UserLastSeen>
        cacheConfigurations.add(new ClientCacheConfiguration()
                .setName(CacheNames.USER_LAST_SEEN)
                .setKeyConfiguration(new CacheKeyConfiguration(UserLastSeenKey.class))
                .setAtomicityMode(CacheAtomicityMode.ATOMIC)
                .setCacheMode(CacheMode.PARTITIONED));

        // Cache<Long,BotEntityBinarylizable>
        cacheConfigurations.add(new ClientCacheConfiguration()
                .setName(CacheNames.BOTS)
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setCacheMode(CacheMode.PARTITIONED));

        // Cache<UUID,Long>
        cacheConfigurations.add(new ClientCacheConfiguration()
                .setName(CacheNames.BOT_TOKENS)
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setCacheMode(CacheMode.PARTITIONED));

        // Cache<Long,long[]>
        cacheConfigurations.add(new ClientCacheConfiguration()
                .setName(CacheNames.USER_BOTS)
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setCacheMode(CacheMode.PARTITIONED));

        // Cache<Long,ProfilePhotosBinaryWrapper>>
        cacheConfigurations.add(new ClientCacheConfiguration()
                .setName(CacheNames.USER_PROFILE_PHOTOS)
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setCacheMode(CacheMode.PARTITIONED));

        // Cache<Long,BotUpdatesState>
        cacheConfigurations.add(new ClientCacheConfiguration()
                .setName(CacheNames.BOT_UPDATE_STATES)
                .setAtomicityMode(CacheAtomicityMode.ATOMIC)
                .setCacheMode(CacheMode.PARTITIONED));

        cacheConfigurations.add(new ClientCacheConfiguration().setName(CacheNames.FILE_UPLOADS)
                .setKeyConfiguration(new CacheKeyConfiguration(FileUploadKey.class)) // FileUploadKey<UserLastSeenKey,FileUploadBinaryWrapper>
                .setAtomicityMode(CacheAtomicityMode.ATOMIC)
                .setCacheMode(CacheMode.PARTITIONED));

        if (config.create()) {
            for (ClientCacheConfiguration cacheConfiguration : cacheConfigurations) {
                cacheConfiguration
                        .setBackups(config.cacheBackups())
                        .setStatisticsEnabled(true);
                try {
                    Log.info(STR."Try create cache `\{cacheConfiguration.getName()}`");
                    client.getOrCreateCache(cacheConfiguration);
                } catch (Exception e) {
                    Log.error(STR."Failed to create cache `\{cacheConfiguration.getName()}`", e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

}

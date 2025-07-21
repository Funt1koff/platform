package com.bonfire.partitioning;

import org.eclipse.microprofile.config.inject.ConfigProperty;

public interface PartitionerConfig {

    @ConfigProperty(defaultValue = "64")
    int partitions();

}

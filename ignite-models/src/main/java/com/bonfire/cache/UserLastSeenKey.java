package com.bonfire.cache;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.apache.ignite.cache.affinity.AffinityKeyMapped;

@Data
@FieldNameConstants
public class UserLastSeenKey {
    @AffinityKeyMapped
    private final long userId;
}

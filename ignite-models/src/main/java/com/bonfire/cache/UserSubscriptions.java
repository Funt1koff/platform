package com.bonfire.cache;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class UserSubscriptions {
    private final long[] subscriptions;
}

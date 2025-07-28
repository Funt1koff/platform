package com.bonfire.cache;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class UserLastSeen {
    private final long last_seen;
}

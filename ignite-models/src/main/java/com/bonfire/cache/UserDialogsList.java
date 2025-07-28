package com.bonfire.cache;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class UserDialogsList {
    private final int size;
    private final long[] p2pUids;
    private final long[] groupIds;
    private final long channelIds;
}

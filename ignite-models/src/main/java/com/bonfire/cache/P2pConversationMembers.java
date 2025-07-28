package com.bonfire.cache;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class P2pConversationMembers {
    private final boolean foreignDcLesserUid;
    private final boolean foreignDcGreaterUid;
}

package com.bonfire.cache.dialogs;

import lombok.Value;
import lombok.experimental.FieldNameConstants;

@Value
@FieldNameConstants
public class UserDialog {
    int topMsgId;
    int readInboxMaxMsgId;
    int readOutboxMaxMsgId;
    int unreadMsgsCount;
    int[] unreadDeletedMsgsIds;
    int unreadMentionsCount;
    boolean unread_mark;
    boolean pinned;
    int maxChannelMsgSeq;
    byte[] topMessageProto;

    public static UserDialog empty = new UserDialog(
            0,
            0,
            0,
            0,
            new int[0],
            0,
            false,
            false,
            0,
            null
    );
}

package com.bonfire.cache.helpers;

import com.bonfire.internal.api.models.Message;

import java.util.Optional;

public interface ReadonlyUserDialogBinaryWrapper {
    Integer getReadInboxMaxMsgId();

    Integer getReadOutboxMaxMsgId();

    int[] getUnreadDeletedMsgIds();

    int getTopMsgId();

    Optional<Message> getTopMessage();

    int getUnreadMsgsCount();
}

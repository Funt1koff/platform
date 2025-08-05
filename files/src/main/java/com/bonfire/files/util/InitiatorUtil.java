package com.bonfire.files.util;

import com.bonfire.internal.api.models.Initiator;
import lombok.val;

public class InitiatorUtil {

    public static long callerId(Initiator initiator) {
        val typ = initiator.getCallerCase();

        if (typ == Initiator.CallerCase.USER) {
            return initiator.getUser().getUserId();
        } else if (typ == Initiator.CallerCase.BOT) {
            return initiator.getBot().getBotId();
        } else {
            throw new RuntimeException("Unknown Initiator");
        }
    }
}

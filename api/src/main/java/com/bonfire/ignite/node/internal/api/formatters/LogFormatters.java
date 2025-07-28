package com.bonfire.ignite.node.internal.api.formatters;

import com.bonfire.internal.api.models.Initiator;

public class LogFormatters {

    public static String format(Initiator initiator) {
        if (initiator == null) {
            return "null";
        }
        return switch (initiator.getCallerCase()) {
            case USER -> format(initiator.getUser());
            case BOT -> format(initiator.getBot());
            default -> "unknown";
        };
    }

    public static String format(Initiator.User userInitiator) {
        if (userInitiator == null) {
            return "null";
        }

        return STR."User{id:\{userInitiator.getUserId()}, session:\{userInitiator.getSessionId()}";
    }

    public static String format(Initiator.Bot botInitiator) {
        if (botInitiator == null) {
            return "null";
        }

        return STR."Bot{id:\{botInitiator.getBotId()}}";
    }
}

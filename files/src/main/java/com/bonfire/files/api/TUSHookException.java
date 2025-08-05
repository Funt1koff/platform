package com.bonfire.files.api;

public sealed class TUSHookException extends RuntimeException {

    public TUSHookException(String message, Throwable cause) {
        super(message, cause, false, false);
    }

    public TUSHookException(String message) {
        this(message, null);
    }

    public static final class Unauthorized extends TUSHookException {
        public Unauthorized(String message, Throwable cause) {
            super(message, cause);
        }

        public Unauthorized(String message) {
            super(message);
        }
    }

    public static final class BadRequest extends TUSHookException {
        public BadRequest(String message, Throwable cause) {
            super(message, cause);
        }

        public BadRequest(String message) {
            super(message);
        }
    }
}

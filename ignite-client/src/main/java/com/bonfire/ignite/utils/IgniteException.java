package com.bonfire.ignite.utils;

public class IgniteException extends RuntimeException {
    public IgniteException(String message) {
        super(message, null, false, false);
    }

    public static class TransactionException extends IgniteException {
        public TransactionException() {
            super("Transaction failed. Retry later");
        }
    }
}

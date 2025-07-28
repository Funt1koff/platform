package com.bonfire.cache;

import com.bonfire.cache.dialogs.UserDialog;
import org.apache.ignite.IgniteBinary;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.apache.ignite.client.IgniteClient;

public class UserDialogBinaryHelper {
    private final IgniteBinary binary;

    public UserDialogBinaryHelper(IgniteClient ignite) {
        binary = ignite.binary();
    }

    public BinaryObjectBuilder initBuilder() {
        return this.binary.builder(UserDialog.class.getName());
    }
}

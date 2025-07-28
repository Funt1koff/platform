package com.bonfire.cache.helpers;

import com.bonfire.cache.UserDialogsList;
import org.apache.ignite.IgniteBinary;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.apache.ignite.client.IgniteClient;

public class UserDialogsListBinaryHelper {
    private final IgniteBinary binary;

    public UserDialogsListBinaryHelper(IgniteClient ignite) {
        binary = ignite.binary();
    }

    public BinaryObjectBuilder initBuilder() {
        return binary.builder(UserDialogsList.class.getName());
    }
}

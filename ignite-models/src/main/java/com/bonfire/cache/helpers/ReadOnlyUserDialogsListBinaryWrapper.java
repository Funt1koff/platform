package com.bonfire.cache.helpers;

public interface ReadOnlyUserDialogsListBinaryWrapper {
    void setSize(int value);

    long[] getP2pIds();

    long[] getGroupIds();

    long[] getChannelIds();
}

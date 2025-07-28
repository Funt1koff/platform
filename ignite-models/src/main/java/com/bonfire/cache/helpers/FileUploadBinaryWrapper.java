package com.bonfire.cache.helpers;

import com.bonfire.cache.FileUploadEntry;
import com.google.protobuf.Parser;

public class FileUploadBinaryWrapper extends ProtoBinaryWrapper<FileUploadEntry> {

    public FileUploadBinaryWrapper(FileUploadEntry value) {
        super(value);
    }

    @Override
    public Parser<FileUploadEntry> parser() {
        return FileUploadEntry.parser();
    }
}

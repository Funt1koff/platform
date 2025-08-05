package com.bonfire.files.storage;

import com.bonfire.internal.api.services.File;

import java.util.Optional;

public interface FilesRepo {
    void insert(File file);

    Optional<File> get(String fileId);

    void delete(String fileId);
}

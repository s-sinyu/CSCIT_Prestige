package com.prestige.renovation.common.storage;

import java.util.Optional;

/**
 * Storage abstraction for uploaded images (PRD 7.3). Callers only ever see an opaque file key.
 * The current implementation keeps bytes in PostgreSQL ({@link DatabaseStorageService});
 * a cloud implementation (e.g. S3, Cloudinary) can be added without changing callers.
 */
public interface StorageService {

    /** Stores the bytes and returns a new server-generated key such as {@code 3f2a...e1.jpg}. */
    String store(byte[] data, String contentType, String extension);

    Optional<StoredContent> load(String key);

    void delete(String key);

    /** File bytes plus their content type. */
    record StoredContent(String contentType, byte[] data) {
    }
}

package com.prestige.renovation.common.storage;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.prestige.renovation.entity.StoredFile;

/** Stores uploaded files as rows of {@link StoredFile} (bytea column). */
@Service
public class DatabaseStorageService implements StorageService {

    /** Keys are always server generated: a UUID plus a whitelisted extension. Nothing else is accepted. */
    private static final Pattern KEY = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpg|png|webp)$");

    public static boolean isValidKey(String key) {
        return key != null && KEY.matcher(key).matches();
    }

    @Override
    public String store(byte[] data, String contentType, String extension) {
        String key = UUID.randomUUID() + "." + extension;
        new StoredFile(key, contentType, data).save();
        return key;
    }

    @Override
    public Optional<StoredContent> load(String key) {
        if (!isValidKey(key)) {
            return Optional.empty();
        }
        return StoredFile.findByKey(key).map(f -> new StoredContent(f.getContentType(), f.getData()));
    }

    @Override
    public void delete(String key) {
        if (isValidKey(key) && StoredFile.findByKey(key).isPresent()) {
            StoredFile.deleteByKey(key);
        }
    }
}

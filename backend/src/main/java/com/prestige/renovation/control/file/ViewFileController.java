package com.prestige.renovation.control.file;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.common.storage.StorageService;

/** Control for serving uploaded images publicly by their server-generated key. */
@Service
public class ViewFileController {

    private final StorageService storage;

    public ViewFileController(StorageService storage) {
        this.storage = storage;
    }

    @Transactional(readOnly = true)
    public StorageService.StoredContent load(String key) {
        return storage.load(key).orElseThrow(() -> ApiException.notFound("File not found"));
    }
}

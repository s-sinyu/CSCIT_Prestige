package com.prestige.renovation.boundary.file;

import java.util.concurrent.TimeUnit;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.storage.StorageService;
import com.prestige.renovation.control.file.ViewFileController;

/** Boundary for user story: Anyone downloads an uploaded image by key. */
@RestController
public class ViewFileBoundary {

    private final ViewFileController controller;

    public ViewFileBoundary(ViewFileController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/files/{key}")
    public ResponseEntity<byte[]> view(@PathVariable String key) {
        StorageService.StoredContent file = controller.load(key);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable())
                .header("X-Content-Type-Options", "nosniff")
                .body(file.data());
    }
}

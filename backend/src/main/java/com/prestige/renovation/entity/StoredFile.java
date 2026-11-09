package com.prestige.renovation.entity;

import java.time.Instant;
import java.util.Optional;

import com.prestige.renovation.entity.repository.StoredFileRepository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Entity: the bytes of an uploaded image (IDP image or company logo).
 * Images are kept in the database because the free hosting disk is wiped on every restart.
 */
@Entity
@Table(name = "stored_files")
public class StoredFile {

    private static StoredFileRepository repository;

    @Id
    @Column(name = "file_key")
    private String fileKey;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private Integer sizeBytes;

    @Column(nullable = false, columnDefinition = Columns.BYTEA)
    private byte[] data;

    @Column(name = "created_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant createdAt;

    protected StoredFile() {
    }

    public StoredFile(String fileKey, String contentType, byte[] data) {
        this.fileKey = fileKey;
        this.contentType = contentType;
        this.data = data;
        this.sizeBytes = data.length;
    }

    public static void bind(StoredFileRepository repo) {
        repository = repo;
    }

    public static Optional<StoredFile> findByKey(String key) {
        return repository.findById(key);
    }

    public static void deleteByKey(String key) {
        repository.deleteById(key);
    }

    public StoredFile save() {
        return repository.save(this);
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public String getFileKey() {
        return fileKey;
    }

    public String getContentType() {
        return contentType;
    }

    public Integer getSizeBytes() {
        return sizeBytes;
    }

    public byte[] getData() {
        return data;
    }
}

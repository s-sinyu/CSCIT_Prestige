package com.prestige.renovation.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prestige.renovation.entity.StoredFile;

public interface StoredFileRepository extends JpaRepository<StoredFile, String> {
}

package com.prestige.renovation.entity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.prestige.renovation.entity.repository.CategoryRepository;
import com.prestige.renovation.entity.repository.ProjectRepository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/** Entity: an IDP category managed by Platform Management (FR-PM-1 to 4). */
@Entity
@Table(name = "categories")
public class Category {

    private static CategoryRepository repository;
    private static ProjectRepository projectRepository;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, columnDefinition = Columns.BOOLEAN)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant updatedAt;

    protected Category() {
    }

    public Category(String name, String description, boolean active) {
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public static void bind(CategoryRepository repo, ProjectRepository projects) {
        repository = repo;
        projectRepository = projects;
    }

    public static Optional<Category> findById(Integer id) {
        return id == null ? Optional.empty() : repository.findById(id);
    }

    public static List<Category> findActive() {
        return repository.findByActiveTrueOrderByNameAsc();
    }

    public static List<Category> findAllSorted() {
        return repository.findAllByOrderByNameAsc();
    }

    public static Page<Category> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public static boolean nameTaken(String name) {
        return repository.existsByNameIgnoreCase(name);
    }

    public static boolean nameTakenByOther(String name, Integer id) {
        return repository.existsByNameIgnoreCaseAndIdNot(name, id);
    }

    /** Number of IDPs (including soft-deleted ones) using this category. */
    public long usageCount() {
        return projectRepository.countByCategoryId(id);
    }

    public Category save() {
        return repository.save(this);
    }

    public void delete() {
        repository.delete(this);
    }

    public void update(String name, String description, boolean active) {
        this.name = name;
        this.description = description;
        this.active = active;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

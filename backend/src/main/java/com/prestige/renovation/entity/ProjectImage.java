package com.prestige.renovation.entity;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.prestige.renovation.entity.repository.ProjectImageRepository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/** Entity: one image of an IDP (FR-IDP-3). One image per project is the cover. */
@Entity
@Table(name = "project_images")
public class ProjectImage {

    private static ProjectImageRepository repository;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private InteriorDesignProject project;

    @Column(name = "file_key", nullable = false)
    private String fileKey;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(nullable = false, columnDefinition = Columns.BOOLEAN)
    private boolean cover;

    @Column(name = "created_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant createdAt;

    protected ProjectImage() {
    }

    public ProjectImage(InteriorDesignProject project, String fileKey, int sortOrder, boolean cover) {
        this.project = project;
        this.fileKey = fileKey;
        this.sortOrder = sortOrder;
        this.cover = cover;
    }

    public static void bind(ProjectImageRepository repo) {
        repository = repo;
    }

    public static List<ProjectImage> findByProject(Integer projectId) {
        return repository.findByProjectIdOrderBySortOrderAscIdAsc(projectId);
    }

    public static long countByProject(Integer projectId) {
        return repository.countByProjectId(projectId);
    }

    public static int nextSortOrder(Integer projectId) {
        return repository.maxSortOrder(projectId) + 1;
    }

    /** Cover image file key per project id, batched for list pages. */
    public static Map<Integer, String> coverKeys(Collection<Integer> projectIds) {
        Map<Integer, String> result = new HashMap<>();
        if (projectIds.isEmpty()) {
            return result;
        }
        for (Object[] row : repository.findCoverKeys(projectIds)) {
            result.put((Integer) row[0], (String) row[1]);
        }
        return result;
    }

    public ProjectImage save() {
        return repository.save(this);
    }

    public void delete() {
        repository.delete(this);
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public Integer getId() {
        return id;
    }

    public InteriorDesignProject getProject() {
        return project;
    }

    public String getFileKey() {
        return fileKey;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public boolean isCover() {
        return cover;
    }

    public void setCover(boolean cover) {
        this.cover = cover;
    }
}

package com.prestige.renovation.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.prestige.renovation.entity.repository.RatingRepository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * Entity: a customer's rating of an interior designer, optionally tied to one of the designer's IDPs (FR-RT).
 * Limits (one general rating per designer, one per IDP) are enforced here and by partial unique indexes.
 */
@Entity
@Table(name = "ratings")
public class Rating {

    private static RatingRepository repository;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "designer_id", nullable = false)
    private User designer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private InteriorDesignProject project;

    @Column(nullable = false)
    private Integer score;

    private String comment;

    @Column(name = "created_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant updatedAt;

    protected Rating() {
    }

    public Rating(User customer, User designer, InteriorDesignProject project, int score, String comment) {
        this.customer = customer;
        this.designer = designer;
        this.project = project;
        this.score = score;
        this.comment = comment;
    }

    public static void bind(RatingRepository repo) {
        repository = repo;
    }

    public static Optional<Rating> findById(Integer id) {
        return id == null ? Optional.empty() : repository.findById(id);
    }

    public static Page<Rating> findForDesigner(Integer designerId, Integer projectId, Pageable pageable) {
        return projectId == null
                ? repository.findByDesignerId(designerId, pageable)
                : repository.findByDesignerIdAndProjectId(designerId, projectId, pageable);
    }

    public static boolean generalRatingExists(Integer customerId, Integer designerId) {
        return repository.existsByCustomerIdAndDesignerIdAndProjectIsNull(customerId, designerId);
    }

    public static boolean projectRatingExists(Integer customerId, Integer projectId) {
        return repository.existsByCustomerIdAndProjectId(customerId, projectId);
    }

    /** Average score rounded to one decimal place (half up), and number of ratings. */
    public static Summary summaryFor(Integer designerId) {
        RatingRepository.RatingStats stats = repository.statsForDesigner(designerId);
        long count = stats == null || stats.getRatingCount() == null ? 0 : stats.getRatingCount();
        Double avg = stats == null ? null : stats.getAverageScore();
        return new Summary(roundAverage(avg, count), count);
    }

    /** Rounds an average to one decimal place; null when there are no ratings. */
    public static BigDecimal roundAverage(Double average, long count) {
        if (count == 0 || average == null) {
            return null;
        }
        return BigDecimal.valueOf(average).setScale(1, RoundingMode.HALF_UP);
    }

    public Rating save() {
        return repository.save(this);
    }

    public void delete() {
        repository.delete(this);
    }

    public boolean isWrittenBy(Integer customerId) {
        return customer.getId().equals(customerId);
    }

    public void update(int score, String comment) {
        this.score = score;
        this.comment = comment;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    /** Average and count of a designer's ratings. */
    public record Summary(BigDecimal average, long count) {
    }

    public Integer getId() {
        return id;
    }

    public User getCustomer() {
        return customer;
    }

    public User getDesigner() {
        return designer;
    }

    public InteriorDesignProject getProject() {
        return project;
    }

    public Integer getScore() {
        return score;
    }

    public String getComment() {
        return comment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

package com.prestige.renovation.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.prestige.renovation.entity.repository.IssueReportRepository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.Predicate;

/** Entity: a problem reported by a customer or designer and resolved by the User Admin (FR-RPT). */
@Entity
@Table(name = "issue_reports")
public class IssueReport {

    private static IssueReportRepository repository;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueReportType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_project_id")
    private InteriorDesignProject targetProject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_designer_id")
    private User targetDesigner;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueReportStatus status = IssueReportStatus.OPEN;

    @Column(name = "created_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant createdAt;

    @Column(name = "rectified_at", columnDefinition = Columns.TIMESTAMPTZ)
    private Instant rectifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rectified_by_id")
    private User rectifiedBy;

    protected IssueReport() {
    }

    public IssueReport(User reporter, IssueReportType type, InteriorDesignProject targetProject, User targetDesigner,
                       String title, String description) {
        this.reporter = reporter;
        this.type = type;
        this.targetProject = targetProject;
        this.targetDesigner = targetDesigner;
        this.title = title;
        this.description = description;
        this.status = IssueReportStatus.OPEN;
    }

    public static void bind(IssueReportRepository repo) {
        repository = repo;
    }

    public static Optional<IssueReport> findById(Integer id) {
        return id == null ? Optional.empty() : repository.findWithDetailsById(id);
    }

    public static Page<IssueReport> findByReporter(Integer reporterId, Pageable pageable) {
        return repository.findByReporterId(reporterId, pageable);
    }

    public static Page<IssueReport> search(IssueReportStatus status, IssueReportType type, Pageable pageable) {
        Specification<IssueReport> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (status != null) {
                p.add(cb.equal(root.get("status"), status));
            }
            if (type != null) {
                p.add(cb.equal(root.get("type"), type));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
        return repository.findAll(spec, pageable);
    }

    public static long countWithStatus(IssueReportStatus status) {
        return repository.countByStatus(status);
    }

    /** Reports created in [from, to), split by their current status. */
    public static Map<IssueReportStatus, Long> countCreatedByStatus(Instant from, Instant to) {
        Map<IssueReportStatus, Long> counts = new EnumMap<>(IssueReportStatus.class);
        for (IssueReportStatus s : IssueReportStatus.values()) {
            counts.put(s, 0L);
        }
        for (Object[] row : repository.countCreatedByStatus(from, to)) {
            counts.put((IssueReportStatus) row[0], ((Number) row[1]).longValue());
        }
        return counts;
    }

    public IssueReport save() {
        return repository.save(this);
    }

    public boolean isReportedBy(Integer userId) {
        return reporter.getId().equals(userId);
    }

    /** One-way transition OPEN -> RECTIFIED. Returns false if already rectified. */
    public boolean rectify(User admin, Instant when) {
        if (status == IssueReportStatus.RECTIFIED) {
            return false;
        }
        status = IssueReportStatus.RECTIFIED;
        rectifiedAt = when;
        rectifiedBy = admin;
        return true;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Integer getId() {
        return id;
    }

    public User getReporter() {
        return reporter;
    }

    public IssueReportType getType() {
        return type;
    }

    public InteriorDesignProject getTargetProject() {
        return targetProject;
    }

    public User getTargetDesigner() {
        return targetDesigner;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public IssueReportStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getRectifiedAt() {
        return rectifiedAt;
    }

    public User getRectifiedBy() {
        return rectifiedBy;
    }
}

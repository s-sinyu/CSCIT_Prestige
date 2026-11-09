package com.prestige.renovation.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.prestige.renovation.entity.repository.ProjectRepository;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

/** Entity: an Interior Design Project (IDP) in a designer's portfolio (FR-IDP). */
@Entity
@Table(name = "projects")
public class InteriorDesignProject {

    private static ProjectRepository repository;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "designer_id", nullable = false)
    private User designer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = Columns.TEXT)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

    @Column(precision = 12, scale = 2)
    private BigDecimal budget;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private String location;

    @Column(nullable = false, columnDefinition = Columns.BOOLEAN)
    private boolean deleted;

    @Column(name = "deleted_at", columnDefinition = Columns.TIMESTAMPTZ)
    private Instant deletedAt;

    @Column(name = "created_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant updatedAt;

    protected InteriorDesignProject() {
    }

    public InteriorDesignProject(User designer) {
        this.designer = designer;
    }

    public static void bind(ProjectRepository repo) {
        repository = repo;
    }

    // ------------------------------------------------------------------ visibility rule (single source of truth)

    /**
     * IDP visibility = not deleted AND designer ACTIVE AND designer not deleted (PRD section 5).
     * Every public read path (search, detail, favourites, enquiry and rating creation) uses this predicate.
     */
    public static Predicate visibilityPredicate(CriteriaBuilder cb, Path<InteriorDesignProject> project) {
        Path<User> designer = project.get("designer");
        return cb.and(
                cb.isFalse(project.get("deleted")),
                cb.equal(designer.get("status"), AccountStatus.ACTIVE),
                cb.isFalse(designer.get("deleted")));
    }

    public static Specification<InteriorDesignProject> visible() {
        return (root, query, cb) -> visibilityPredicate(cb, root);
    }

    /** True if the project is visible to the public, evaluated in memory on a loaded entity. */
    public boolean isVisible() {
        return !deleted && designer != null && designer.isActive();
    }

    // ------------------------------------------------------------------ data access

    public static Optional<InteriorDesignProject> findVisibleById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        Specification<InteriorDesignProject> byId = (root, query, cb) -> cb.equal(root.get("id"), id);
        return repository.findOne(visible().and(byId));
    }

    /** Any project row, including soft-deleted ones (used for reports and issue-report targets). */
    public static Optional<InteriorDesignProject> findAnyById(Integer id) {
        return id == null ? Optional.empty() : repository.findById(id);
    }

    public static Optional<InteriorDesignProject> findOwned(Integer id, Integer designerId) {
        return repository.findByIdAndDesignerIdAndDeletedFalse(id, designerId);
    }

    public static Page<InteriorDesignProject> findOwnedByDesigner(Integer designerId, Pageable pageable) {
        return repository.findByDesignerIdAndDeletedFalse(designerId, pageable);
    }

    public static long countOwnedByDesigner(Integer designerId) {
        return repository.countByDesignerIdAndDeletedFalse(designerId);
    }

    /**
     * Public search (FR-CUS-1). All filters are optional and combinable. Sort: "newest" (default),
     * "budgetAsc" or "budgetDesc"; IDPs without a budget always come last when sorting by budget.
     * The pageable must be unsorted: ordering is applied here.
     */
    public static Page<InteriorDesignProject> search(SearchCriteria criteria, Pageable pageable) {
        Specification<InteriorDesignProject> spec = visible().and((root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (criteria.keyword() != null && !criteria.keyword().isBlank()) {
                String like = "%" + escapeLike(criteria.keyword().trim().toLowerCase()) + "%";
                p.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like, '\\'),
                        cb.like(cb.lower(root.get("description")), like, '\\')));
            }
            if (criteria.categoryId() != null) {
                p.add(cb.equal(root.get("category").get("id"), criteria.categoryId()));
            }
            if (criteria.status() != null) {
                p.add(cb.equal(root.get("status"), criteria.status()));
            }
            if (criteria.budgetMin() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("budget"), criteria.budgetMin()));
            }
            if (criteria.budgetMax() != null) {
                p.add(cb.lessThanOrEqualTo(root.get("budget"), criteria.budgetMax()));
            }
            if (criteria.designerName() != null && !criteria.designerName().isBlank()) {
                p.add(designerNameMatches(root, query, cb,
                        "%" + escapeLike(criteria.designerName().trim().toLowerCase()) + "%"));
            }
            // Ordering (Spring Data drops it again for the count query)
            List<Order> order = new ArrayList<>();
            String sort = criteria.sort() == null ? "newest" : criteria.sort();
            if (sort.equals("budgetAsc") || sort.equals("budgetDesc")) {
                Expression<Integer> noBudgetLast = cb.<Integer>selectCase()
                        .when(cb.isNull(root.get("budget")), 1).otherwise(0);
                order.add(cb.asc(noBudgetLast));
                order.add(sort.equals("budgetAsc") ? cb.asc(root.get("budget")) : cb.desc(root.get("budget")));
            }
            order.add(cb.desc(root.get("createdAt")));
            order.add(cb.desc(root.get("id")));
            query.orderBy(order);
            return cb.and(p.toArray(new Predicate[0]));
        });
        return repository.findAll(spec, pageable);
    }

    /** Company name matches, or the designer's full name matches when the designer has no company record. */
    private static Predicate designerNameMatches(Root<InteriorDesignProject> root,
                                                 jakarta.persistence.criteria.CriteriaQuery<?> query,
                                                 CriteriaBuilder cb, String like) {
        Join<InteriorDesignProject, User> designer = root.join("designer");

        Subquery<Integer> companyMatch = query.subquery(Integer.class);
        Root<DesignerCompany> c1 = companyMatch.from(DesignerCompany.class);
        companyMatch.select(c1.get("id")).where(
                cb.equal(c1.get("designer"), designer),
                cb.like(cb.lower(c1.get("companyName")), like, '\\'));

        Subquery<Integer> anyCompany = query.subquery(Integer.class);
        Root<DesignerCompany> c2 = anyCompany.from(DesignerCompany.class);
        anyCompany.select(c2.get("id")).where(cb.equal(c2.get("designer"), designer));

        return cb.or(
                cb.exists(companyMatch),
                cb.and(cb.not(cb.exists(anyCompany)), cb.like(cb.lower(designer.get("fullName")), like, '\\')));
    }

    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    /** IDPs created per category id in [from, to), including soft-deleted ones. */
    public static Map<Integer, Long> countCreatedByCategory(Instant from, Instant to) {
        Map<Integer, Long> counts = new HashMap<>();
        for (Object[] row : repository.countCreatedByCategory(from, to)) {
            counts.put((Integer) row[0], ((Number) row[1]).longValue());
        }
        return counts;
    }

    public static List<InteriorDesignProject> findAllByIds(List<Integer> ids) {
        return repository.findAllById(ids);
    }

    public InteriorDesignProject save() {
        return repository.save(this);
    }

    // ------------------------------------------------------------------ behaviour

    public boolean isOwnedBy(Integer userId) {
        return designer != null && designer.getId().equals(userId);
    }

    public void update(Category category, String title, String description, ProjectStatus status, BigDecimal budget,
                       LocalDate startDate, LocalDate endDate, String location) {
        this.category = category;
        this.title = title;
        this.description = description;
        this.status = status;
        this.budget = budget;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
    }

    public void softDelete(Instant when) {
        this.deleted = true;
        this.deletedAt = when;
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

    /** Search filters for {@link #search(SearchCriteria, Pageable)}. */
    public record SearchCriteria(String keyword, Integer categoryId, BigDecimal budgetMin, BigDecimal budgetMax,
                                 ProjectStatus status, String designerName, String sort) {
    }

    // ------------------------------------------------------------------ accessors

    public Integer getId() {
        return id;
    }

    public User getDesigner() {
        return designer;
    }

    public Category getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getLocation() {
        return location;
    }

    public boolean isDeleted() {
        return deleted;
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
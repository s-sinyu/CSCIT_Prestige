package com.prestige.renovation.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;

import com.prestige.renovation.entity.repository.ProjectViewRepository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entity: one counted view of an IDP by a logged-in customer on one calendar day (FR-ANA-1).
 * The unique constraint (customer, project, view_date) de-duplicates at database level.
 */
@Entity
@Table(name = "project_views")
public class ProjectView {

    private static ProjectViewRepository repository;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private InteriorDesignProject project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(name = "view_date", nullable = false)
    private LocalDate viewDate;

    @Column(name = "viewed_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant viewedAt;

    protected ProjectView() {
    }

    public ProjectView(InteriorDesignProject project, User customer, LocalDate viewDate, Instant viewedAt) {
        this.project = project;
        this.customer = customer;
        this.viewDate = viewDate;
        this.viewedAt = viewedAt;
    }

    public static void bind(ProjectViewRepository repo) {
        repository = repo;
    }

    /** Records a view unless this customer already viewed this IDP on that day. Returns true if counted. */
    public static boolean record(Integer projectId, Integer customerId, LocalDate day, Instant at) {
        return repository.insertIfAbsent(projectId, customerId, day, at) > 0;
    }

    public static long countForProject(Integer projectId) {
        return repository.countByProjectId(projectId);
    }

    public static Map<Integer, Long> countsForProjects(Collection<Integer> projectIds) {
        return Favourite.toMap(projectIds.isEmpty() ? List.of() : repository.countByProjectIds(projectIds));
    }

    public static long totalForDesigner(Integer designerId) {
        return repository.countForDesigner(designerId);
    }

    public static long countBetween(LocalDate from, LocalDate to) {
        return repository.countBetween(from, to);
    }

    /** Most viewed IDPs between two dates (inclusive): list of [projectId, views]. */
    public static List<long[]> topViewed(LocalDate from, LocalDate to, int limit) {
        List<long[]> result = new ArrayList<>();
        for (Object[] row : repository.topViewedBetween(from, to, PageRequest.of(0, limit))) {
            result.add(new long[] {((Integer) row[0]).longValue(), ((Number) row[1]).longValue()});
        }
        return result;
    }

    public ProjectView save() {
        return repository.save(this);
    }

    public Integer getId() {
        return id;
    }

    public InteriorDesignProject getProject() {
        return project;
    }

    public User getCustomer() {
        return customer;
    }

    public LocalDate getViewDate() {
        return viewDate;
    }

    public Instant getViewedAt() {
        return viewedAt;
    }
}

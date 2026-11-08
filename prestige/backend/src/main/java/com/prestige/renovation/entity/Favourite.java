package com.prestige.renovation.entity;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.prestige.renovation.entity.repository.FavouriteRepository;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

/**
 * Entity: an IDP saved to a customer's favourite list (FR-CUS-4).
 * The shortlist figure of an IDP is the number of favourites currently held (FR-ANA-2).
 */
@Entity
@Table(name = "favourites")
public class Favourite {

    private static FavouriteRepository repository;

    @EmbeddedId
    private FavouriteId id;

    @MapsId("customerId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private User customer;

    @MapsId("projectId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    private InteriorDesignProject project;

    @Column(name = "saved_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant savedAt;

    protected Favourite() {
    }

    public Favourite(User customer, InteriorDesignProject project, Instant savedAt) {
        this.id = new FavouriteId(customer.getId(), project.getId());
        this.customer = customer;
        this.project = project;
        this.savedAt = savedAt;
    }

    public static void bind(FavouriteRepository repo) {
        repository = repo;
    }

    /** Idempotent add: saving the same favourite twice keeps a single row. Returns true if a row was inserted. */
    public static boolean add(Integer customerId, Integer projectId, Instant savedAt) {
        return repository.insertIfAbsent(customerId, projectId, savedAt) > 0;
    }

    public static void remove(Integer customerId, Integer projectId) {
        FavouriteId key = new FavouriteId(customerId, projectId);
        if (repository.existsById(key)) {
            repository.deleteById(key);
        }
    }

    public static boolean exists(Integer customerId, Integer projectId) {
        return repository.existsById(new FavouriteId(customerId, projectId));
    }

    /** A customer's favourites whose IDP is still visible, newest first. */
    public static Page<Favourite> findVisibleForCustomer(Integer customerId, Pageable pageable) {
        Specification<Favourite> spec = (root, query, cb) -> cb.and(
                cb.equal(root.get("customer").get("id"), customerId),
                InteriorDesignProject.visibilityPredicate(cb, root.get("project")));
        return repository.findAll(spec, pageable);
    }

    /** Shortlist figure: number of customers currently holding the IDP as a favourite. */
    public static long shortlistCount(Integer projectId) {
        return repository.countByProjectId(projectId);
    }

    public static Map<Integer, Long> shortlistCounts(Collection<Integer> projectIds) {
        return toMap(projectIds.isEmpty() ? java.util.List.of() : repository.countByProjectIds(projectIds));
    }

    public static long shortlistTotalForDesigner(Integer designerId) {
        return repository.countForDesigner(designerId);
    }

    public static long countSavedBetween(Instant from, Instant to) {
        return repository.countSavedBetween(from, to);
    }

    public static Map<Integer, Long> countSavedBetweenByProject(Instant from, Instant to, Collection<Integer> ids) {
        return toMap(ids.isEmpty() ? java.util.List.of() : repository.countSavedBetweenByProject(from, to, ids));
    }

    static Map<Integer, Long> toMap(java.util.List<Object[]> rows) {
        Map<Integer, Long> result = new HashMap<>();
        for (Object[] row : rows) {
            result.put((Integer) row[0], ((Number) row[1]).longValue());
        }
        return result;
    }

    public Favourite save() {
        return repository.save(this);
    }

    public FavouriteId getId() {
        return id;
    }

    public User getCustomer() {
        return customer;
    }

    public InteriorDesignProject getProject() {
        return project;
    }

    public Instant getSavedAt() {
        return savedAt;
    }
}

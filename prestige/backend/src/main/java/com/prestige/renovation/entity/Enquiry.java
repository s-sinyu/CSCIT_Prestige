package com.prestige.renovation.entity;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.prestige.renovation.entity.repository.EnquiryRepository;

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

/** Entity: an enquiry sent by a customer about an IDP (FR-CUS-5, FR-ENQ). */
@Entity
@Table(name = "enquiries")
public class Enquiry {

    private static EnquiryRepository repository;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private InteriorDesignProject project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnquiryStatus status = EnquiryStatus.NEW;

    @Column(name = "read_at", columnDefinition = Columns.TIMESTAMPTZ)
    private Instant readAt;

    @Column(name = "replied_at", columnDefinition = Columns.TIMESTAMPTZ)
    private Instant repliedAt;

    @Column(name = "created_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant updatedAt;

    protected Enquiry() {
    }

    public Enquiry(InteriorDesignProject project, User customer, String message) {
        this.project = project;
        this.customer = customer;
        this.message = message;
        this.status = EnquiryStatus.NEW;
    }

    public static void bind(EnquiryRepository repo) {
        repository = repo;
    }

    public static Optional<Enquiry> findById(Integer id) {
        return id == null ? Optional.empty() : repository.findById(id);
    }

    public static Page<Enquiry> findForDesigner(Integer designerId, EnquiryStatus status, Pageable pageable) {
        return status == null
                ? repository.findByProject_Designer_Id(designerId, pageable)
                : repository.findByProject_Designer_IdAndStatus(designerId, status, pageable);
    }

    public static long countForDesigner(Integer designerId) {
        return repository.countByProject_Designer_Id(designerId);
    }

    public static long countForDesigner(Integer designerId, EnquiryStatus status) {
        return repository.countByProject_Designer_IdAndStatus(designerId, status);
    }

    public static long countCreatedBetween(Instant from, Instant to) {
        return repository.countCreatedBetween(from, to);
    }

    public Enquiry save() {
        return repository.save(this);
    }

    // ------------------------------------------------------------------ behaviour

    /** True if the enquiry was sent about one of this designer's IDPs. */
    public boolean belongsToDesigner(Integer designerId) {
        return project.isOwnedBy(designerId);
    }

    /**
     * Applies the status rule NEW -> READ -> REPLIED (NEW may also go straight to REPLIED).
     * Status never reverts. Setting the current status again is a no-op.
     *
     * @return false if the transition is not allowed
     */
    public boolean changeStatus(EnquiryStatus target, Instant when) {
        if (target == null || target == EnquiryStatus.NEW) {
            return status == EnquiryStatus.NEW && target == EnquiryStatus.NEW;
        }
        if (target == status) {
            return true;
        }
        if (target == EnquiryStatus.READ && status == EnquiryStatus.NEW) {
            status = EnquiryStatus.READ;
            readAt = when;
            return true;
        }
        if (target == EnquiryStatus.REPLIED) {
            if (readAt == null) {
                readAt = when;
            }
            status = EnquiryStatus.REPLIED;
            repliedAt = when;
            return true;
        }
        return false;
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

    public Integer getId() {
        return id;
    }

    public InteriorDesignProject getProject() {
        return project;
    }

    public User getCustomer() {
        return customer;
    }

    public String getMessage() {
        return message;
    }

    public EnquiryStatus getStatus() {
        return status;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public Instant getRepliedAt() {
        return repliedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

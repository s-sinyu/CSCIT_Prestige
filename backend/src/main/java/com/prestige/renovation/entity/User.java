package com.prestige.renovation.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.prestige.renovation.entity.repository.UserRepository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.Predicate;

/**
 * Entity: a platform account. One table for all four roles; role-specific data lives in
 * {@link DesignerProfile} and {@link DesignerCompany} (PRD section 5, OI-20).
 */
@Entity
@Table(name = "users")
public class User {

    private static UserRepository repository;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(nullable = false, columnDefinition = Columns.BOOLEAN)
    private boolean deleted;

    @Column(name = "deleted_at", columnDefinition = Columns.TIMESTAMPTZ)
    private Instant deletedAt;

    @Column(name = "created_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant updatedAt;

    protected User() {
    }

    public User(String email, String passwordHash, String fullName, String phone, Role role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.status = AccountStatus.ACTIVE;
    }

    // ------------------------------------------------------------------ data access

    public static void bind(UserRepository repo) {
        repository = repo;
    }

    public static Optional<User> findById(Integer id) {
        return id == null ? Optional.empty() : repository.findById(id);
    }

    public static Optional<User> findByEmail(String email) {
        return repository.findByEmailIgnoreCase(email);
    }

    public static boolean emailTaken(String email) {
        return repository.existsByEmailIgnoreCase(email);
    }

    public static boolean emailTakenByOther(String email, Integer userId) {
        return repository.existsByEmailIgnoreCaseAndIdNot(email, userId);
    }

    public static boolean anyWithRole(Role role) {
        return repository.existsByRole(role);
    }

    /** Admin search (FR-ADM-2): name/email keyword, role, status, optionally including soft-deleted accounts. */
    public static Page<User> search(String keyword, Role role, AccountStatus status, boolean includeDeleted,
                                    Pageable pageable) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!includeDeleted) {
                predicates.add(cb.isFalse(root.get("deleted")));
            }
            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("email")), like),
                        cb.like(cb.lower(root.get("fullName")), like)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return repository.findAll(spec, pageable);
    }

    /** New registrations per role with createdAt in [from, to). */
    public static Map<Role, Long> countRegistrationsByRole(Instant from, Instant to) {
        Map<Role, Long> counts = new java.util.EnumMap<>(Role.class);
        for (Object[] row : repository.countRegistrationsByRole(from, to)) {
            counts.put((Role) row[0], ((Number) row[1]).longValue());
        }
        return counts;
    }

    public User save() {
        return repository.save(this);
    }

    // ------------------------------------------------------------------ behaviour

    /** Active means not suspended and not soft-deleted. Only active users can log in or act. */
    public boolean isActive() {
        return status == AccountStatus.ACTIVE && !deleted;
    }

    public boolean hasRole(Role r) {
        return role == r;
    }

    public void changeStatus(AccountStatus newStatus) {
        this.status = newStatus;
    }

    public void softDelete(Instant when) {
        this.deleted = true;
        this.deletedAt = when;
    }

    public void updateDetails(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;
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

    // ------------------------------------------------------------------ accessors

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Instant getDeletedAt() {
        return deletedAt;
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

package com.prestige.renovation.entity;

import java.time.Instant;
import java.util.Optional;

import com.prestige.renovation.entity.repository.DesignerProfileRepository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/** Entity: an interior designer's public profile (FR-CO-5). One per designer. */
@Entity
@Table(name = "designer_profiles")
public class DesignerProfile {

    private static DesignerProfileRepository repository;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "designer_id", nullable = false, unique = true)
    private User designer;

    private String bio;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    private String specialisation;

    @Column(name = "created_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant updatedAt;

    protected DesignerProfile() {
    }

    public DesignerProfile(User designer) {
        this.designer = designer;
    }

    public static void bind(DesignerProfileRepository repo) {
        repository = repo;
    }

    public static Optional<DesignerProfile> findByDesigner(Integer designerId) {
        return repository.findByDesignerId(designerId);
    }

    public DesignerProfile save() {
        return repository.save(this);
    }

    public void update(String bio, Integer yearsOfExperience, String specialisation) {
        this.bio = bio;
        this.yearsOfExperience = yearsOfExperience;
        this.specialisation = specialisation;
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

    public User getDesigner() {
        return designer;
    }

    public String getBio() {
        return bio;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public String getSpecialisation() {
        return specialisation;
    }
}

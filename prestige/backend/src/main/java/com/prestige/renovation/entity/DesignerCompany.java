package com.prestige.renovation.entity;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.prestige.renovation.entity.repository.DesignerCompanyRepository;

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

/** Entity: company information of an interior designer (FR-CO-1). At most one per designer. */
@Entity
@Table(name = "designer_companies")
public class DesignerCompany {

    private static DesignerCompanyRepository repository;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "designer_id", nullable = false, unique = true)
    private User designer;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(columnDefinition = Columns.TEXT)
    private String description;

    private String address;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "logo_key")
    private String logoKey;

    @Column(name = "created_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant updatedAt;

    protected DesignerCompany() {
    }

    public DesignerCompany(User designer) {
        this.designer = designer;
    }

    public static void bind(DesignerCompanyRepository repo) {
        repository = repo;
    }

    public static Optional<DesignerCompany> findByDesigner(Integer designerId) {
        return repository.findByDesignerId(designerId);
    }

    public static boolean existsForDesigner(Integer designerId) {
        return repository.existsByDesignerId(designerId);
    }

    /** Batch lookup used by list pages to avoid N+1 queries. */
    public static Map<Integer, DesignerCompany> findByDesigners(Collection<Integer> designerIds) {
        Map<Integer, DesignerCompany> result = new HashMap<>();
        if (designerIds.isEmpty()) {
            return result;
        }
        for (DesignerCompany c : repository.findByDesignerIdIn(designerIds)) {
            result.put(c.getDesigner().getId(), c);
        }
        return result;
    }

    public DesignerCompany save() {
        return repository.save(this);
    }

    public void delete() {
        repository.delete(this);
    }

    public void update(String companyName, String registrationNumber, String description, String address,
                       String contactEmail, String contactPhone, String websiteUrl) {
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
        this.description = description;
        this.address = address;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.websiteUrl = websiteUrl;
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

    public String getCompanyName() {
        return companyName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public String getLogoKey() {
        return logoKey;
    }

    public void setLogoKey(String logoKey) {
        this.logoKey = logoKey;
    }
}

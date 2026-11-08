package com.prestige.renovation.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.prestige.renovation.entity.repository.AvailabilityRepository;

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

/** Entity: a designer's availability on one date (FR-AV). No record for a date means AVAILABLE. */
@Entity
@Table(name = "availability")
public class Availability {

    private static AvailabilityRepository repository;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "designer_id", nullable = false)
    private User designer;

    @Column(name = "calendar_date", nullable = false)
    private LocalDate calendarDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailabilityStatus status;

    @Column(name = "updated_at", nullable = false, columnDefinition = Columns.TIMESTAMPTZ)
    private Instant updatedAt;

    protected Availability() {
    }

    public Availability(User designer, LocalDate calendarDate, AvailabilityStatus status) {
        this.designer = designer;
        this.calendarDate = calendarDate;
        this.status = status;
    }

    public static void bind(AvailabilityRepository repo) {
        repository = repo;
    }

    public static Optional<Availability> find(Integer designerId, LocalDate date) {
        return repository.findByDesignerIdAndCalendarDate(designerId, date);
    }

    /** Effective status of every day of the month: stored status, or AVAILABLE when no record exists. */
    public static Map<LocalDate, AvailabilityStatus> monthFor(Integer designerId, YearMonth month) {
        Map<LocalDate, AvailabilityStatus> days = new LinkedHashMap<>();
        for (int d = 1; d <= month.lengthOfMonth(); d++) {
            days.put(month.atDay(d), AvailabilityStatus.AVAILABLE);
        }
        repository.findByDesignerIdAndCalendarDateBetweenOrderByCalendarDateAsc(
                        designerId, month.atDay(1), month.atEndOfMonth())
                .forEach(a -> days.put(a.getCalendarDate(), a.getStatus()));
        return days;
    }

    public Availability save() {
        return repository.save(this);
    }

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }

    public Integer getId() {
        return id;
    }

    public User getDesigner() {
        return designer;
    }

    public LocalDate getCalendarDate() {
        return calendarDate;
    }

    public AvailabilityStatus getStatus() {
        return status;
    }

    public void setStatus(AvailabilityStatus status) {
        this.status = status;
    }
}

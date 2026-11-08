package com.prestige.renovation.entity.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prestige.renovation.entity.Availability;

public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {

    List<Availability> findByDesignerIdAndCalendarDateBetweenOrderByCalendarDateAsc(Integer designerId,
                                                                                     LocalDate from, LocalDate to);

    Optional<Availability> findByDesignerIdAndCalendarDate(Integer designerId, LocalDate date);
}

package com.prestige.renovation.entity.repository;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prestige.renovation.entity.Enquiry;
import com.prestige.renovation.entity.EnquiryStatus;

public interface EnquiryRepository extends JpaRepository<Enquiry, Integer> {

    @EntityGraph(attributePaths = {"customer", "project"})
    Page<Enquiry> findByProject_Designer_Id(Integer designerId, Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "project"})
    Page<Enquiry> findByProject_Designer_IdAndStatus(Integer designerId, EnquiryStatus status, Pageable pageable);

    long countByProject_Designer_Id(Integer designerId);

    long countByProject_Designer_IdAndStatus(Integer designerId, EnquiryStatus status);

    @Query("select count(e) from Enquiry e where e.createdAt >= :from and e.createdAt < :to")
    long countCreatedBetween(@Param("from") Instant from, @Param("to") Instant to);
}

package com.prestige.renovation.entity.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prestige.renovation.entity.Rating;

public interface RatingRepository extends JpaRepository<Rating, Integer> {

    /** Aggregate projection for a designer's average score and rating count. */
    interface RatingStats {
        Double getAverageScore();

        Long getRatingCount();
    }

    @EntityGraph(attributePaths = {"customer", "project"})
    Page<Rating> findByDesignerId(Integer designerId, Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "project"})
    Page<Rating> findByDesignerIdAndProjectId(Integer designerId, Integer projectId, Pageable pageable);

    @Query("select avg(r.score) as averageScore, count(r) as ratingCount from Rating r where r.designer.id = :designerId")
    RatingStats statsForDesigner(@Param("designerId") Integer designerId);

    boolean existsByCustomerIdAndDesignerIdAndProjectIsNull(Integer customerId, Integer designerId);

    boolean existsByCustomerIdAndProjectId(Integer customerId, Integer projectId);
}

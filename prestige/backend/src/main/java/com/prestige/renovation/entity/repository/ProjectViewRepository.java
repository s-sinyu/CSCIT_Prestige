package com.prestige.renovation.entity.repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prestige.renovation.entity.ProjectView;

public interface ProjectViewRepository extends JpaRepository<ProjectView, Integer> {

    /** At most one view per customer per project per day, enforced by a unique constraint (safe under concurrency). */
    @Modifying
    @Query(value = "insert into project_views (project_id, customer_id, view_date, viewed_at) "
            + "values (:projectId, :customerId, :viewDate, :viewedAt) "
            + "on conflict (customer_id, project_id, view_date) do nothing", nativeQuery = true)
    int insertIfAbsent(@Param("projectId") Integer projectId, @Param("customerId") Integer customerId,
                       @Param("viewDate") LocalDate viewDate, @Param("viewedAt") Instant viewedAt);

    long countByProjectId(Integer projectId);

    @Query("select v.project.id, count(v) from ProjectView v where v.project.id in :ids group by v.project.id")
    List<Object[]> countByProjectIds(@Param("ids") Collection<Integer> projectIds);

    @Query("select count(v) from ProjectView v where v.project.designer.id = :designerId and v.project.deleted = false")
    long countForDesigner(@Param("designerId") Integer designerId);

    @Query("select count(v) from ProjectView v where v.viewDate >= :from and v.viewDate <= :to")
    long countBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("select v.project.id, count(v) from ProjectView v where v.viewDate >= :from and v.viewDate <= :to "
            + "group by v.project.id order by count(v) desc, v.project.id asc")
    List<Object[]> topViewedBetween(@Param("from") LocalDate from, @Param("to") LocalDate to, Pageable limit);
}

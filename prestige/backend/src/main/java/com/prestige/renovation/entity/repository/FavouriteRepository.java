package com.prestige.renovation.entity.repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prestige.renovation.entity.Favourite;
import com.prestige.renovation.entity.FavouriteId;

public interface FavouriteRepository extends JpaRepository<Favourite, FavouriteId>, JpaSpecificationExecutor<Favourite> {

    @Override
    @EntityGraph(attributePaths = {"project", "project.category", "project.designer"})
    Page<Favourite> findAll(Specification<Favourite> spec, Pageable pageable);

    /** Idempotent insert: the composite primary key makes a second save a no-op, even under concurrency. */
    @Modifying
    @Query(value = "insert into favourites (customer_id, project_id, saved_at) values (:customerId, :projectId, :savedAt) "
            + "on conflict (customer_id, project_id) do nothing", nativeQuery = true)
    int insertIfAbsent(@Param("customerId") Integer customerId, @Param("projectId") Integer projectId,
                       @Param("savedAt") Instant savedAt);

    long countByProjectId(Integer projectId);

    @Query("select f.project.id, count(f) from Favourite f where f.project.id in :ids group by f.project.id")
    List<Object[]> countByProjectIds(@Param("ids") Collection<Integer> projectIds);

    @Query("select count(f) from Favourite f where f.project.designer.id = :designerId and f.project.deleted = false")
    long countForDesigner(@Param("designerId") Integer designerId);

    @Query("select count(f) from Favourite f where f.savedAt >= :from and f.savedAt < :to")
    long countSavedBetween(@Param("from") Instant from, @Param("to") Instant to);

    @Query("select f.project.id, count(f) from Favourite f where f.savedAt >= :from and f.savedAt < :to "
            + "and f.project.id in :ids group by f.project.id")
    List<Object[]> countSavedBetweenByProject(@Param("from") Instant from, @Param("to") Instant to,
                                              @Param("ids") Collection<Integer> projectIds);
}

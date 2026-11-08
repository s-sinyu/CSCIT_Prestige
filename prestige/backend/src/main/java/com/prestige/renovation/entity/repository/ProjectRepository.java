package com.prestige.renovation.entity.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prestige.renovation.entity.InteriorDesignProject;

public interface ProjectRepository extends JpaRepository<InteriorDesignProject, Integer>,
        JpaSpecificationExecutor<InteriorDesignProject> {

    @Override
    @EntityGraph(attributePaths = {"category", "designer"})
    Page<InteriorDesignProject> findAll(Specification<InteriorDesignProject> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"category", "designer"})
    Optional<InteriorDesignProject> findOne(Specification<InteriorDesignProject> spec);

    @EntityGraph(attributePaths = {"category"})
    Page<InteriorDesignProject> findByDesignerIdAndDeletedFalse(Integer designerId, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "designer"})
    Optional<InteriorDesignProject> findByIdAndDesignerIdAndDeletedFalse(Integer id, Integer designerId);

    long countByDesignerIdAndDeletedFalse(Integer designerId);

    long countByCategoryId(Integer categoryId);

    @Query("select p.category.id, count(p) from InteriorDesignProject p "
            + "where p.createdAt >= :from and p.createdAt < :to group by p.category.id")
    List<Object[]> countCreatedByCategory(@Param("from") Instant from, @Param("to") Instant to);
}

package com.prestige.renovation.entity.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prestige.renovation.entity.ProjectImage;

public interface ProjectImageRepository extends JpaRepository<ProjectImage, Integer> {

    List<ProjectImage> findByProjectIdOrderBySortOrderAscIdAsc(Integer projectId);

    long countByProjectId(Integer projectId);

    @Query("select coalesce(max(i.sortOrder), 0) from ProjectImage i where i.project.id = :projectId")
    int maxSortOrder(@Param("projectId") Integer projectId);

    @Query("select i.project.id, i.fileKey from ProjectImage i where i.cover = true and i.project.id in :ids")
    List<Object[]> findCoverKeys(@Param("ids") Collection<Integer> projectIds);
}

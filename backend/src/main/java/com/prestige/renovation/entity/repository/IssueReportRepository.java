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

import com.prestige.renovation.entity.IssueReport;
import com.prestige.renovation.entity.IssueReportStatus;

public interface IssueReportRepository extends JpaRepository<IssueReport, Integer>, JpaSpecificationExecutor<IssueReport> {

    @Override
    @EntityGraph(attributePaths = {"reporter", "targetProject", "targetDesigner"})
    Page<IssueReport> findAll(Specification<IssueReport> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"targetProject", "targetDesigner"})
    Page<IssueReport> findByReporterId(Integer reporterId, Pageable pageable);

    @EntityGraph(attributePaths = {"reporter", "targetProject", "targetDesigner", "rectifiedBy"})
    Optional<IssueReport> findWithDetailsById(Integer id);

    long countByStatus(IssueReportStatus status);

    @Query("select r.status, count(r) from IssueReport r where r.createdAt >= :from and r.createdAt < :to group by r.status")
    List<Object[]> countCreatedByStatus(@Param("from") Instant from, @Param("to") Instant to);
}

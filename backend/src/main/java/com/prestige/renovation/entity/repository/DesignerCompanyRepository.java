package com.prestige.renovation.entity.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prestige.renovation.entity.DesignerCompany;

public interface DesignerCompanyRepository extends JpaRepository<DesignerCompany, Integer> {

    Optional<DesignerCompany> findByDesignerId(Integer designerId);

    boolean existsByDesignerId(Integer designerId);

    List<DesignerCompany> findByDesignerIdIn(Collection<Integer> designerIds);
}

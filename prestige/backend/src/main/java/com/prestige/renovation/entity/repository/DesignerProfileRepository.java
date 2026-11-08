package com.prestige.renovation.entity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prestige.renovation.entity.DesignerProfile;

public interface DesignerProfileRepository extends JpaRepository<DesignerProfile, Integer> {

    Optional<DesignerProfile> findByDesignerId(Integer designerId);
}

package com.prestige.renovation.entity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prestige.renovation.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findByActiveTrueOrderByNameAsc();

    List<Category> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);
}

package com.prestige.renovation.boundary.category;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.control.category.ViewActiveCategoriesController;
import com.prestige.renovation.dto.category.CategoryResponse;

/** Boundary for user story: Anyone lists active categories. */
@RestController
public class ViewActiveCategoriesBoundary {

    private final ViewActiveCategoriesController controller;

    public ViewActiveCategoriesBoundary(ViewActiveCategoriesController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/categories")
    public List<CategoryResponse> list() {
        return controller.list();
    }
}

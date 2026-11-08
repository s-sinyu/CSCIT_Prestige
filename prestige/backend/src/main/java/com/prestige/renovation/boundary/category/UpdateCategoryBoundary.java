package com.prestige.renovation.boundary.category;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.control.category.UpdateCategoryController;
import com.prestige.renovation.dto.category.CategoryRequest;
import com.prestige.renovation.dto.category.CategoryResponse;

import jakarta.validation.Valid;

/** Boundary for user story: Platform Management edits a category. */
@RestController
public class UpdateCategoryBoundary {

    private final UpdateCategoryController controller;

    public UpdateCategoryBoundary(UpdateCategoryController controller) {
        this.controller = controller;
    }

    @PutMapping("/api/management/categories/{id}")
    public CategoryResponse update(@PathVariable Integer id, @Valid @RequestBody CategoryRequest request) {
        return controller.update(id, request);
    }
}

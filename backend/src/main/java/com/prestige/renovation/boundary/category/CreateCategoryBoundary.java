package com.prestige.renovation.boundary.category;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.control.category.CreateCategoryController;
import com.prestige.renovation.dto.category.CategoryRequest;
import com.prestige.renovation.dto.category.CategoryResponse;

import jakarta.validation.Valid;

/** Boundary for user story: Platform Management creates a category. */
@RestController
public class CreateCategoryBoundary {

    private final CreateCategoryController controller;

    public CreateCategoryBoundary(CreateCategoryController controller) {
        this.controller = controller;
    }

    @PostMapping("/api/management/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@Valid @RequestBody CategoryRequest request) {
        return controller.create(request);
    }
}

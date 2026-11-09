package com.prestige.renovation.boundary.category;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.control.category.ChangeCategoryActiveController;
import com.prestige.renovation.dto.category.ActiveRequest;
import com.prestige.renovation.dto.category.CategoryResponse;

import jakarta.validation.Valid;

/** Boundary for user story: Platform Management activates or deactivates a category. */
@RestController
public class ChangeCategoryActiveBoundary {

    private final ChangeCategoryActiveController controller;

    public ChangeCategoryActiveBoundary(ChangeCategoryActiveController controller) {
        this.controller = controller;
    }

    @PatchMapping("/api/management/categories/{id}/active")
    public CategoryResponse setActive(@PathVariable Integer id, @Valid @RequestBody ActiveRequest request) {
        return controller.setActive(id, request.active());
    }
}

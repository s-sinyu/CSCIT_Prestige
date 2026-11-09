package com.prestige.renovation.boundary.category;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.control.category.DeleteCategoryController;

/** Boundary for user story: Platform Management deletes an unused category. */
@RestController
public class DeleteCategoryBoundary {

    private final DeleteCategoryController controller;

    public DeleteCategoryBoundary(DeleteCategoryController controller) {
        this.controller = controller;
    }

    @DeleteMapping("/api/management/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        controller.delete(id);
    }
}

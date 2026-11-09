package com.prestige.renovation.boundary.category;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.control.category.ViewCategoriesController;
import com.prestige.renovation.dto.PageResponse;
import com.prestige.renovation.dto.category.CategoryResponse;

/** Boundary for user story: Platform Management views all categories. */
@RestController
public class ViewCategoriesBoundary {

    private final ViewCategoriesController controller;

    public ViewCategoriesBoundary(ViewCategoriesController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/management/categories")
    public PageResponse<CategoryResponse> list(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return controller.list(pageable);
    }
}

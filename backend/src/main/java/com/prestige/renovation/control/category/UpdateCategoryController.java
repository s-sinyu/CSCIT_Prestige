package com.prestige.renovation.control.category;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.category.CategoryRequest;
import com.prestige.renovation.dto.category.CategoryResponse;
import com.prestige.renovation.entity.Category;

/** Control for user story: Platform Management edits a category (FR-PM-1). */
@Service
public class UpdateCategoryController {

    @Transactional
    public CategoryResponse update(Integer categoryId, CategoryRequest request) {
        Category category = Category.findById(categoryId)
                .orElseThrow(() -> ApiException.notFound("Category not found"));
        String name = request.name().trim();
        if (Category.nameTakenByOther(name, categoryId)) {
            throw ApiException.conflict("name", "A category with this name already exists");
        }
        boolean active = request.active() == null ? category.isActive() : request.active();
        category.update(name, ControlSupport.clean(request.description()), active);
        return CategoryResponse.from(category.save());
    }
}

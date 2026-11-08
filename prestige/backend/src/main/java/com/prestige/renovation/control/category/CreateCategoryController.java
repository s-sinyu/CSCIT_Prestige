package com.prestige.renovation.control.category;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.category.CategoryRequest;
import com.prestige.renovation.dto.category.CategoryResponse;
import com.prestige.renovation.entity.Category;

/** Control for user story: Platform Management creates an IDP category (FR-PM-1). */
@Service
public class CreateCategoryController {

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String name = request.name().trim();
        if (Category.nameTaken(name)) {
            throw ApiException.conflict("name", "A category with this name already exists");
        }
        boolean active = request.active() == null || request.active();
        Category category = new Category(name, ControlSupport.clean(request.description()), active);
        return CategoryResponse.from(category.save());
    }
}

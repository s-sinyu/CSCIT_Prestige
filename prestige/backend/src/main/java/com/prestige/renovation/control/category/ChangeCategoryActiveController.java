package com.prestige.renovation.control.category;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.dto.category.CategoryResponse;
import com.prestige.renovation.entity.Category;

/** Control for user story: Platform Management activates or deactivates a category (FR-PM-2, FR-PM-3). */
@Service
public class ChangeCategoryActiveController {

    @Transactional
    public CategoryResponse setActive(Integer categoryId, boolean active) {
        Category category = Category.findById(categoryId)
                .orElseThrow(() -> ApiException.notFound("Category not found"));
        category.setActive(active);
        return CategoryResponse.from(category.save());
    }
}

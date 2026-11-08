package com.prestige.renovation.control.category;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.entity.Category;

/**
 * Control for user story: Platform Management deletes a category (FR-PM-2).
 * A category used by any IDP (including soft-deleted ones) cannot be deleted; deactivate it instead.
 */
@Service
public class DeleteCategoryController {

    @Transactional
    public void delete(Integer categoryId) {
        Category category = Category.findById(categoryId)
                .orElseThrow(() -> ApiException.notFound("Category not found"));
        long used = category.usageCount();
        if (used > 0) {
            throw ApiException.conflict("This category is used by " + used
                    + (used == 1 ? " project" : " projects") + " and cannot be deleted. Deactivate it instead.");
        }
        category.delete();
    }
}

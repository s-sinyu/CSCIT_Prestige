package com.prestige.renovation.control.category;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.dto.PageResponse;
import com.prestige.renovation.dto.category.CategoryResponse;
import com.prestige.renovation.entity.Category;

/** Control for user story: Platform Management views all categories, active and inactive (FR-PM-1). */
@Service
public class ViewCategoriesController {

    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> list(Pageable pageable) {
        return PageResponse.of(Category.findAll(pageable), CategoryResponse::from);
    }
}

package com.prestige.renovation.control.category;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.dto.category.CategoryResponse;
import com.prestige.renovation.entity.Category;

/** Control for user story: Anyone lists the active categories for search filters and IDP forms (FR-PM-4). */
@Service
public class ViewActiveCategoriesController {

    @Transactional(readOnly = true)
    public List<CategoryResponse> list() {
        return Category.findActive().stream().map(CategoryResponse::from).toList();
    }
}

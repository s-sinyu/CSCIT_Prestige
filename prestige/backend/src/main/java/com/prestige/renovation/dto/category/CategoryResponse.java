package com.prestige.renovation.dto.category;

import com.prestige.renovation.entity.Category;

public record CategoryResponse(Integer id, String name, String description, boolean active) {

    public static CategoryResponse from(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription(), c.isActive());
    }
}

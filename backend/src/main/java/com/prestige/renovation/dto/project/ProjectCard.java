package com.prestige.renovation.dto.project;

import java.math.BigDecimal;
import java.time.Instant;

import com.prestige.renovation.entity.ProjectStatus;

/** Search result / favourites card (FR-CUS-2). */
public record ProjectCard(Integer id, String title, Integer categoryId, String categoryName, ProjectStatus status,
                          BigDecimal budget, Integer designerId, String designerName, String coverImageUrl,
                          Instant createdAt) {
}

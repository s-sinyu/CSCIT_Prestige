package com.prestige.renovation.dto.project;

import java.math.BigDecimal;
import java.time.Instant;

import com.prestige.renovation.entity.ProjectStatus;

/** A row in the designer's "My Projects" list including interest figures (FR-ANA-3). */
public record DesignerProjectSummary(Integer id, String title, String categoryName, ProjectStatus status,
                                     BigDecimal budget, String coverImageUrl, long views, long shortlists,
                                     Instant createdAt, Instant updatedAt) {
}

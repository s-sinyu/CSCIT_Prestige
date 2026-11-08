package com.prestige.renovation.dto.project;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.prestige.renovation.dto.designer.DesignerInfo;
import com.prestige.renovation.entity.ProjectStatus;

/**
 * Public IDP detail (FR-CUS-3). {@code favourited} is only set for a logged-in customer; null otherwise.
 */
public record ProjectDetail(Integer id, String title, String description, Integer categoryId, String categoryName,
                            ProjectStatus status, BigDecimal budget, LocalDate startDate, LocalDate endDate,
                            String location, List<ImageResponse> images, Instant createdAt,
                            DesignerInfo designer, Boolean favourited) {
}

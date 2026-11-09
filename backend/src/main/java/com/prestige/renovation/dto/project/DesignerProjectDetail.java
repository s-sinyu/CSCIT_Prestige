package com.prestige.renovation.dto.project;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.prestige.renovation.entity.ProjectStatus;

/** The designer's own view of an IDP, including views and shortlists. */
public record DesignerProjectDetail(Integer id, String title, String description, Integer categoryId,
                                    String categoryName, boolean categoryActive, ProjectStatus status,
                                    BigDecimal budget, LocalDate startDate, LocalDate endDate, String location,
                                    List<ImageResponse> images, long views, long shortlists,
                                    Instant createdAt, Instant updatedAt) {
}

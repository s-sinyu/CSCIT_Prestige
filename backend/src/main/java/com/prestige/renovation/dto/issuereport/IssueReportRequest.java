package com.prestige.renovation.dto.issuereport;

import com.prestige.renovation.entity.IssueReportType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** New issue report (FR-RPT-1). For IDP_OR_DESIGNER at least one target is required (checked in control). */
public record IssueReportRequest(
        @NotNull(message = "Type is required") IssueReportType type,
        Integer targetProjectId,
        Integer targetDesignerId,
        @NotBlank(message = "Title is required") @Size(max = 150, message = "Title must be at most 150 characters") String title,
        @NotBlank(message = "Description is required") @Size(max = 2000, message = "Description must be at most 2000 characters") String description) {
}

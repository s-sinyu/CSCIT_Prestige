package com.prestige.renovation.dto.project;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prestige.renovation.entity.ProjectStatus;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** IDP fields (FR-IDP-1). */
public record ProjectRequest(
        @NotBlank(message = "Title is required") @Size(max = 150, message = "Title must be at most 150 characters") String title,
        @Size(max = 10000, message = "Description must be at most 10000 characters") String description,
        @NotNull(message = "Category is required") Integer categoryId,
        @NotNull(message = "Status is required") ProjectStatus status,
        @DecimalMin(value = "0.00", message = "Budget must be 0 or more")
        @Digits(integer = 10, fraction = 2, message = "Budget must have at most 2 decimal places") BigDecimal budget,
        LocalDate startDate,
        LocalDate endDate,
        @Size(max = 150, message = "Location must be at most 150 characters") String location) {

    /** Reported as field error "endDateValid". */
    @JsonIgnore
    @AssertTrue(message = "End date must not be before start date")
    public boolean isEndDateValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }
}

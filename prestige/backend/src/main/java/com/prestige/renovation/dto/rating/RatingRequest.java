package com.prestige.renovation.dto.rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** New rating (FR-RT-1). projectId is optional; when set the IDP must belong to the designer. */
public record RatingRequest(
        @NotNull(message = "Designer is required") Integer designerId,
        Integer projectId,
        @NotNull(message = "Score is required") @Min(value = 1, message = "Score must be between 1 and 5")
        @Max(value = 5, message = "Score must be between 1 and 5") Integer score,
        @Size(max = 1000, message = "Comment must be at most 1000 characters") String comment) {
}

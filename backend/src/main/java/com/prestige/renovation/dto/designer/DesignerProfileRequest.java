package com.prestige.renovation.dto.designer;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/** Designer profile fields (FR-CO-5); all optional. */
public record DesignerProfileRequest(
        @Size(max = 2000, message = "Bio must be at most 2000 characters") String bio,
        @Min(value = 0, message = "Years of experience must be between 0 and 80")
        @Max(value = 80, message = "Years of experience must be between 0 and 80") Integer yearsOfExperience,
        @Size(max = 150, message = "Specialisation must be at most 150 characters") String specialisation) {
}

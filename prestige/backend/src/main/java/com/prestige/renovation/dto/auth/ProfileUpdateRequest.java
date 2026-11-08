package com.prestige.renovation.dto.auth;

import com.prestige.renovation.dto.ValidationRules;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @NotBlank(message = "Full name is required") @Size(max = 150, message = "Full name must be at most 150 characters") String fullName,
        @Pattern(regexp = ValidationRules.PHONE_REGEX, message = ValidationRules.PHONE_MESSAGE) String phone) {
}

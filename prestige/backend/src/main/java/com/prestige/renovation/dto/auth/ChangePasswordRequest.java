package com.prestige.renovation.dto.auth;

import com.prestige.renovation.dto.ValidationRules;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordRequest(
        @NotBlank(message = "Current password is required") String currentPassword,
        @NotBlank(message = "New password is required")
        @Pattern(regexp = ValidationRules.PASSWORD_REGEX, message = ValidationRules.PASSWORD_MESSAGE) String newPassword) {
}

package com.prestige.renovation.dto.auth;

import com.prestige.renovation.dto.ValidationRules;
import com.prestige.renovation.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Email is required") @Email(message = "Email format is invalid") @Size(max = 255) String email,
        @NotBlank(message = "Password is required")
        @Pattern(regexp = ValidationRules.PASSWORD_REGEX, message = ValidationRules.PASSWORD_MESSAGE) String password,
        @NotBlank(message = "Full name is required") @Size(max = 150, message = "Full name must be at most 150 characters") String fullName,
        @Pattern(regexp = ValidationRules.PHONE_REGEX, message = ValidationRules.PHONE_MESSAGE) String phone,
        @NotNull(message = "Please choose an account type") Role role) {
}

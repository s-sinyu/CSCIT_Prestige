package com.prestige.renovation.dto.company;

import com.prestige.renovation.dto.ValidationRules;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Company information fields (FR-CO-1). */
public record CompanyRequest(
        @NotBlank(message = "Company name is required") @Size(max = 150, message = "Company name must be at most 150 characters") String companyName,
        @Size(max = 50, message = "Registration number must be at most 50 characters") String registrationNumber,
        @Size(max = 5000, message = "Description must be at most 5000 characters") String description,
        @Size(max = 255, message = "Address must be at most 255 characters") String address,
        @Email(message = "Contact email format is invalid") @Size(max = 255) String contactEmail,
        @Pattern(regexp = ValidationRules.PHONE_REGEX, message = ValidationRules.PHONE_MESSAGE) String contactPhone,
        @Pattern(regexp = "^$|^https?://[^\\s/$.?#].[^\\s]*$", message = "Website must be a valid http(s) URL")
        @Size(max = 255) String websiteUrl) {
}

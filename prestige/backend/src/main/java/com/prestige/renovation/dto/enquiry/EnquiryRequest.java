package com.prestige.renovation.dto.enquiry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EnquiryRequest(
        @NotBlank(message = "Message is required") @Size(max = 2000, message = "Message must be at most 2000 characters") String message) {
}

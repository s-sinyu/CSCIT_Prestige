package com.prestige.renovation.dto.enquiry;

import com.prestige.renovation.entity.EnquiryStatus;

import jakarta.validation.constraints.NotNull;

public record EnquiryStatusRequest(@NotNull(message = "Status is required") EnquiryStatus status) {
}

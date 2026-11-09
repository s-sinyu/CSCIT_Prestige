package com.prestige.renovation.dto.availability;

import com.prestige.renovation.entity.AvailabilityStatus;

import jakarta.validation.constraints.NotNull;

public record AvailabilityRequest(@NotNull(message = "Status is required") AvailabilityStatus status) {
}

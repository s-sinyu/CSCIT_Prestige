package com.prestige.renovation.dto.category;

import jakarta.validation.constraints.NotNull;

public record ActiveRequest(@NotNull(message = "active is required") Boolean active) {
}

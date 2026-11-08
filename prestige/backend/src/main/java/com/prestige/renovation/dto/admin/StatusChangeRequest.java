package com.prestige.renovation.dto.admin;

import com.prestige.renovation.entity.AccountStatus;

import jakarta.validation.constraints.NotNull;

public record StatusChangeRequest(@NotNull(message = "Status is required") AccountStatus status) {
}

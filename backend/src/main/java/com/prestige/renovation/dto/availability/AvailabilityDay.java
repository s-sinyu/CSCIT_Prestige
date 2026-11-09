package com.prestige.renovation.dto.availability;

import java.time.LocalDate;

import com.prestige.renovation.entity.AvailabilityStatus;

public record AvailabilityDay(LocalDate date, AvailabilityStatus status) {
}

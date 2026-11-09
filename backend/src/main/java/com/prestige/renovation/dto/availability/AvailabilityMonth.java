package com.prestige.renovation.dto.availability;

import java.time.LocalDate;
import java.util.List;

/** Effective availability of every day in a month; {@code today} lets the UI disable past dates. */
public record AvailabilityMonth(Integer designerId, String designerName, String month, LocalDate today,
                                List<AvailabilityDay> days) {
}

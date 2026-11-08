package com.prestige.renovation.control.availability;

import java.time.LocalDate;
import java.time.YearMonth;

import com.prestige.renovation.dto.availability.AvailabilityDay;
import com.prestige.renovation.dto.availability.AvailabilityMonth;
import com.prestige.renovation.entity.Availability;
import com.prestige.renovation.entity.User;

/** Builds the month view used by both the designer's and the customer's calendar. */
final class AvailabilityMonthAssembler {

    private AvailabilityMonthAssembler() {
    }

    static AvailabilityMonth build(User designer, YearMonth month, LocalDate today) {
        var days = Availability.monthFor(designer.getId(), month).entrySet().stream()
                .map(e -> new AvailabilityDay(e.getKey(), e.getValue())).toList();
        return new AvailabilityMonth(designer.getId(), designer.getFullName(), month.toString(), today, days);
    }
}

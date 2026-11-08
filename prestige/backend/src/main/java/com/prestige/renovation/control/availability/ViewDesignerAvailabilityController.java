package com.prestige.renovation.control.availability;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.availability.AvailabilityMonth;

/**
 * Control for user story: Customer views a designer's availability calendar by month (FR-AV-5).
 * Not available for suspended or deleted designers.
 */
@Service
public class ViewDesignerAvailabilityController {

    private final Clock clock;

    public ViewDesignerAvailabilityController(Clock clock) {
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public AvailabilityMonth view(Integer designerId, YearMonth month) {
        var designer = ControlSupport.requireActiveDesigner(designerId);
        LocalDate today = LocalDate.now(clock);
        return AvailabilityMonthAssembler.build(designer, month == null ? YearMonth.from(today) : month, today);
    }
}

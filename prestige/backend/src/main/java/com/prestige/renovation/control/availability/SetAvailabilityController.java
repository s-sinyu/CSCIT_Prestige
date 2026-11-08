package com.prestige.renovation.control.availability;

import java.time.Clock;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.availability.AvailabilityDay;
import com.prestige.renovation.entity.Availability;
import com.prestige.renovation.entity.AvailabilityStatus;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;

/**
 * Control for user story: Interior Designer sets a date as AVAILABLE or UNAVAILABLE (FR-AV-1, FR-AV-3).
 * Only today or future dates (Asia/Singapore) can be set; one record per designer per date.
 */
@Service
public class SetAvailabilityController {

    private final Clock clock;

    public SetAvailabilityController(Clock clock) {
        this.clock = clock;
    }

    @Transactional
    public AvailabilityDay set(Integer designerId, LocalDate date, AvailabilityStatus status) {
        User designer = ControlSupport.requireRole(designerId, Role.INTERIOR_DESIGNER);
        if (date.isBefore(LocalDate.now(clock))) {
            throw ApiException.badRequest("date", "Past dates cannot be changed");
        }
        Availability record = Availability.find(designerId, date)
                .orElseGet(() -> new Availability(designer, date, status));
        record.setStatus(status);
        record.save();
        return new AvailabilityDay(date, status);
    }
}

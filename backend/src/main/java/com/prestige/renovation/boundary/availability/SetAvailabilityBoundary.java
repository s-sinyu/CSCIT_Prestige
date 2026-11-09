package com.prestige.renovation.boundary.availability;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.availability.SetAvailabilityController;
import com.prestige.renovation.dto.availability.AvailabilityDay;
import com.prestige.renovation.dto.availability.AvailabilityRequest;

import jakarta.validation.Valid;

/** Boundary for user story: Interior Designer sets availability for a date. */
@RestController
public class SetAvailabilityBoundary {

    private final SetAvailabilityController controller;

    public SetAvailabilityBoundary(SetAvailabilityController controller) {
        this.controller = controller;
    }

    @PutMapping("/api/designer/availability/{date}")
    public AvailabilityDay set(@AuthenticationPrincipal AuthUser me,
                               @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                               @Valid @RequestBody AvailabilityRequest request) {
        return controller.set(me.id(), date, request.status());
    }
}

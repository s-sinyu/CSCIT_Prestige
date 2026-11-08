package com.prestige.renovation.boundary.availability;

import java.time.YearMonth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.availability.ViewOwnAvailabilityController;
import com.prestige.renovation.dto.availability.AvailabilityMonth;

/** Boundary for user story: Interior Designer views own availability calendar. */
@RestController
public class ViewOwnAvailabilityBoundary {

    private final ViewOwnAvailabilityController controller;

    public ViewOwnAvailabilityBoundary(ViewOwnAvailabilityController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/designer/availability")
    public AvailabilityMonth view(@AuthenticationPrincipal AuthUser me,
                                  @RequestParam(required = false) String month) {
        return controller.view(me.id(), month == null || month.isBlank() ? null : YearMonth.parse(month));
    }
}

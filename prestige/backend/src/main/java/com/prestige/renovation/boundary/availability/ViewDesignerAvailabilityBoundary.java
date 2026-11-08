package com.prestige.renovation.boundary.availability;

import java.time.YearMonth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.control.availability.ViewDesignerAvailabilityController;
import com.prestige.renovation.dto.availability.AvailabilityMonth;

/** Boundary for user story: Customer views a designer's availability calendar. */
@RestController
public class ViewDesignerAvailabilityBoundary {

    private final ViewDesignerAvailabilityController controller;

    public ViewDesignerAvailabilityBoundary(ViewDesignerAvailabilityController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/customer/designers/{id}/availability")
    public AvailabilityMonth view(@PathVariable Integer id,
                                  @RequestParam(required = false) String month) {
        return controller.view(id, month == null || month.isBlank() ? null : YearMonth.parse(month));
    }
}

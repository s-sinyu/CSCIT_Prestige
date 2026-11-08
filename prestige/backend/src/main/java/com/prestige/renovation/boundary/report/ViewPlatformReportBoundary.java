package com.prestige.renovation.boundary.report;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.control.report.ViewPlatformReportController;
import com.prestige.renovation.dto.report.PlatformReport;
import com.prestige.renovation.entity.ReportPeriodType;

/** Boundary for user story: Platform Management views the daily, weekly or monthly report. */
@RestController
public class ViewPlatformReportBoundary {

    private final ViewPlatformReportController controller;

    public ViewPlatformReportBoundary(ViewPlatformReportController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/management/reports")
    public PlatformReport view(@RequestParam(defaultValue = "DAILY") ReportPeriodType period,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return controller.view(period, date);
    }
}

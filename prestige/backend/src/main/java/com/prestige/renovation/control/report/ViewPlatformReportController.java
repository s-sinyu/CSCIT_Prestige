package com.prestige.renovation.control.report;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.dto.report.PlatformReport;
import com.prestige.renovation.entity.ReportPeriodType;

/** Control for user stories: Platform Management views the daily / weekly / monthly report (FR-PM-5 to 7). */
@Service
public class ViewPlatformReportController {

    private final PlatformReportGenerator generator;

    public ViewPlatformReportController(PlatformReportGenerator generator) {
        this.generator = generator;
    }

    @Transactional(readOnly = true)
    public PlatformReport view(ReportPeriodType period, LocalDate date) {
        return generator.generate(period, date);
    }
}

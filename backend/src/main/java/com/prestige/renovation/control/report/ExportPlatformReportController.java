package com.prestige.renovation.control.report;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.dto.report.PlatformReport;
import com.prestige.renovation.entity.ReportPeriodType;

/** Control for user story: Platform Management downloads the report as a CSV file (FR-PM-8). */
@Service
public class ExportPlatformReportController {

    private final PlatformReportGenerator generator;

    public ExportPlatformReportController(PlatformReportGenerator generator) {
        this.generator = generator;
    }

    /** CSV text plus the download file name. */
    public record CsvExport(String fileName, String content) {
    }

    @Transactional(readOnly = true)
    public CsvExport export(ReportPeriodType period, LocalDate date) {
        PlatformReport report = generator.generate(period, date);
        return new CsvExport(ReportCsvRenderer.fileName(report), ReportCsvRenderer.render(report));
    }
}

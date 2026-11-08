package com.prestige.renovation.boundary.report;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.control.report.ExportPlatformReportController;
import com.prestige.renovation.entity.ReportPeriodType;

/** Boundary for user story: Platform Management downloads the report as CSV. */
@RestController
public class ExportPlatformReportBoundary {

    private final ExportPlatformReportController controller;

    public ExportPlatformReportBoundary(ExportPlatformReportController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/management/reports/export")
    public ResponseEntity<byte[]> export(@RequestParam(defaultValue = "DAILY") ReportPeriodType period,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        ExportPlatformReportController.CsvExport csv = controller.export(period, date);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(csv.fileName()).build().toString())
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv.content().getBytes(StandardCharsets.UTF_8));
    }
}

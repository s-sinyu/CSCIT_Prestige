package com.prestige.renovation.boundary.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.control.admin.ViewIssueReportController;
import com.prestige.renovation.dto.issuereport.IssueReportResponse;

/** Boundary for user story: User Admin views one issue report. */
@RestController
public class ViewIssueReportBoundary {

    private final ViewIssueReportController controller;

    public ViewIssueReportBoundary(ViewIssueReportController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/admin/issue-reports/{id}")
    public IssueReportResponse view(@PathVariable Integer id) {
        return controller.view(id);
    }
}

package com.prestige.renovation.boundary.issuereport;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.issuereport.ViewMyIssueReportController;
import com.prestige.renovation.dto.issuereport.IssueReportResponse;

/** Boundary for user story: Reporter views one own issue report. */
@RestController
public class ViewMyIssueReportBoundary {

    private final ViewMyIssueReportController controller;

    public ViewMyIssueReportBoundary(ViewMyIssueReportController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/issue-reports/mine/{id}")
    public IssueReportResponse view(@AuthenticationPrincipal AuthUser me, @PathVariable Integer id) {
        return controller.view(me.id(), id);
    }
}

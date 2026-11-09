package com.prestige.renovation.boundary.issuereport;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.issuereport.ViewMyIssueReportsController;
import com.prestige.renovation.dto.PageResponse;
import com.prestige.renovation.dto.issuereport.IssueReportResponse;

/** Boundary for user story: Reporter views own issue reports. */
@RestController
public class ViewMyIssueReportsBoundary {

    private final ViewMyIssueReportsController controller;

    public ViewMyIssueReportsBoundary(ViewMyIssueReportsController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/issue-reports/mine")
    public PageResponse<IssueReportResponse> list(@AuthenticationPrincipal AuthUser me,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return controller.list(me.id(), pageable);
    }
}

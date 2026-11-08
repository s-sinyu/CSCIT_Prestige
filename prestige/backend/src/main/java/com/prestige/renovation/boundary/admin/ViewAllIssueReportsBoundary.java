package com.prestige.renovation.boundary.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.control.admin.ViewAllIssueReportsController;
import com.prestige.renovation.dto.PageResponse;
import com.prestige.renovation.dto.issuereport.IssueReportResponse;
import com.prestige.renovation.entity.IssueReportStatus;
import com.prestige.renovation.entity.IssueReportType;

/** Boundary for user story: User Admin views all issue reports. */
@RestController
public class ViewAllIssueReportsBoundary {

    private final ViewAllIssueReportsController controller;

    public ViewAllIssueReportsBoundary(ViewAllIssueReportsController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/admin/issue-reports")
    public PageResponse<IssueReportResponse> list(@RequestParam(required = false) IssueReportStatus status,
                                                  @RequestParam(required = false) IssueReportType type,
                                                  @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return controller.list(status, type, pageable);
    }
}

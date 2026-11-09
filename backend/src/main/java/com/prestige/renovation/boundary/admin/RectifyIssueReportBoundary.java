package com.prestige.renovation.boundary.admin;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.admin.RectifyIssueReportController;
import com.prestige.renovation.dto.issuereport.IssueReportResponse;

/** Boundary for user story: User Admin marks an issue report as rectified. */
@RestController
public class RectifyIssueReportBoundary {

    private final RectifyIssueReportController controller;

    public RectifyIssueReportBoundary(RectifyIssueReportController controller) {
        this.controller = controller;
    }

    @PatchMapping("/api/admin/issue-reports/{id}/rectify")
    public IssueReportResponse rectify(@AuthenticationPrincipal AuthUser me, @PathVariable Integer id) {
        return controller.rectify(me.id(), id);
    }
}

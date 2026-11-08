package com.prestige.renovation.boundary.issuereport;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.issuereport.SubmitIssueReportController;
import com.prestige.renovation.dto.issuereport.IssueReportRequest;
import com.prestige.renovation.dto.issuereport.IssueReportResponse;

import jakarta.validation.Valid;

/** Boundary for user story: Customer or Interior Designer submits an issue report. */
@RestController
public class SubmitIssueReportBoundary {

    private final SubmitIssueReportController controller;

    public SubmitIssueReportBoundary(SubmitIssueReportController controller) {
        this.controller = controller;
    }

    @PostMapping("/api/issue-reports")
    @ResponseStatus(HttpStatus.CREATED)
    public IssueReportResponse submit(@AuthenticationPrincipal AuthUser me, @Valid @RequestBody IssueReportRequest request) {
        return controller.submit(me.id(), request);
    }
}

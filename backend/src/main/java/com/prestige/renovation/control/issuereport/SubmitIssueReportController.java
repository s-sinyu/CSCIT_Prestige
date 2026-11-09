package com.prestige.renovation.control.issuereport;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.issuereport.IssueReportRequest;
import com.prestige.renovation.dto.issuereport.IssueReportResponse;
import com.prestige.renovation.entity.InteriorDesignProject;
import com.prestige.renovation.entity.IssueReport;
import com.prestige.renovation.entity.IssueReportType;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;

/**
 * Control for user story: Customer or Interior Designer reports a problem (FR-RPT-1, FR-RPT-6).
 * An IDP_OR_DESIGNER report needs a target IDP or designer; a PLATFORM_ISSUE report has no target.
 */
@Service
public class SubmitIssueReportController {

    @Transactional
    public IssueReportResponse submit(Integer reporterId, IssueReportRequest request) {
        User reporter = ControlSupport.requireActiveUser(reporterId);
        if (!reporter.hasRole(Role.CUSTOMER) && !reporter.hasRole(Role.INTERIOR_DESIGNER)) {
            throw ApiException.forbidden("Only customers and interior designers can submit reports");
        }

        InteriorDesignProject project = null;
        User designer = null;
        if (request.type() == IssueReportType.IDP_OR_DESIGNER) {
            if (request.targetProjectId() == null && request.targetDesignerId() == null) {
                throw ApiException.badRequest("targetProjectId", "Choose the project or designer you are reporting");
            }
            if (request.targetProjectId() != null) {
                project = InteriorDesignProject.findAnyById(request.targetProjectId())
                        .filter(p -> !p.isDeleted())
                        .orElseThrow(() -> ApiException.badRequest("targetProjectId", "Project not found"));
            }
            if (request.targetDesignerId() != null) {
                designer = User.findById(request.targetDesignerId())
                        .filter(u -> u.hasRole(Role.INTERIOR_DESIGNER))
                        .orElseThrow(() -> ApiException.badRequest("targetDesignerId", "Designer not found"));
            }
        }

        IssueReport report = new IssueReport(reporter, request.type(), project, designer,
                request.title().trim(), request.description().trim());
        return IssueReportResponse.forReporter(report.save());
    }
}

package com.prestige.renovation.control.admin;

import java.time.Clock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.issuereport.IssueReportResponse;
import com.prestige.renovation.entity.IssueReport;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;

/** Control for user story: User Admin marks an issue report as RECTIFIED, recording who and when (FR-RPT-5). */
@Service
public class RectifyIssueReportController {

    private final Clock clock;

    public RectifyIssueReportController(Clock clock) {
        this.clock = clock;
    }

    @Transactional
    public IssueReportResponse rectify(Integer adminId, Integer reportId) {
        User admin = ControlSupport.requireRole(adminId, Role.USER_ADMIN);
        IssueReport report = IssueReport.findById(reportId)
                .orElseThrow(() -> ApiException.notFound("Issue report not found"));
        if (!report.rectify(admin, clock.instant())) {
            throw ApiException.conflict("This report has already been rectified");
        }
        return IssueReportResponse.forAdmin(report.save());
    }
}

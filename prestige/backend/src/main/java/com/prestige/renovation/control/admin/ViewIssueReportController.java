package com.prestige.renovation.control.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.dto.issuereport.IssueReportResponse;
import com.prestige.renovation.entity.IssueReport;

/** Control for user story: User Admin views an issue report with reporter details and target (FR-RPT-4). */
@Service
public class ViewIssueReportController {

    @Transactional(readOnly = true)
    public IssueReportResponse view(Integer reportId) {
        return IssueReport.findById(reportId).map(IssueReportResponse::forAdmin)
                .orElseThrow(() -> ApiException.notFound("Issue report not found"));
    }
}

package com.prestige.renovation.control.issuereport;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.dto.issuereport.IssueReportResponse;
import com.prestige.renovation.entity.IssueReport;

/** Control for user story: Reporter views one of own issue reports; others' reports are 404 (FR-RPT-3). */
@Service
public class ViewMyIssueReportController {

    @Transactional(readOnly = true)
    public IssueReportResponse view(Integer reporterId, Integer reportId) {
        return IssueReport.findById(reportId)
                .filter(r -> r.isReportedBy(reporterId))
                .map(IssueReportResponse::forReporter)
                .orElseThrow(() -> ApiException.notFound("Issue report not found"));
    }
}

package com.prestige.renovation.control.issuereport;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.dto.PageResponse;
import com.prestige.renovation.dto.issuereport.IssueReportResponse;
import com.prestige.renovation.entity.IssueReport;

/** Control for user story: Reporter views own issue reports and their status (FR-RPT-3). */
@Service
public class ViewMyIssueReportsController {

    @Transactional(readOnly = true)
    public PageResponse<IssueReportResponse> list(Integer reporterId, Pageable pageable) {
        return PageResponse.of(IssueReport.findByReporter(reporterId, pageable), IssueReportResponse::forReporter);
    }
}

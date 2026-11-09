package com.prestige.renovation.control.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.dto.PageResponse;
import com.prestige.renovation.dto.issuereport.IssueReportResponse;
import com.prestige.renovation.entity.IssueReport;
import com.prestige.renovation.entity.IssueReportStatus;
import com.prestige.renovation.entity.IssueReportType;

/** Control for user story: User Admin views all issue reports, filtered by status and type (FR-RPT-4). */
@Service
public class ViewAllIssueReportsController {

    @Transactional(readOnly = true)
    public PageResponse<IssueReportResponse> list(IssueReportStatus status, IssueReportType type, Pageable pageable) {
        return PageResponse.of(IssueReport.search(status, type, pageable), IssueReportResponse::forAdmin);
    }
}

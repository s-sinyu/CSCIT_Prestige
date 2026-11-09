package com.prestige.renovation.dto.issuereport;

import java.time.Instant;

import com.prestige.renovation.entity.IssueReport;
import com.prestige.renovation.entity.IssueReportStatus;
import com.prestige.renovation.entity.IssueReportType;
import com.prestige.renovation.entity.Role;

/** Issue report; reporter fields are filled only for the User Admin's view (FR-RPT-4). */
public record IssueReportResponse(Integer id, IssueReportType type, String title, String description,
                                  IssueReportStatus status, Instant createdAt, Instant rectifiedAt,
                                  Integer targetProjectId, String targetProjectTitle,
                                  Integer targetDesignerId, String targetDesignerName,
                                  Integer reporterId, String reporterName, String reporterEmail, Role reporterRole,
                                  String rectifiedByName) {

    public static IssueReportResponse forReporter(IssueReport r) {
        return build(r, false);
    }

    public static IssueReportResponse forAdmin(IssueReport r) {
        return build(r, true);
    }

    private static IssueReportResponse build(IssueReport r, boolean admin) {
        var p = r.getTargetProject();
        var d = r.getTargetDesigner();
        var rep = r.getReporter();
        return new IssueReportResponse(r.getId(), r.getType(), r.getTitle(), r.getDescription(), r.getStatus(),
                r.getCreatedAt(), r.getRectifiedAt(),
                p == null ? null : p.getId(), p == null ? null : p.getTitle(),
                d == null ? null : d.getId(), d == null ? null : d.getFullName(),
                admin ? rep.getId() : null, admin ? rep.getFullName() : null,
                admin ? rep.getEmail() : null, admin ? rep.getRole() : null,
                admin && r.getRectifiedBy() != null ? r.getRectifiedBy().getFullName() : null);
    }
}

package com.prestige.renovation.dto.designer;

/** Totals across a designer's IDPs (FR-ANA-3). */
public record DashboardResponse(long totalProjects, long totalViews, long totalShortlists,
                                long newEnquiries, long totalEnquiries) {
}

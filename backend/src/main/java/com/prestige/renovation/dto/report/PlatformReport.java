package com.prestige.renovation.dto.report;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.prestige.renovation.entity.ReportPeriodType;
import com.prestige.renovation.entity.Role;

/**
 * Platform Management report for one period (FR-PM-6). Dates are calendar dates in Asia/Singapore;
 * {@code endDate} is inclusive. {@code previousDate}/{@code nextDate} support period navigation.
 */
public record PlatformReport(
        ReportPeriodType periodType,
        LocalDate referenceDate,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate previousDate,
        LocalDate nextDate,
        String timeZone,
        IdpsCreated idpsCreated,
        Engagement engagement,
        Registrations registrations,
        Activity activity) {

    public record CategoryCount(Integer categoryId, String categoryName, boolean active, long count) {
    }

    public record IdpsCreated(long total, List<CategoryCount> byCategory) {
    }

    public record TopProject(Integer projectId, String title, String designerName, String categoryName,
                             long views, long favouritesSaved) {
    }

    public record Engagement(long totalViews, long totalFavouritesSaved, List<TopProject> topViewed) {
    }

    public record Registrations(Map<Role, Long> byRole, long total) {
    }

    public record Activity(long enquiriesReceived, long issueReportsCreatedOpen, long issueReportsCreatedRectified,
                           long issueReportsCurrentlyOpen) {
    }
}

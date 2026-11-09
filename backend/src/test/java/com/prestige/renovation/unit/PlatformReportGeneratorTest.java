package com.prestige.renovation.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.prestige.renovation.control.report.PlatformReportGenerator;
import com.prestige.renovation.control.report.ReportCsvRenderer;
import com.prestige.renovation.entity.InteriorDesignProject;
import com.prestige.renovation.entity.IssueReportStatus;
import com.prestige.renovation.entity.ReportPeriodType;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.support.EntityMockSupport;
import com.prestige.renovation.support.TestData;

/** Report assembly from query results (FR-PM-6) and CSV rendering (FR-PM-8). */
class PlatformReportGeneratorTest extends EntityMockSupport {

    @Test
    void weeklyReportIncludesZeroCategoriesTopProjectsAndCounts() {
        var kitchen = TestData.category(1, "Kitchen", true);
        var bath = TestData.category(2, "Bathroom", false);
        var designer = TestData.user(3, Role.INTERIOR_DESIGNER);
        InteriorDesignProject p = TestData.project(100, designer, kitchen);

        Instant from = Instant.parse("2026-11-01T16:00:00Z"); // Mon 2 Nov 00:00 SGT
        Instant to = Instant.parse("2026-11-08T16:00:00Z");   // Mon 9 Nov 00:00 SGT
        when(categories.findAllByOrderByNameAsc()).thenReturn(List.of(bath, kitchen));
        when(projects.countCreatedByCategory(from, to)).thenReturn(List.<Object[]>of(new Object[] {1, 4L}));
        when(views.countBetween(LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 8))).thenReturn(25L);
        when(favourites.countSavedBetween(from, to)).thenReturn(6L);
        when(views.topViewedBetween(eq(LocalDate.of(2026, 11, 2)), eq(LocalDate.of(2026, 11, 8)), any()))
                .thenReturn(List.<Object[]>of(new Object[] {100, 12L}));
        when(projects.findAllById(List.of(100))).thenReturn(List.of(p));
        when(favourites.countSavedBetweenByProject(eq(from), eq(to), anyCollection()))
                .thenReturn(List.<Object[]>of(new Object[] {100, 2L}));
        when(users.countRegistrationsByRole(from, to)).thenReturn(List.<Object[]>of(
                new Object[] {Role.CUSTOMER, 7L}, new Object[] {Role.INTERIOR_DESIGNER, 2L}));
        when(enquiries.countCreatedBetween(from, to)).thenReturn(3L);
        when(issueReports.countCreatedByStatus(from, to)).thenReturn(List.<Object[]>of(
                new Object[] {IssueReportStatus.OPEN, 2L}, new Object[] {IssueReportStatus.RECTIFIED, 1L}));
        when(issueReports.countByStatus(IssueReportStatus.OPEN)).thenReturn(5L);

        var report = new PlatformReportGenerator(TestData.clockAt("2026-11-08T12:00"))
                .generate(ReportPeriodType.WEEKLY, LocalDate.of(2026, 11, 8));

        assertThat(report.startDate()).isEqualTo(LocalDate.of(2026, 11, 2));
        assertThat(report.idpsCreated().total()).isEqualTo(4);
        assertThat(report.idpsCreated().byCategory()).extracting(c -> c.categoryName() + "=" + c.count())
                .containsExactly("Bathroom=0", "Kitchen=4");
        assertThat(report.engagement().totalViews()).isEqualTo(25);
        assertThat(report.engagement().totalFavouritesSaved()).isEqualTo(6);
        assertThat(report.engagement().topViewed()).singleElement().satisfies(t -> {
            assertThat(t.views()).isEqualTo(12);
            assertThat(t.favouritesSaved()).isEqualTo(2);
            assertThat(t.designerName()).isEqualTo("User 3");
        });
        assertThat(report.registrations().byRole()).containsEntry(Role.CUSTOMER, 7L)
                .containsEntry(Role.PLATFORM_MANAGEMENT, 0L);
        assertThat(report.registrations().total()).isEqualTo(9);
        assertThat(report.activity().enquiriesReceived()).isEqualTo(3);
        assertThat(report.activity().issueReportsCreatedOpen()).isEqualTo(2);
        assertThat(report.activity().issueReportsCreatedRectified()).isEqualTo(1);
        assertThat(report.activity().issueReportsCurrentlyOpen()).isEqualTo(5);

        String csv = ReportCsvRenderer.render(report);
        assertThat(csv).startsWith("﻿").contains(",Kitchen,Yes,4\r\n").contains(",Bathroom,No,0\r\n");
        assertThat(ReportCsvRenderer.fileName(report)).isEqualTo("platform-report-weekly-2026-11-02.csv");
    }
}

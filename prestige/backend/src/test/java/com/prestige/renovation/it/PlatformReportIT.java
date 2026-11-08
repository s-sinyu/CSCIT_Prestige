package com.prestige.renovation.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.prestige.renovation.entity.Role;

/**
 * Report figures for a prepared dataset with fixed timestamps (acceptance criterion 12).
 * Reference week: Monday 2 Nov 2026 to Sunday 8 Nov 2026 (Asia/Singapore).
 */
class PlatformReportIT extends AbstractIntegrationTest {

    private String manager;

    @BeforeEach
    void prepareDataset() {
        int m = createUser("m@test.sg", Role.PLATFORM_MANAGEMENT);
        manager = tokenFor(m);
        int designer = createUser("d@test.sg", Role.INTERIOR_DESIGNER);
        int c1 = createUser("c1@test.sg", Role.CUSTOMER);
        int c2 = createUser("c2@test.sg", Role.CUSTOMER);
        // Registration dates: two customers inside the week (one at 23:30 SGT on Sunday = 15:30 UTC), the rest before
        jdbc.update("update users set created_at = '2026-10-01T00:00:00Z'");
        jdbc.update("update users set created_at = '2026-11-03T02:00:00Z' where id = ?", c1);
        jdbc.update("update users set created_at = '2026-11-08T15:30:00Z' where id = ?", c2);

        int kitchen = createCategory("Kitchen");
        createCategory("Bathroom");
        int p1 = insertProject(designer, kitchen, "P1", "2026-11-02T01:00:00Z");
        int p2 = insertProject(designer, kitchen, "P2", "2026-11-05T01:00:00Z");
        insertProject(designer, kitchen, "Old", "2026-10-20T01:00:00Z");
        // Sunday 8 Nov 16:30 UTC is already Monday 9 Nov in Singapore: outside the week
        insertProject(designer, kitchen, "Next week", "2026-11-08T16:30:00Z");

        insertView(p1, c1, "2026-11-03");
        insertView(p1, c2, "2026-11-03");
        insertView(p1, c1, "2026-11-04");
        insertView(p2, c1, "2026-11-04");
        insertView(p2, c1, "2026-10-30");
        jdbc.update("insert into favourites (customer_id, project_id, saved_at) values (?, ?, '2026-11-04T03:00:00Z')", c1, p1);
        jdbc.update("insert into favourites (customer_id, project_id, saved_at) values (?, ?, '2026-10-04T03:00:00Z')", c2, p1);
        jdbc.update("insert into enquiries (project_id, customer_id, message, status, created_at, updated_at) "
                + "values (?, ?, 'hi', 'NEW', '2026-11-06T03:00:00Z', now())", p1, c1);
        jdbc.update("insert into issue_reports (reporter_id, type, title, description, status, created_at) "
                + "values (?, 'PLATFORM_ISSUE', 't', 'd', 'OPEN', '2026-11-06T03:00:00Z')", c1);
        jdbc.update("insert into issue_reports (reporter_id, type, title, description, status, created_at, rectified_at) "
                + "values (?, 'PLATFORM_ISSUE', 't', 'd', 'RECTIFIED', '2026-11-07T03:00:00Z', now())", c2);
        jdbc.update("insert into issue_reports (reporter_id, type, title, description, status, created_at) "
                + "values (?, 'PLATFORM_ISSUE', 't', 'd', 'OPEN', '2026-09-01T03:00:00Z')", c2);
    }

    private int insertProject(int designer, int category, String title, String createdAt) {
        return jdbc.queryForObject("insert into projects (designer_id, category_id, title, status, deleted, created_at, "
                + "updated_at) values (?, ?, ?, 'COMPLETED', false, ?::timestamptz, now()) returning id",
                Integer.class, designer, category, title, createdAt);
    }

    private void insertView(int project, int customer, String day) {
        jdbc.update("insert into project_views (project_id, customer_id, view_date, viewed_at) values (?, ?, ?::date, now())",
                project, customer, day);
    }

    @Test
    void weeklyReportFigures() throws Exception {
        getJson("/api/management/reports?period=WEEKLY&date=2026-11-08", manager).andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value("2026-11-02"))
                .andExpect(jsonPath("$.endDate").value("2026-11-08"))
                .andExpect(jsonPath("$.previousDate").value("2026-10-26"))
                .andExpect(jsonPath("$.idpsCreated.total").value(2))
                .andExpect(jsonPath("$.idpsCreated.byCategory.length()").value(2))
                .andExpect(jsonPath("$.engagement.totalViews").value(4))
                .andExpect(jsonPath("$.engagement.totalFavouritesSaved").value(1))
                .andExpect(jsonPath("$.engagement.topViewed[0].title").value("P1"))
                .andExpect(jsonPath("$.engagement.topViewed[0].views").value(3))
                .andExpect(jsonPath("$.engagement.topViewed[0].favouritesSaved").value(1))
                .andExpect(jsonPath("$.registrations.byRole.CUSTOMER").value(2))
                .andExpect(jsonPath("$.registrations.total").value(2))
                .andExpect(jsonPath("$.activity.enquiriesReceived").value(1))
                .andExpect(jsonPath("$.activity.issueReportsCreatedOpen").value(1))
                .andExpect(jsonPath("$.activity.issueReportsCreatedRectified").value(1))
                .andExpect(jsonPath("$.activity.issueReportsCurrentlyOpen").value(2));
    }

    @Test
    void dailyAndMonthlyReportFigures() throws Exception {
        getJson("/api/management/reports?period=DAILY&date=2026-11-03", manager)
                .andExpect(jsonPath("$.engagement.totalViews").value(2))
                .andExpect(jsonPath("$.registrations.total").value(1));
        getJson("/api/management/reports?period=MONTHLY&date=2026-11-20", manager)
                .andExpect(jsonPath("$.startDate").value("2026-11-01"))
                .andExpect(jsonPath("$.endDate").value("2026-11-30"))
                .andExpect(jsonPath("$.idpsCreated.total").value(3))
                .andExpect(jsonPath("$.engagement.totalViews").value(4));
        getJson("/api/management/reports?period=MONTHLY&date=2026-10-15", manager)
                .andExpect(jsonPath("$.engagement.totalViews").value(1))
                .andExpect(jsonPath("$.idpsCreated.total").value(1));
    }

    @Test
    void csvExportMatchesTheReport() throws Exception {
        var response = mvc.perform(get("/api/management/reports/export?period=WEEKLY&date=2026-11-08")
                        .header("Authorization", "Bearer " + manager))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("platform-report-weekly-2026-11-02.csv")))
                .andReturn().getResponse();
        String csv = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        assertThat(csv).startsWith("﻿");
        assertThat(csv).contains(",Kitchen,Yes,2\r\n").contains(",Bathroom,Yes,0\r\n");
        assertThat(csv).contains(",4,1\r\n");             // total views, favourites saved
        assertThat(csv).contains(",1,P1,");               // top viewed rank 1
        assertThat(csv).contains(",Customer,2\r\n");
    }

    @Test
    void onlyPlatformManagementCanSeeReports() throws Exception {
        String admin = tokenFor(createUser("a@test.sg", Role.USER_ADMIN));
        getJson("/api/management/reports", admin).andExpect(status().isForbidden());
    }
}

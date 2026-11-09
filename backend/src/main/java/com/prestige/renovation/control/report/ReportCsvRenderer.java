package com.prestige.renovation.control.report;

import java.util.Locale;
import java.util.Map;

import com.prestige.renovation.common.util.CsvWriter;
import com.prestige.renovation.dto.report.PlatformReport;
import com.prestige.renovation.entity.Role;

/** Renders a {@link PlatformReport} as CSV with one header row per section (FR-PM-8). */
public final class ReportCsvRenderer {

    private ReportCsvRenderer() {
    }

    public static String render(PlatformReport r) {
        CsvWriter csv = new CsvWriter();
        csv.row("Report", "Period type", "Start date", "End date", "Time zone")
           .row("Platform report", r.periodType(), r.startDate(), r.endDate(), r.timeZone())
           .blankLine();

        csv.row("IDPs created by category", "Category", "Active", "Count");
        for (PlatformReport.CategoryCount c : r.idpsCreated().byCategory()) {
            csv.row("", c.categoryName(), c.active() ? "Yes" : "No", c.count());
        }
        csv.row("", "Total", "", r.idpsCreated().total()).blankLine();

        csv.row("Views and shortlists", "Total views", "Favourites saved")
           .row("", r.engagement().totalViews(), r.engagement().totalFavouritesSaved())
           .blankLine();

        csv.row("Top 10 most viewed IDPs", "Rank", "Title", "Designer / company", "Category", "Views", "Favourites saved");
        int rank = 1;
        for (PlatformReport.TopProject p : r.engagement().topViewed()) {
            csv.row("", rank++, p.title(), p.designerName(), p.categoryName(), p.views(), p.favouritesSaved());
        }
        csv.blankLine();

        csv.row("New registrations", "Role", "Count");
        for (Map.Entry<Role, Long> e : r.registrations().byRole().entrySet()) {
            csv.row("", label(e.getKey()), e.getValue());
        }
        csv.row("", "Total", r.registrations().total()).blankLine();

        csv.row("Enquiries and issue reports", "Enquiries received", "Reports created (now OPEN)",
                "Reports created (now RECTIFIED)", "Reports currently OPEN (all time)")
           .row("", r.activity().enquiriesReceived(), r.activity().issueReportsCreatedOpen(),
                r.activity().issueReportsCreatedRectified(), r.activity().issueReportsCurrentlyOpen());
        return csv.build();
    }

    public static String fileName(PlatformReport r) {
        return "platform-report-" + r.periodType().name().toLowerCase(Locale.ROOT) + "-" + r.startDate() + ".csv";
    }

    private static String label(Role role) {
        return switch (role) {
            case CUSTOMER -> "Customer";
            case INTERIOR_DESIGNER -> "Interior Designer";
            case PLATFORM_MANAGEMENT -> "Platform Management";
            case USER_ADMIN -> "User Admin";
        };
    }
}

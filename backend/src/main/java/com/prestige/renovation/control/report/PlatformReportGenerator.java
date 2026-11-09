package com.prestige.renovation.control.report;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.prestige.renovation.control.common.ProjectCardAssembler;
import com.prestige.renovation.dto.report.PlatformReport;
import com.prestige.renovation.entity.Category;
import com.prestige.renovation.entity.DesignerCompany;
import com.prestige.renovation.entity.Enquiry;
import com.prestige.renovation.entity.Favourite;
import com.prestige.renovation.entity.InteriorDesignProject;
import com.prestige.renovation.entity.IssueReport;
import com.prestige.renovation.entity.IssueReportStatus;
import com.prestige.renovation.entity.ProjectView;
import com.prestige.renovation.entity.ReportPeriodType;
import com.prestige.renovation.entity.Role;

/**
 * Computes the Platform Management report (FR-PM-6) with queries over the existing tables; nothing is stored.
 * Shared by the "view report" and "export report" control classes so both always show the same figures.
 */
@Component
public class PlatformReportGenerator {

    static final int TOP_N = 10;

    private final Clock clock;

    public PlatformReportGenerator(Clock clock) {
        this.clock = clock;
    }

    public PlatformReport generate(ReportPeriodType type, LocalDate referenceDate) {
        LocalDate reference = referenceDate == null ? LocalDate.now(clock) : referenceDate;
        ReportPeriod period = ReportPeriod.of(type, reference, clock.getZone());
        Instant from = period.startInstant();
        Instant to = period.endExclusiveInstant();

        // 1. IDPs created, per category (all categories, zero included)
        Map<Integer, Long> created = InteriorDesignProject.countCreatedByCategory(from, to);
        List<PlatformReport.CategoryCount> byCategory = Category.findAllSorted().stream()
                .map(c -> new PlatformReport.CategoryCount(c.getId(), c.getName(), c.isActive(),
                        created.getOrDefault(c.getId(), 0L)))
                .toList();
        long totalCreated = created.values().stream().mapToLong(Long::longValue).sum();

        // 2. Views and favourites saved in the period, top 10 most viewed
        long views = ProjectView.countBetween(period.start(), period.end());
        long favouritesSaved = Favourite.countSavedBetween(from, to);
        List<long[]> top = ProjectView.topViewed(period.start(), period.end(), TOP_N);
        List<Integer> topIds = top.stream().map(r -> (int) r[0]).toList();
        Map<Integer, InteriorDesignProject> projects = InteriorDesignProject.findAllByIds(topIds).stream()
                .collect(Collectors.toMap(InteriorDesignProject::getId, Function.identity()));
        Map<Integer, Long> favPerProject = Favourite.countSavedBetweenByProject(from, to, topIds);
        Map<Integer, DesignerCompany> companies = DesignerCompany.findByDesigners(
                projects.values().stream().map(p -> p.getDesigner().getId()).collect(Collectors.toSet()));
        List<PlatformReport.TopProject> topViewed = new ArrayList<>();
        for (long[] row : top) {
            InteriorDesignProject p = projects.get((int) row[0]);
            if (p == null) {
                continue;
            }
            topViewed.add(new PlatformReport.TopProject(p.getId(), p.getTitle(),
                    ProjectCardAssembler.displayName(p.getDesigner(), companies.get(p.getDesigner().getId())),
                    p.getCategory().getName(), row[1], favPerProject.getOrDefault(p.getId(), 0L)));
        }

        // 3. New registrations per role
        Map<Role, Long> registrations = new EnumMap<>(Role.class);
        for (Role r : Role.values()) {
            registrations.put(r, 0L);
        }
        registrations.putAll(com.prestige.renovation.entity.User.countRegistrationsByRole(from, to));
        long totalRegistrations = registrations.values().stream().mapToLong(Long::longValue).sum();

        // 4. Enquiries and issue reports
        long enquiries = Enquiry.countCreatedBetween(from, to);
        Map<IssueReportStatus, Long> reports = IssueReport.countCreatedByStatus(from, to);
        long currentlyOpen = IssueReport.countWithStatus(IssueReportStatus.OPEN);

        return new PlatformReport(type, reference, period.start(), period.end(),
                period.previousReference(), period.nextReference(), clock.getZone().getId(),
                new PlatformReport.IdpsCreated(totalCreated, byCategory),
                new PlatformReport.Engagement(views, favouritesSaved, topViewed),
                new PlatformReport.Registrations(registrations, totalRegistrations),
                new PlatformReport.Activity(enquiries, reports.get(IssueReportStatus.OPEN),
                        reports.get(IssueReportStatus.RECTIFIED), currentlyOpen));
    }
}

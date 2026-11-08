package com.prestige.renovation.control.report;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

import com.prestige.renovation.entity.ReportPeriodType;

/**
 * The period containing a reference date (FR-PM-5): the day; the Monday-to-Sunday week; or the calendar month.
 * {@code start} and {@code end} are inclusive calendar dates in the business time zone. Timestamps are compared
 * against the half-open instant range [startInstant, endExclusiveInstant).
 */
public record ReportPeriod(ReportPeriodType type, LocalDate reference, LocalDate start, LocalDate end, ZoneId zone) {

    public static ReportPeriod of(ReportPeriodType type, LocalDate reference, ZoneId zone) {
        return switch (type) {
            case DAILY -> new ReportPeriod(type, reference, reference, reference, zone);
            case WEEKLY -> {
                LocalDate monday = reference.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                yield new ReportPeriod(type, reference, monday, monday.plusDays(6), zone);
            }
            case MONTHLY -> new ReportPeriod(type, reference, reference.withDayOfMonth(1),
                    reference.with(TemporalAdjusters.lastDayOfMonth()), zone);
        };
    }

    public Instant startInstant() {
        return start.atStartOfDay(zone).toInstant();
    }

    public Instant endExclusiveInstant() {
        return end.plusDays(1).atStartOfDay(zone).toInstant();
    }

    /** A date inside the previous period (used for "previous" navigation). */
    public LocalDate previousReference() {
        return switch (type) {
            case DAILY -> start.minusDays(1);
            case WEEKLY -> start.minusWeeks(1);
            case MONTHLY -> start.minusMonths(1);
        };
    }

    /** A date inside the next period. */
    public LocalDate nextReference() {
        return switch (type) {
            case DAILY -> start.plusDays(1);
            case WEEKLY -> start.plusWeeks(1);
            case MONTHLY -> start.plusMonths(1);
        };
    }
}

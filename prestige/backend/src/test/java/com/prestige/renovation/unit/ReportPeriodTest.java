package com.prestige.renovation.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.prestige.renovation.control.report.ReportPeriod;
import com.prestige.renovation.entity.ReportPeriodType;
import com.prestige.renovation.support.TestData;

/** Report period calculation: day, Monday-start week and calendar month in Asia/Singapore (FR-PM-5). */
class ReportPeriodTest {

    @Test
    void dailyPeriodIsTheDayItself() {
        ReportPeriod p = ReportPeriod.of(ReportPeriodType.DAILY, LocalDate.of(2026, 11, 8), TestData.SGT);
        assertThat(p.start()).isEqualTo(LocalDate.of(2026, 11, 8));
        assertThat(p.end()).isEqualTo(LocalDate.of(2026, 11, 8));
        // Midnight in Singapore is 16:00 UTC the previous day
        assertThat(p.startInstant()).isEqualTo(Instant.parse("2026-11-07T16:00:00Z"));
        assertThat(p.endExclusiveInstant()).isEqualTo(Instant.parse("2026-11-08T16:00:00Z"));
        assertThat(p.previousReference()).isEqualTo(LocalDate.of(2026, 11, 7));
        assertThat(p.nextReference()).isEqualTo(LocalDate.of(2026, 11, 9));
    }

    @Test
    void weekStartsOnMondayWhenReferenceIsSunday() {
        // 8 Nov 2026 is a Sunday
        ReportPeriod p = ReportPeriod.of(ReportPeriodType.WEEKLY, LocalDate.of(2026, 11, 8), TestData.SGT);
        assertThat(p.start()).isEqualTo(LocalDate.of(2026, 11, 2));
        assertThat(p.end()).isEqualTo(LocalDate.of(2026, 11, 8));
    }

    @Test
    void weekStartsOnSameDayWhenReferenceIsMonday() {
        ReportPeriod p = ReportPeriod.of(ReportPeriodType.WEEKLY, LocalDate.of(2026, 11, 9), TestData.SGT);
        assertThat(p.start()).isEqualTo(LocalDate.of(2026, 11, 9));
        assertThat(p.end()).isEqualTo(LocalDate.of(2026, 11, 15));
        assertThat(p.previousReference()).isEqualTo(LocalDate.of(2026, 11, 2));
    }

    @Test
    void weekCanSpanMonthAndYearBoundaries() {
        ReportPeriod p = ReportPeriod.of(ReportPeriodType.WEEKLY, LocalDate.of(2027, 1, 1), TestData.SGT);
        assertThat(p.start()).isEqualTo(LocalDate.of(2026, 12, 28));
        assertThat(p.end()).isEqualTo(LocalDate.of(2027, 1, 3));
    }

    @Test
    void monthCoversWholeCalendarMonthIncludingLeapDay() {
        ReportPeriod p = ReportPeriod.of(ReportPeriodType.MONTHLY, LocalDate.of(2028, 2, 15), TestData.SGT);
        assertThat(p.start()).isEqualTo(LocalDate.of(2028, 2, 1));
        assertThat(p.end()).isEqualTo(LocalDate.of(2028, 2, 29));
        assertThat(p.previousReference()).isEqualTo(LocalDate.of(2028, 1, 1));
        assertThat(p.nextReference()).isEqualTo(LocalDate.of(2028, 3, 1));
    }
}

package com.prestige.renovation.unit;

import static com.prestige.renovation.support.ApiAssertions.assertApiError;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import com.prestige.renovation.control.availability.SetAvailabilityController;
import com.prestige.renovation.control.enquiry.UpdateEnquiryStatusController;
import com.prestige.renovation.control.issuereport.SubmitIssueReportController;
import com.prestige.renovation.control.rating.CreateRatingController;
import com.prestige.renovation.control.rating.UpdateRatingController;
import com.prestige.renovation.dto.issuereport.IssueReportRequest;
import com.prestige.renovation.dto.rating.RatingRequest;
import com.prestige.renovation.dto.rating.RatingUpdateRequest;
import com.prestige.renovation.entity.Availability;
import com.prestige.renovation.entity.AvailabilityStatus;
import com.prestige.renovation.entity.Enquiry;
import com.prestige.renovation.entity.EnquiryStatus;
import com.prestige.renovation.entity.InteriorDesignProject;
import com.prestige.renovation.entity.IssueReport;
import com.prestige.renovation.entity.IssueReportType;
import com.prestige.renovation.entity.Rating;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;
import com.prestige.renovation.support.EntityMockSupport;
import com.prestige.renovation.support.TestData;

/** Availability (FR-AV), ratings (FR-RT), enquiry status (FR-ENQ-3) and issue reports (FR-RPT-1). */
class CustomerAndDesignerControllersTest extends EntityMockSupport {

    private final User designer = TestData.user(1, Role.INTERIOR_DESIGNER);
    private final User customer = TestData.user(5, Role.CUSTOMER);
    private final InteriorDesignProject project = TestData.project(100, designer, TestData.category(10, "K", true));

    // ---------------------------------------------------------------- availability

    @Test
    void pastDatesCannotBeSet() {
        when(users.findById(1)).thenReturn(Optional.of(designer));
        var controller = new SetAvailabilityController(TestData.clockAt("2026-11-08T00:05"));
        assertApiError(() -> controller.set(1, LocalDate.of(2026, 11, 7), AvailabilityStatus.UNAVAILABLE),
                HttpStatus.BAD_REQUEST);
        verify(availability, never()).save(any());
    }

    @Test
    void todayCanBeSetAndExistingRecordIsUpdated() {
        when(users.findById(1)).thenReturn(Optional.of(designer));
        Availability existing = new Availability(designer, LocalDate.of(2026, 11, 8), AvailabilityStatus.UNAVAILABLE);
        when(availability.findByDesignerIdAndCalendarDate(1, LocalDate.of(2026, 11, 8))).thenReturn(Optional.of(existing));
        new SetAvailabilityController(TestData.clockAt("2026-11-08T00:05"))
                .set(1, LocalDate.of(2026, 11, 8), AvailabilityStatus.AVAILABLE);
        assertThat(existing.getStatus()).isEqualTo(AvailabilityStatus.AVAILABLE);
        verify(availability).save(existing);
    }

    @Test
    void datesWithoutARecordAreAvailable() {
        when(availability.findByDesignerIdAndCalendarDateBetweenOrderByCalendarDateAsc(1,
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30)))
                .thenReturn(List.of(new Availability(designer, LocalDate.of(2026, 11, 20), AvailabilityStatus.UNAVAILABLE)));
        var month = Availability.monthFor(1, YearMonth.of(2026, 11));
        assertThat(month).hasSize(30);
        assertThat(month.get(LocalDate.of(2026, 11, 20))).isEqualTo(AvailabilityStatus.UNAVAILABLE);
        assertThat(month.values().stream().filter(s -> s == AvailabilityStatus.AVAILABLE).count()).isEqualTo(29);
    }

    // ---------------------------------------------------------------- ratings

    private void stubRatingParties() {
        when(users.findById(5)).thenReturn(Optional.of(customer));
        when(users.findById(1)).thenReturn(Optional.of(designer));
        when(ratings.save(any(Rating.class))).thenAnswer(inv -> TestData.withId(inv.getArgument(0), 70));
    }

    @Test
    void secondGeneralRatingIsAConflict() {
        stubRatingParties();
        when(ratings.existsByCustomerIdAndDesignerIdAndProjectIsNull(5, 1)).thenReturn(true);
        assertApiError(() -> new CreateRatingController().create(5, new RatingRequest(1, null, 4, null)),
                HttpStatus.CONFLICT);
    }

    @Test
    @SuppressWarnings("unchecked")
    void secondRatingForSameProjectIsAConflict() {
        stubRatingParties();
        when(projects.findOne(any(Specification.class))).thenReturn(Optional.of(project));
        when(ratings.existsByCustomerIdAndProjectId(5, 100)).thenReturn(true);
        assertApiError(() -> new CreateRatingController().create(5, new RatingRequest(1, 100, 4, null)),
                HttpStatus.CONFLICT);
    }

    @Test
    @SuppressWarnings("unchecked")
    void ratedProjectMustBelongToTheRatedDesigner() {
        stubRatingParties();
        InteriorDesignProject othersProject = TestData.project(200, TestData.user(2, Role.INTERIOR_DESIGNER),
                TestData.category(10, "K", true));
        when(projects.findOne(any(Specification.class))).thenReturn(Optional.of(othersProject));
        assertApiError(() -> new CreateRatingController().create(5, new RatingRequest(1, 200, 4, null)),
                HttpStatus.BAD_REQUEST);
    }

    @Test
    void generalAndProjectRatingsCanCoexist() {
        stubRatingParties();
        var general = new CreateRatingController().create(5, new RatingRequest(1, null, 5, " Great "));
        assertThat(general.comment()).isEqualTo("Great");
        assertThat(general.mine()).isTrue();
    }

    @Test
    void designersCannotRate() {
        when(users.findById(1)).thenReturn(Optional.of(designer));
        assertApiError(() -> new CreateRatingController().create(1, new RatingRequest(1, null, 5, null)),
                HttpStatus.FORBIDDEN);
    }

    @Test
    void customersCannotEditOthersRatings() {
        Rating r = TestData.withId(new Rating(TestData.user(6, Role.CUSTOMER), designer, null, 3, null), 70);
        when(ratings.findById(70)).thenReturn(Optional.of(r));
        assertApiError(() -> new UpdateRatingController().update(5, 70, new RatingUpdateRequest(1, null)),
                HttpStatus.NOT_FOUND);
    }

    // ---------------------------------------------------------------- enquiries

    @Test
    void enquiryCannotGoBackToNewOrToAnotherDesigner() {
        Enquiry e = TestData.withId(new Enquiry(project, customer, "Hi"), 30);
        e.changeStatus(EnquiryStatus.REPLIED, java.time.Instant.now());
        when(enquiries.findById(30)).thenReturn(Optional.of(e));
        var controller = new UpdateEnquiryStatusController(TestData.clockAt("2026-11-08T10:00"));
        assertApiError(() -> controller.updateStatus(1, 30, EnquiryStatus.NEW), HttpStatus.BAD_REQUEST);
        assertApiError(() -> controller.updateStatus(1, 30, EnquiryStatus.READ), HttpStatus.BAD_REQUEST);
        assertApiError(() -> controller.updateStatus(2, 30, EnquiryStatus.REPLIED), HttpStatus.NOT_FOUND);
    }

    // ---------------------------------------------------------------- issue reports

    @Test
    void idpOrDesignerReportNeedsATarget() {
        when(users.findById(5)).thenReturn(Optional.of(customer));
        var request = new IssueReportRequest(IssueReportType.IDP_OR_DESIGNER, null, null, "Fake", "Photos copied");
        assertApiError(() -> new SubmitIssueReportController().submit(5, request), HttpStatus.BAD_REQUEST);
    }

    @Test
    void platformIssueIgnoresTargets() {
        when(users.findById(5)).thenReturn(Optional.of(customer));
        when(issueReports.save(any(IssueReport.class))).thenAnswer(inv -> TestData.withId(inv.getArgument(0), 8));
        var response = new SubmitIssueReportController().submit(5,
                new IssueReportRequest(IssueReportType.PLATFORM_ISSUE, 100, 1, "Slow", "Search is slow"));
        assertThat(response.targetProjectId()).isNull();
        assertThat(response.targetDesignerId()).isNull();
        assertThat(response.status().name()).isEqualTo("OPEN");
    }

    @Test
    void managementCannotSubmitReports() {
        when(users.findById(7)).thenReturn(Optional.of(TestData.user(7, Role.PLATFORM_MANAGEMENT)));
        var request = new IssueReportRequest(IssueReportType.PLATFORM_ISSUE, null, null, "x", "y");
        assertApiError(() -> new SubmitIssueReportController().submit(7, request), HttpStatus.FORBIDDEN);
    }
}

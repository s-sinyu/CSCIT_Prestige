package com.prestige.renovation.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.prestige.renovation.entity.Enquiry;
import com.prestige.renovation.entity.EnquiryStatus;
import com.prestige.renovation.entity.IssueReport;
import com.prestige.renovation.entity.IssueReportStatus;
import com.prestige.renovation.entity.IssueReportType;
import com.prestige.renovation.entity.Rating;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;
import com.prestige.renovation.support.TestData;

/** Business rules held by the entity classes themselves. */
class EntityRulesTest {

    private final Instant now = Instant.parse("2026-11-08T02:00:00Z");

    private Enquiry newEnquiry() {
        User designer = TestData.user(1, Role.INTERIOR_DESIGNER);
        var project = TestData.project(10, designer, TestData.category(1, "Kitchen", true));
        return new Enquiry(project, TestData.user(2, Role.CUSTOMER), "Hello");
    }

    @Test
    void enquiryMovesNewToReadToReplied() {
        Enquiry e = newEnquiry();
        assertThat(e.getStatus()).isEqualTo(EnquiryStatus.NEW);
        assertThat(e.changeStatus(EnquiryStatus.READ, now)).isTrue();
        assertThat(e.getReadAt()).isEqualTo(now);
        assertThat(e.changeStatus(EnquiryStatus.REPLIED, now)).isTrue();
        assertThat(e.getStatus()).isEqualTo(EnquiryStatus.REPLIED);
    }

    @Test
    void enquiryCanBeRepliedDirectlyAndNeverGoesBack() {
        Enquiry e = newEnquiry();
        assertThat(e.changeStatus(EnquiryStatus.REPLIED, now)).isTrue();
        assertThat(e.changeStatus(EnquiryStatus.READ, now)).isFalse();
        assertThat(e.changeStatus(EnquiryStatus.NEW, now)).isFalse();
        assertThat(e.getStatus()).isEqualTo(EnquiryStatus.REPLIED);
    }

    @Test
    void enquiryBelongsOnlyToTheProjectOwner() {
        Enquiry e = newEnquiry();
        assertThat(e.belongsToDesigner(1)).isTrue();
        assertThat(e.belongsToDesigner(99)).isFalse();
    }

    @Test
    void issueReportRectifiesOnlyOnce() {
        User admin = TestData.user(9, Role.USER_ADMIN);
        IssueReport r = new IssueReport(TestData.user(2, Role.CUSTOMER), IssueReportType.PLATFORM_ISSUE, null, null,
                "Bug", "Details");
        assertThat(r.rectify(admin, now)).isTrue();
        assertThat(r.getStatus()).isEqualTo(IssueReportStatus.RECTIFIED);
        assertThat(r.getRectifiedBy()).isSameAs(admin);
        assertThat(r.rectify(admin, now.plusSeconds(60))).isFalse();
        assertThat(r.getRectifiedAt()).isEqualTo(now);
    }

    @Test
    void averageRatingIsRoundedToOneDecimalHalfUp() {
        assertThat(Rating.roundAverage(4.25, 4)).isEqualByComparingTo(new BigDecimal("4.3"));
        assertThat(Rating.roundAverage(3.0, 1)).isEqualByComparingTo(new BigDecimal("3.0"));
        assertThat(Rating.roundAverage(3.333333, 3)).isEqualByComparingTo(new BigDecimal("3.3"));
        assertThat(Rating.roundAverage(null, 0)).isNull();
    }

    @Test
    void suspendedOrDeletedUsersAreNotActive() {
        User u = TestData.user(5, Role.CUSTOMER);
        assertThat(u.isActive()).isTrue();
        TestData.suspended(u);
        assertThat(u.isActive()).isFalse();
        User d = TestData.user(6, Role.CUSTOMER);
        d.softDelete(now);
        assertThat(d.isActive()).isFalse();
    }

    @Test
    void projectVisibilityDependsOnDeletionAndDesignerState() {
        User designer = TestData.user(1, Role.INTERIOR_DESIGNER);
        var project = TestData.project(10, designer, TestData.category(1, "Kitchen", true));
        assertThat(project.isVisible()).isTrue();
        TestData.suspended(designer);
        assertThat(project.isVisible()).isFalse();
        var other = TestData.project(11, TestData.user(2, Role.INTERIOR_DESIGNER), TestData.category(1, "K", true));
        other.softDelete(now);
        assertThat(other.isVisible()).isFalse();
    }
}

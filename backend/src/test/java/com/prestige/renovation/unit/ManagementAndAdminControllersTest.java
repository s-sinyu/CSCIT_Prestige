package com.prestige.renovation.unit;

import static com.prestige.renovation.support.ApiAssertions.assertApiError;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.prestige.renovation.control.admin.ChangeAccountStatusController;
import com.prestige.renovation.control.admin.DeleteUserAccountController;
import com.prestige.renovation.control.admin.RectifyIssueReportController;
import com.prestige.renovation.control.category.ChangeCategoryActiveController;
import com.prestige.renovation.control.category.CreateCategoryController;
import com.prestige.renovation.control.category.DeleteCategoryController;
import com.prestige.renovation.dto.category.CategoryRequest;
import com.prestige.renovation.entity.AccountStatus;
import com.prestige.renovation.entity.Category;
import com.prestige.renovation.entity.IssueReport;
import com.prestige.renovation.entity.IssueReportStatus;
import com.prestige.renovation.entity.IssueReportType;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;
import com.prestige.renovation.support.EntityMockSupport;
import com.prestige.renovation.support.TestData;

/** Category rules (FR-PM-1 to 3) and admin rules (FR-ADM-5 to 7, FR-RPT-5). */
class ManagementAndAdminControllersTest extends EntityMockSupport {

    @Test
    void categoryNamesAreUniqueIgnoringCase() {
        when(categories.existsByNameIgnoreCase("Kitchen")).thenReturn(true);
        assertApiError(() -> new CreateCategoryController().create(new CategoryRequest(" Kitchen ", null, true)),
                HttpStatus.CONFLICT);
    }

    @Test
    void usedCategoryCannotBeDeleted() {
        Category c = TestData.category(10, "Kitchen", true);
        when(categories.findById(10)).thenReturn(Optional.of(c));
        when(projects.countByCategoryId(10)).thenReturn(2L);
        assertApiError(() -> new DeleteCategoryController().delete(10), HttpStatus.CONFLICT);
        verify(categories, never()).delete(any(Category.class));
    }

    @Test
    void unusedCategoryIsHardDeleted() {
        Category c = TestData.category(10, "Kitchen", true);
        when(categories.findById(10)).thenReturn(Optional.of(c));
        when(projects.countByCategoryId(10)).thenReturn(0L);
        new DeleteCategoryController().delete(10);
        verify(categories).delete(c);
    }

    @Test
    void categoryCanBeDeactivated() {
        Category c = TestData.category(10, "Kitchen", true);
        when(categories.findById(10)).thenReturn(Optional.of(c));
        when(categories.save(c)).thenReturn(c);
        assertThat(new ChangeCategoryActiveController().setActive(10, false).active()).isFalse();
    }

    @Test
    void adminCannotSuspendOrDeleteSelf() {
        assertApiError(() -> new ChangeAccountStatusController().changeStatus(9, 9, AccountStatus.SUSPENDED),
                HttpStatus.BAD_REQUEST);
        assertApiError(() -> new DeleteUserAccountController(TestData.clockAt("2026-11-08T10:00")).delete(9, 9),
                HttpStatus.BAD_REQUEST);
    }

    @Test
    void suspendAndReactivate() {
        User designer = TestData.user(3, Role.INTERIOR_DESIGNER);
        when(users.findById(3)).thenReturn(Optional.of(designer));
        when(users.save(designer)).thenReturn(designer);
        var controller = new ChangeAccountStatusController();
        assertThat(controller.changeStatus(9, 3, AccountStatus.SUSPENDED).status()).isEqualTo(AccountStatus.SUSPENDED);
        assertThat(designer.isActive()).isFalse();
        assertThat(controller.changeStatus(9, 3, AccountStatus.ACTIVE).status()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(designer.isActive()).isTrue();
    }

    @Test
    void deleteIsSoftAndKeepsTheRow() {
        User customer = TestData.user(4, Role.CUSTOMER);
        when(users.findById(4)).thenReturn(Optional.of(customer));
        new DeleteUserAccountController(TestData.clockAt("2026-11-08T10:00")).delete(9, 4);
        assertThat(customer.isDeleted()).isTrue();
        verify(users).save(customer);
        verify(users, never()).delete(any(User.class));
    }

    @Test
    void rectifyingTwiceIsAConflict() {
        User admin = TestData.user(9, Role.USER_ADMIN);
        when(users.findById(9)).thenReturn(Optional.of(admin));
        IssueReport report = TestData.withId(new IssueReport(TestData.user(4, Role.CUSTOMER),
                IssueReportType.PLATFORM_ISSUE, null, null, "Bug", "Details"), 50);
        when(issueReports.findWithDetailsById(50)).thenReturn(Optional.of(report));
        when(issueReports.save(report)).thenReturn(report);
        var controller = new RectifyIssueReportController(TestData.clockAt("2026-11-08T10:00"));
        assertThat(controller.rectify(9, 50).status()).isEqualTo(IssueReportStatus.RECTIFIED);
        assertApiError(() -> controller.rectify(9, 50), HttpStatus.CONFLICT);
    }
}

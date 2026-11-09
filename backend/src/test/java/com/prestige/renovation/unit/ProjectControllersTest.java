package com.prestige.renovation.unit;

import static com.prestige.renovation.support.ApiAssertions.assertApiError;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.prestige.renovation.control.project.CreateProjectController;
import com.prestige.renovation.control.project.DeleteProjectController;
import com.prestige.renovation.control.project.UpdateProjectController;
import com.prestige.renovation.control.project.ViewMyProjectController;
import com.prestige.renovation.dto.project.ProjectRequest;
import com.prestige.renovation.entity.Category;
import com.prestige.renovation.entity.InteriorDesignProject;
import com.prestige.renovation.entity.ProjectStatus;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;
import com.prestige.renovation.support.EntityMockSupport;
import com.prestige.renovation.support.TestData;

/** IDP management: ownership checks, active-category rule and soft delete (FR-IDP-1 to 4, FR-PM-3). */
class ProjectControllersTest extends EntityMockSupport {

    private final User designerA = TestData.user(1, Role.INTERIOR_DESIGNER);
    private final Category kitchen = TestData.category(10, "Kitchen", true);
    private final Category retired = TestData.category(11, "Retired", false);

    @BeforeEach
    void setUp() {
        when(users.findById(1)).thenReturn(Optional.of(designerA));
        when(categories.findById(10)).thenReturn(Optional.of(kitchen));
        when(categories.findById(11)).thenReturn(Optional.of(retired));
        when(projects.save(any(InteriorDesignProject.class))).thenAnswer(inv -> {
            InteriorDesignProject p = inv.getArgument(0);
            return p.getId() == null ? TestData.withId(p, 100) : p;
        });
        when(images.findByProjectIdOrderBySortOrderAscIdAsc(anyInt())).thenReturn(List.of());
    }

    private ProjectRequest request(int categoryId) {
        return new ProjectRequest("Loft", "Nice", categoryId, ProjectStatus.CURRENT, new BigDecimal("1000.00"),
                null, null, "Bishan");
    }

    @Test
    void createsProjectInActiveCategory() {
        var detail = new CreateProjectController().create(1, request(10));
        assertThat(detail.id()).isEqualTo(100);
        assertThat(detail.categoryName()).isEqualTo("Kitchen");
        assertThat(detail.views()).isZero();
    }

    @Test
    void cannotCreateProjectInInactiveCategory() {
        assertApiError(() -> new CreateProjectController().create(1, request(11)), HttpStatus.BAD_REQUEST);
        verify(projects, never()).save(any());
    }

    @Test
    void customerCannotCreateProjects() {
        when(users.findById(2)).thenReturn(Optional.of(TestData.user(2, Role.CUSTOMER)));
        assertApiError(() -> new CreateProjectController().create(2, request(10)), HttpStatus.FORBIDDEN);
    }

    @Test
    void otherDesignersProjectIsNotFoundForViewEditAndDelete() {
        // Ownership is part of the query: designer 2 asking for designer 1's project finds nothing.
        when(projects.findByIdAndDesignerIdAndDeletedFalse(eq(100), eq(2))).thenReturn(Optional.empty());
        assertApiError(() -> new ViewMyProjectController().view(2, 100), HttpStatus.NOT_FOUND);
        assertApiError(() -> new UpdateProjectController().update(2, 100, request(10)), HttpStatus.NOT_FOUND);
        assertApiError(() -> new DeleteProjectController(TestData.clockAt("2026-11-08T10:00")).delete(2, 100),
                HttpStatus.NOT_FOUND);
    }

    @Test
    void existingProjectMayKeepItsNowInactiveCategory() {
        InteriorDesignProject p = TestData.project(100, designerA, retired);
        when(projects.findByIdAndDesignerIdAndDeletedFalse(100, 1)).thenReturn(Optional.of(p));
        var detail = new UpdateProjectController().update(1, 100, request(11));
        assertThat(detail.categoryActive()).isFalse();
    }

    @Test
    void editingCannotMoveProjectIntoAnInactiveCategory() {
        InteriorDesignProject p = TestData.project(100, designerA, kitchen);
        when(projects.findByIdAndDesignerIdAndDeletedFalse(100, 1)).thenReturn(Optional.of(p));
        assertApiError(() -> new UpdateProjectController().update(1, 100, request(11)), HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteIsSoft() {
        InteriorDesignProject p = TestData.project(100, designerA, kitchen);
        when(projects.findByIdAndDesignerIdAndDeletedFalse(100, 1)).thenReturn(Optional.of(p));
        new DeleteProjectController(TestData.clockAt("2026-11-08T10:00")).delete(1, 100);
        assertThat(p.isDeleted()).isTrue();
        verify(projects).save(p);
        verify(projects, never()).delete(any());
    }
}

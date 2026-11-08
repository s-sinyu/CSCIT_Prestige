package com.prestige.renovation.unit;

import static com.prestige.renovation.support.ApiAssertions.assertApiError;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.browse.ViewProjectDetailController;
import com.prestige.renovation.control.favourite.AddFavouriteController;
import com.prestige.renovation.entity.Favourite;
import com.prestige.renovation.entity.FavouriteId;
import com.prestige.renovation.entity.InteriorDesignProject;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;
import com.prestige.renovation.entity.repository.RatingRepository;
import com.prestige.renovation.support.EntityMockSupport;
import com.prestige.renovation.support.TestData;

/** View counting (FR-ANA-1), favourited flag, idempotent favourites and shortlist counting (FR-CUS-4, FR-ANA-2). */
class BrowseAndFavouriteControllersTest extends EntityMockSupport {

    private final User designer = TestData.user(1, Role.INTERIOR_DESIGNER);
    private final InteriorDesignProject project = TestData.project(100, designer, TestData.category(10, "Kitchen", true));
    private final ViewProjectDetailController detail = new ViewProjectDetailController(TestData.clockAt("2026-11-08T23:30"));

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        when(projects.findOne(any(Specification.class))).thenReturn(Optional.of(project));
        when(images.findByProjectIdOrderBySortOrderAscIdAsc(anyInt())).thenReturn(List.of());
        when(companies.findByDesignerId(1)).thenReturn(Optional.empty());
        when(profiles.findByDesignerId(1)).thenReturn(Optional.empty());
        when(ratings.statsForDesigner(1)).thenReturn(new RatingRepository.RatingStats() {
            public Double getAverageScore() {
                return null;
            }

            public Long getRatingCount() {
                return 0L;
            }
        });
    }

    @Test
    void customerViewIsRecordedForTheSingaporeCalendarDay() {
        // 23:30 in Singapore on 8 Nov is still 8 Nov locally (15:30 UTC)
        var result = detail.view(100, new AuthUser(5, "c@test.sg", Role.CUSTOMER));
        verify(views).insertIfAbsent(100, 5, LocalDate.of(2026, 11, 8), Instant.parse("2026-11-08T15:30:00Z"));
        assertThat(result.favourited()).isFalse();
    }

    @Test
    void visitorAndDesignerViewsAreNotCounted() {
        var anonymous = detail.view(100, null);
        detail.view(100, new AuthUser(1, "d@test.sg", Role.INTERIOR_DESIGNER));
        detail.view(100, new AuthUser(9, "a@test.sg", Role.USER_ADMIN));
        verify(views, never()).insertIfAbsent(anyInt(), anyInt(), any(), any());
        assertThat(anonymous.favourited()).isNull();
    }

    @Test
    void favouritedFlagReflectsCustomersList() {
        when(favourites.existsById(new FavouriteId(5, 100))).thenReturn(true);
        assertThat(detail.view(100, new AuthUser(5, "c@test.sg", Role.CUSTOMER)).favourited()).isTrue();
    }

    @Test
    @SuppressWarnings("unchecked")
    void hiddenProjectIsNotFound() {
        when(projects.findOne(any(Specification.class))).thenReturn(Optional.empty());
        assertApiError(() -> detail.view(100, null), HttpStatus.NOT_FOUND);
    }

    @Test
    void addingFavouriteTwiceUsesTheIdempotentInsert() {
        when(users.findById(5)).thenReturn(Optional.of(TestData.user(5, Role.CUSTOMER)));
        var controller = new AddFavouriteController(TestData.clockAt("2026-11-08T10:00"));
        controller.add(5, 100);
        controller.add(5, 100);
        verify(favourites, times(2)).insertIfAbsent(any(), any(), any());
        verify(favourites, never()).save(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void cannotFavouriteAHiddenProject() {
        when(users.findById(5)).thenReturn(Optional.of(TestData.user(5, Role.CUSTOMER)));
        when(projects.findOne(any(Specification.class))).thenReturn(Optional.empty());
        assertApiError(() -> new AddFavouriteController(TestData.clockAt("2026-11-08T10:00")).add(5, 100),
                HttpStatus.NOT_FOUND);
    }

    @Test
    void shortlistFigureIsTheCurrentNumberOfFavourites() {
        when(favourites.countByProjectId(100)).thenReturn(3L, 2L);
        assertThat(Favourite.shortlistCount(100)).isEqualTo(3);
        // after one customer removes it
        assertThat(Favourite.shortlistCount(100)).isEqualTo(2);
    }
}

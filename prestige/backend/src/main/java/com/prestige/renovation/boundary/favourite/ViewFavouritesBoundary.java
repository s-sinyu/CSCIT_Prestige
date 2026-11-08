package com.prestige.renovation.boundary.favourite;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.favourite.ViewFavouritesController;
import com.prestige.renovation.dto.PageResponse;
import com.prestige.renovation.dto.project.ProjectCard;

/** Boundary for user story: Customer views the favourite list. */
@RestController
public class ViewFavouritesBoundary {

    private final ViewFavouritesController controller;

    public ViewFavouritesBoundary(ViewFavouritesController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/customer/favourites")
    public PageResponse<ProjectCard> list(@AuthenticationPrincipal AuthUser me,
                                          @PageableDefault(size = 9) Pageable pageable) {
        return controller.list(me.id(), pageable);
    }
}

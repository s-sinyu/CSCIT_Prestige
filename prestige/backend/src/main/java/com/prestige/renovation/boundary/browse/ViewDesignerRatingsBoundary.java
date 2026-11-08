package com.prestige.renovation.boundary.browse;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.browse.ViewDesignerRatingsController;
import com.prestige.renovation.dto.rating.DesignerRatingsResponse;

/** Boundary for user story: Anyone views a designer's ratings and average. */
@RestController
public class ViewDesignerRatingsBoundary {

    private final ViewDesignerRatingsController controller;

    public ViewDesignerRatingsBoundary(ViewDesignerRatingsController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/designers/{id}/ratings")
    public DesignerRatingsResponse view(@PathVariable Integer id,
                                        @RequestParam(required = false) Integer projectId,
                                        @AuthenticationPrincipal AuthUser me,
                                        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return controller.view(id, projectId, me == null ? null : me.id(), pageable);
    }
}

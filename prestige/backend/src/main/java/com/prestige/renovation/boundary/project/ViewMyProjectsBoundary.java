package com.prestige.renovation.boundary.project;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.project.ViewMyProjectsController;
import com.prestige.renovation.dto.PageResponse;
import com.prestige.renovation.dto.project.DesignerProjectSummary;

/** Boundary for user story: Interior Designer lists own IDPs with interest figures. */
@RestController
public class ViewMyProjectsBoundary {

    private final ViewMyProjectsController controller;

    public ViewMyProjectsBoundary(ViewMyProjectsController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/designer/projects")
    public PageResponse<DesignerProjectSummary> list(@AuthenticationPrincipal AuthUser me,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return controller.list(me.id(), pageable);
    }
}

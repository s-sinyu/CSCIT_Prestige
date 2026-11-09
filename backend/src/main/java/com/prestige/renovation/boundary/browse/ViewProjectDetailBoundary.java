package com.prestige.renovation.boundary.browse;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.browse.ViewProjectDetailController;
import com.prestige.renovation.dto.project.ProjectDetail;

/** Boundary for user story: Visitor or customer views an IDP detail page (JWT optional). */
@RestController
public class ViewProjectDetailBoundary {

    private final ViewProjectDetailController controller;

    public ViewProjectDetailBoundary(ViewProjectDetailController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/projects/{id}")
    public ProjectDetail view(@PathVariable Integer id, @AuthenticationPrincipal AuthUser me) {
        return controller.view(id, me);
    }
}

package com.prestige.renovation.boundary.project;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.project.ViewMyProjectController;
import com.prestige.renovation.dto.project.DesignerProjectDetail;

/** Boundary for user story: Interior Designer views one own IDP. */
@RestController
public class ViewMyProjectBoundary {

    private final ViewMyProjectController controller;

    public ViewMyProjectBoundary(ViewMyProjectController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/designer/projects/{id}")
    public DesignerProjectDetail view(@AuthenticationPrincipal AuthUser me, @PathVariable Integer id) {
        return controller.view(me.id(), id);
    }
}

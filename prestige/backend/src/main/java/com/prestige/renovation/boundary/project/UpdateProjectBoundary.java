package com.prestige.renovation.boundary.project;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.project.UpdateProjectController;
import com.prestige.renovation.dto.project.DesignerProjectDetail;
import com.prestige.renovation.dto.project.ProjectRequest;

import jakarta.validation.Valid;

/** Boundary for user story: Interior Designer edits one own IDP. */
@RestController
public class UpdateProjectBoundary {

    private final UpdateProjectController controller;

    public UpdateProjectBoundary(UpdateProjectController controller) {
        this.controller = controller;
    }

    @PutMapping("/api/designer/projects/{id}")
    public DesignerProjectDetail update(@AuthenticationPrincipal AuthUser me, @PathVariable Integer id,
                                        @Valid @RequestBody ProjectRequest request) {
        return controller.update(me.id(), id, request);
    }
}

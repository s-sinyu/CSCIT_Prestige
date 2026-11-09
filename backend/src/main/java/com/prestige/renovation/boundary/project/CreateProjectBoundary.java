package com.prestige.renovation.boundary.project;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.project.CreateProjectController;
import com.prestige.renovation.dto.project.DesignerProjectDetail;
import com.prestige.renovation.dto.project.ProjectRequest;

import jakarta.validation.Valid;

/** Boundary for user story: Interior Designer creates an IDP. */
@RestController
public class CreateProjectBoundary {

    private final CreateProjectController controller;

    public CreateProjectBoundary(CreateProjectController controller) {
        this.controller = controller;
    }

    @PostMapping("/api/designer/projects")
    @ResponseStatus(HttpStatus.CREATED)
    public DesignerProjectDetail create(@AuthenticationPrincipal AuthUser me, @Valid @RequestBody ProjectRequest request) {
        return controller.create(me.id(), request);
    }
}

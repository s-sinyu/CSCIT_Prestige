package com.prestige.renovation.boundary.project;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.project.DeleteProjectController;

/** Boundary for user story: Interior Designer deletes one own IDP. */
@RestController
public class DeleteProjectBoundary {

    private final DeleteProjectController controller;

    public DeleteProjectBoundary(DeleteProjectController controller) {
        this.controller = controller;
    }

    @DeleteMapping("/api/designer/projects/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser me, @PathVariable Integer id) {
        controller.delete(me.id(), id);
    }
}

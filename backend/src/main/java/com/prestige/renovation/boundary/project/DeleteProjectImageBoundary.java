package com.prestige.renovation.boundary.project;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.project.DeleteProjectImageController;

/** Boundary for user story: Interior Designer deletes an image of own IDP. */
@RestController
public class DeleteProjectImageBoundary {

    private final DeleteProjectImageController controller;

    public DeleteProjectImageBoundary(DeleteProjectImageController controller) {
        this.controller = controller;
    }

    @DeleteMapping("/api/designer/projects/{id}/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser me, @PathVariable Integer id, @PathVariable Integer imageId) {
        controller.delete(me.id(), id, imageId);
    }
}

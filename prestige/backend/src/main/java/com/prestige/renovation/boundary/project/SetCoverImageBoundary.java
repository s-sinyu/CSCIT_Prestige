package com.prestige.renovation.boundary.project;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.project.SetCoverImageController;
import com.prestige.renovation.dto.project.ImageResponse;

/** Boundary for user story: Interior Designer sets the cover image of own IDP. */
@RestController
public class SetCoverImageBoundary {

    private final SetCoverImageController controller;

    public SetCoverImageBoundary(SetCoverImageController controller) {
        this.controller = controller;
    }

    @PutMapping("/api/designer/projects/{id}/images/{imageId}/cover")
    public List<ImageResponse> setCover(@AuthenticationPrincipal AuthUser me, @PathVariable Integer id,
                                        @PathVariable Integer imageId) {
        return controller.setCover(me.id(), id, imageId);
    }
}

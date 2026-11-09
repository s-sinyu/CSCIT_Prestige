package com.prestige.renovation.boundary.designerprofile;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.designerprofile.UpdateDesignerProfileController;
import com.prestige.renovation.dto.designer.DesignerProfileRequest;
import com.prestige.renovation.dto.designer.DesignerProfileResponse;

import jakarta.validation.Valid;

/** Boundary for user story: Interior Designer edits own designer profile. */
@RestController
public class UpdateDesignerProfileBoundary {

    private final UpdateDesignerProfileController controller;

    public UpdateDesignerProfileBoundary(UpdateDesignerProfileController controller) {
        this.controller = controller;
    }

    @PutMapping("/api/designer/profile")
    public DesignerProfileResponse update(@AuthenticationPrincipal AuthUser me, @Valid @RequestBody DesignerProfileRequest request) {
        return controller.update(me.id(), request);
    }
}

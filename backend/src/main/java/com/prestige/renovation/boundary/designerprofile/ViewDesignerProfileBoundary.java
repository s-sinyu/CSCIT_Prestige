package com.prestige.renovation.boundary.designerprofile;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.designerprofile.ViewDesignerProfileController;
import com.prestige.renovation.dto.designer.DesignerProfileResponse;

/** Boundary for user story: Interior Designer views own designer profile. */
@RestController
public class ViewDesignerProfileBoundary {

    private final ViewDesignerProfileController controller;

    public ViewDesignerProfileBoundary(ViewDesignerProfileController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/designer/profile")
    public DesignerProfileResponse view(@AuthenticationPrincipal AuthUser me) {
        return controller.view(me.id());
    }
}

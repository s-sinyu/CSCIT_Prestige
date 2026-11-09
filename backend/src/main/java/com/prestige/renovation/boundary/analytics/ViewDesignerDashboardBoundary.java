package com.prestige.renovation.boundary.analytics;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.analytics.ViewDesignerDashboardController;
import com.prestige.renovation.dto.designer.DashboardResponse;

/** Boundary for user story: Interior Designer views dashboard totals. */
@RestController
public class ViewDesignerDashboardBoundary {

    private final ViewDesignerDashboardController controller;

    public ViewDesignerDashboardBoundary(ViewDesignerDashboardController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/designer/dashboard")
    public DashboardResponse view(@AuthenticationPrincipal AuthUser me) {
        return controller.view(me.id());
    }
}

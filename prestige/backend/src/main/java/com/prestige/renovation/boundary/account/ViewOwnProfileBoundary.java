package com.prestige.renovation.boundary.account;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.account.ViewOwnProfileController;
import com.prestige.renovation.dto.auth.UserSummary;

/** Boundary for user story: Logged-in user views own profile. */
@RestController
public class ViewOwnProfileBoundary {

    private final ViewOwnProfileController controller;

    public ViewOwnProfileBoundary(ViewOwnProfileController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/me")
    public UserSummary view(@AuthenticationPrincipal AuthUser me) {
        return controller.view(me.id());
    }
}

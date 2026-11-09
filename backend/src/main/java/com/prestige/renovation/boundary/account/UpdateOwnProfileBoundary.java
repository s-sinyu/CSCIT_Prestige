package com.prestige.renovation.boundary.account;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.account.UpdateOwnProfileController;
import com.prestige.renovation.dto.auth.ProfileUpdateRequest;
import com.prestige.renovation.dto.auth.UserSummary;

import jakarta.validation.Valid;

/** Boundary for user story: Logged-in user edits own full name and phone. */
@RestController
public class UpdateOwnProfileBoundary {

    private final UpdateOwnProfileController controller;

    public UpdateOwnProfileBoundary(UpdateOwnProfileController controller) {
        this.controller = controller;
    }

    @PutMapping("/api/me")
    public UserSummary update(@AuthenticationPrincipal AuthUser me, @Valid @RequestBody ProfileUpdateRequest request) {
        return controller.update(me.id(), request);
    }
}

package com.prestige.renovation.boundary.account;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.account.ChangePasswordController;
import com.prestige.renovation.dto.auth.ChangePasswordRequest;

import jakarta.validation.Valid;

/** Boundary for user story: Logged-in user changes own password. */
@RestController
public class ChangePasswordBoundary {

    private final ChangePasswordController controller;

    public ChangePasswordBoundary(ChangePasswordController controller) {
        this.controller = controller;
    }

    @PutMapping("/api/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void change(@AuthenticationPrincipal AuthUser me, @Valid @RequestBody ChangePasswordRequest request) {
        controller.changePassword(me.id(), request);
    }
}

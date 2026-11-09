package com.prestige.renovation.boundary.admin;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.admin.CreatePlatformManagerController;
import com.prestige.renovation.dto.admin.AdminUserResponse;
import com.prestige.renovation.dto.admin.CreateManagerRequest;

import jakarta.validation.Valid;

/** Boundary for user story: User Admin creates a Platform Management account. */
@RestController
public class CreatePlatformManagerBoundary {

    private final CreatePlatformManagerController controller;

    public CreatePlatformManagerBoundary(CreatePlatformManagerController controller) {
        this.controller = controller;
    }

    @PostMapping("/api/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    public AdminUserResponse create(@AuthenticationPrincipal AuthUser me, @Valid @RequestBody CreateManagerRequest request) {
        return controller.create(me.id(), request);
    }
}

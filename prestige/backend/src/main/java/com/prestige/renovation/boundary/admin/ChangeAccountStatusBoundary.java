package com.prestige.renovation.boundary.admin;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.admin.ChangeAccountStatusController;
import com.prestige.renovation.dto.admin.AdminUserResponse;
import com.prestige.renovation.dto.admin.StatusChangeRequest;

import jakarta.validation.Valid;

/** Boundary for user story: User Admin suspends or reactivates an account. */
@RestController
public class ChangeAccountStatusBoundary {

    private final ChangeAccountStatusController controller;

    public ChangeAccountStatusBoundary(ChangeAccountStatusController controller) {
        this.controller = controller;
    }

    @PatchMapping("/api/admin/users/{id}/status")
    public AdminUserResponse changeStatus(@AuthenticationPrincipal AuthUser me, @PathVariable Integer id,
                                          @Valid @RequestBody StatusChangeRequest request) {
        return controller.changeStatus(me.id(), id, request.status());
    }
}

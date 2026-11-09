package com.prestige.renovation.boundary.admin;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.admin.DeleteUserAccountController;

/** Boundary for user story: User Admin deletes (soft-deletes) an account. */
@RestController
public class DeleteUserAccountBoundary {

    private final DeleteUserAccountController controller;

    public DeleteUserAccountBoundary(DeleteUserAccountController controller) {
        this.controller = controller;
    }

    @DeleteMapping("/api/admin/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser me, @PathVariable Integer id) {
        controller.delete(me.id(), id);
    }
}

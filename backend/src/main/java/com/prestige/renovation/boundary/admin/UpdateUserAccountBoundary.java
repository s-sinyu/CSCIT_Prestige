package com.prestige.renovation.boundary.admin;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.control.admin.UpdateUserAccountController;
import com.prestige.renovation.dto.admin.AdminUpdateUserRequest;
import com.prestige.renovation.dto.admin.AdminUserResponse;

import jakarta.validation.Valid;

/** Boundary for user story: User Admin edits an account's details. */
@RestController
public class UpdateUserAccountBoundary {

    private final UpdateUserAccountController controller;

    public UpdateUserAccountBoundary(UpdateUserAccountController controller) {
        this.controller = controller;
    }

    @PutMapping("/api/admin/users/{id}")
    public AdminUserResponse update(@PathVariable Integer id, @Valid @RequestBody AdminUpdateUserRequest request) {
        return controller.update(id, request);
    }
}

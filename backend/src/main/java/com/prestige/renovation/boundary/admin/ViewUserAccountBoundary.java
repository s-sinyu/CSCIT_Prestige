package com.prestige.renovation.boundary.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.control.admin.ViewUserAccountController;
import com.prestige.renovation.dto.admin.AdminUserResponse;

/** Boundary for user story: User Admin views one account. */
@RestController
public class ViewUserAccountBoundary {

    private final ViewUserAccountController controller;

    public ViewUserAccountBoundary(ViewUserAccountController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/admin/users/{id}")
    public AdminUserResponse view(@PathVariable Integer id) {
        return controller.view(id);
    }
}

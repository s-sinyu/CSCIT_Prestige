package com.prestige.renovation.boundary.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.control.auth.LoginController;
import com.prestige.renovation.dto.auth.LoginRequest;
import com.prestige.renovation.dto.auth.LoginResponse;

import jakarta.validation.Valid;

/** Boundary for user story: User logs in. */
@RestController
public class LoginBoundary {

    private final LoginController controller;

    public LoginBoundary(LoginController controller) {
        this.controller = controller;
    }

    @PostMapping("/api/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return controller.login(request);
    }
}

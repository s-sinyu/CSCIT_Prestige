package com.prestige.renovation.boundary.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.control.auth.RegisterController;
import com.prestige.renovation.dto.auth.RegisterRequest;
import com.prestige.renovation.dto.auth.UserSummary;

import jakarta.validation.Valid;

/** Boundary for user story: Visitor registers as a Customer or Interior Designer. */
@RestController
public class RegisterBoundary {

    private final RegisterController controller;

    public RegisterBoundary(RegisterController controller) {
        this.controller = controller;
    }

    @PostMapping("/api/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserSummary register(@Valid @RequestBody RegisterRequest request) {
        return controller.register(request);
    }
}

package com.prestige.renovation.boundary.rating;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.rating.CreateRatingController;
import com.prestige.renovation.dto.rating.RatingRequest;
import com.prestige.renovation.dto.rating.RatingResponse;

import jakarta.validation.Valid;

/** Boundary for user story: Customer rates a designer. */
@RestController
public class CreateRatingBoundary {

    private final CreateRatingController controller;

    public CreateRatingBoundary(CreateRatingController controller) {
        this.controller = controller;
    }

    @PostMapping("/api/customer/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    public RatingResponse create(@AuthenticationPrincipal AuthUser me, @Valid @RequestBody RatingRequest request) {
        return controller.create(me.id(), request);
    }
}

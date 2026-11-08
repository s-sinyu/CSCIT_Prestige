package com.prestige.renovation.boundary.rating;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.rating.UpdateRatingController;
import com.prestige.renovation.dto.rating.RatingResponse;
import com.prestige.renovation.dto.rating.RatingUpdateRequest;

import jakarta.validation.Valid;

/** Boundary for user story: Customer edits own rating. */
@RestController
public class UpdateRatingBoundary {

    private final UpdateRatingController controller;

    public UpdateRatingBoundary(UpdateRatingController controller) {
        this.controller = controller;
    }

    @PutMapping("/api/customer/ratings/{id}")
    public RatingResponse update(@AuthenticationPrincipal AuthUser me, @PathVariable Integer id,
                                 @Valid @RequestBody RatingUpdateRequest request) {
        return controller.update(me.id(), id, request);
    }
}

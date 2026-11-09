package com.prestige.renovation.boundary.rating;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.rating.DeleteOwnRatingController;

/** Boundary for user story: Customer deletes own rating. */
@RestController
public class DeleteOwnRatingBoundary {

    private final DeleteOwnRatingController controller;

    public DeleteOwnRatingBoundary(DeleteOwnRatingController controller) {
        this.controller = controller;
    }

    @DeleteMapping("/api/customer/ratings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser me, @PathVariable Integer id) {
        controller.delete(me.id(), id);
    }
}

package com.prestige.renovation.boundary.favourite;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.favourite.RemoveFavouriteController;

/** Boundary for user story: Customer removes an IDP from favourites. */
@RestController
public class RemoveFavouriteBoundary {

    private final RemoveFavouriteController controller;

    public RemoveFavouriteBoundary(RemoveFavouriteController controller) {
        this.controller = controller;
    }

    @DeleteMapping("/api/customer/favourites/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@AuthenticationPrincipal AuthUser me, @PathVariable Integer projectId) {
        controller.remove(me.id(), projectId);
    }
}

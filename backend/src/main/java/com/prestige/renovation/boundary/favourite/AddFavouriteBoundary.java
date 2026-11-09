package com.prestige.renovation.boundary.favourite;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.favourite.AddFavouriteController;

/** Boundary for user story: Customer saves an IDP to favourites. */
@RestController
public class AddFavouriteBoundary {

    private final AddFavouriteController controller;

    public AddFavouriteBoundary(AddFavouriteController controller) {
        this.controller = controller;
    }

    @PostMapping("/api/customer/favourites/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void add(@AuthenticationPrincipal AuthUser me, @PathVariable Integer projectId) {
        controller.add(me.id(), projectId);
    }
}

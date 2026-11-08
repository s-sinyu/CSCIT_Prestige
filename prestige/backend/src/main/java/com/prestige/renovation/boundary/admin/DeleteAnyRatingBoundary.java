package com.prestige.renovation.boundary.admin;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.control.admin.DeleteAnyRatingController;

/** Boundary for user story: User Admin deletes any rating. */
@RestController
public class DeleteAnyRatingBoundary {

    private final DeleteAnyRatingController controller;

    public DeleteAnyRatingBoundary(DeleteAnyRatingController controller) {
        this.controller = controller;
    }

    @DeleteMapping("/api/admin/ratings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        controller.delete(id);
    }
}

package com.prestige.renovation.control.rating;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.entity.Rating;

/** Customers may only change their own ratings; others' ratings are reported as 404. */
final class RatingOwnership {

    private RatingOwnership() {
    }

    static Rating requireOwn(Integer customerId, Integer ratingId) {
        return Rating.findById(ratingId)
                .filter(r -> r.isWrittenBy(customerId))
                .orElseThrow(() -> ApiException.notFound("Rating not found"));
    }
}

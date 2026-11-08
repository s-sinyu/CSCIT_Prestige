package com.prestige.renovation.control.rating;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Control for user story: Customer deletes own rating (FR-RT-3). */
@Service
public class DeleteOwnRatingController {

    @Transactional
    public void delete(Integer customerId, Integer ratingId) {
        RatingOwnership.requireOwn(customerId, ratingId).delete();
    }
}

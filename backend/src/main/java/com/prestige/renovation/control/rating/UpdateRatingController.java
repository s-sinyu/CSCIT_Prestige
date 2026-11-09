package com.prestige.renovation.control.rating;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.rating.RatingResponse;
import com.prestige.renovation.dto.rating.RatingUpdateRequest;
import com.prestige.renovation.entity.Rating;

/** Control for user story: Customer edits the score and comment of own rating (FR-RT-3). */
@Service
public class UpdateRatingController {

    @Transactional
    public RatingResponse update(Integer customerId, Integer ratingId, RatingUpdateRequest request) {
        Rating rating = RatingOwnership.requireOwn(customerId, ratingId);
        rating.update(request.score(), ControlSupport.clean(request.comment()));
        return RatingResponse.from(rating.save(), customerId);
    }
}

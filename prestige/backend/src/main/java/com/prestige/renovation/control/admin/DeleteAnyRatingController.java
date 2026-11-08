package com.prestige.renovation.control.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.entity.Rating;

/** Control for user story: User Admin deletes any rating permanently (FR-RT-5). */
@Service
public class DeleteAnyRatingController {

    @Transactional
    public void delete(Integer ratingId) {
        Rating rating = Rating.findById(ratingId).orElseThrow(() -> ApiException.notFound("Rating not found"));
        rating.delete();
    }
}

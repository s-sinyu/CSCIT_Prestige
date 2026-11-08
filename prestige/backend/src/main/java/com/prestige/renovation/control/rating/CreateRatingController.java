package com.prestige.renovation.control.rating;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.rating.RatingRequest;
import com.prestige.renovation.dto.rating.RatingResponse;
import com.prestige.renovation.entity.InteriorDesignProject;
import com.prestige.renovation.entity.Rating;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;

/**
 * Control for user story: Customer rates a designer, optionally for one IDP (FR-RT-1, FR-RT-2).
 * At most one general rating per designer plus one per IDP; duplicates return 409.
 */
@Service
public class CreateRatingController {

    @Transactional
    public RatingResponse create(Integer customerId, RatingRequest request) {
        User customer = ControlSupport.requireRole(customerId, Role.CUSTOMER);
        User designer = ControlSupport.requireActiveDesigner(request.designerId());

        InteriorDesignProject project = null;
        if (request.projectId() != null) {
            project = InteriorDesignProject.findVisibleById(request.projectId())
                    .filter(p -> p.isOwnedBy(designer.getId()))
                    .orElseThrow(() -> ApiException.badRequest("projectId",
                            "The project must be a visible project of this designer"));
            if (Rating.projectRatingExists(customerId, project.getId())) {
                throw ApiException.conflict("You have already rated this project. Edit your existing rating instead.");
            }
        } else if (Rating.generalRatingExists(customerId, designer.getId())) {
            throw ApiException.conflict("You have already rated this designer. Edit your existing rating instead.");
        }

        Rating rating = new Rating(customer, designer, project, request.score(),
                ControlSupport.clean(request.comment()));
        return RatingResponse.from(rating.save(), customerId);
    }
}

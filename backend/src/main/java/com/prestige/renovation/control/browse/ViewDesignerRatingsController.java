package com.prestige.renovation.control.browse;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.PageResponse;
import com.prestige.renovation.dto.rating.DesignerRatingsResponse;
import com.prestige.renovation.dto.rating.RatingResponse;
import com.prestige.renovation.entity.Rating;

/**
 * Control for user story: Anyone views a designer's average rating and list of ratings (FR-RT-4).
 * Ratings of a suspended or deleted designer are hidden (FR-RT-6).
 */
@Service
public class ViewDesignerRatingsController {

    @Transactional(readOnly = true)
    public DesignerRatingsResponse view(Integer designerId, Integer projectId, Integer callerId, Pageable pageable) {
        ControlSupport.requireActiveDesigner(designerId);
        Rating.Summary summary = Rating.summaryFor(designerId);
        PageResponse<RatingResponse> page = PageResponse.of(Rating.findForDesigner(designerId, projectId, pageable),
                r -> RatingResponse.from(r, callerId));
        return new DesignerRatingsResponse(designerId, summary.average(), summary.count(), page);
    }
}

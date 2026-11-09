package com.prestige.renovation.dto.rating;

import java.math.BigDecimal;

import com.prestige.renovation.dto.PageResponse;

public record DesignerRatingsResponse(Integer designerId, BigDecimal average, long count,
                                      PageResponse<RatingResponse> ratings) {
}

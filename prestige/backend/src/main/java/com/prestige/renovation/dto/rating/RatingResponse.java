package com.prestige.renovation.dto.rating;

import java.time.Instant;

import com.prestige.renovation.entity.Rating;

/**
 * A rating as shown publicly (FR-RT-4). {@code projectRemoved} labels ratings of a soft-deleted IDP (FR-RT-6);
 * {@code mine} tells a logged-in customer which ratings they can edit or delete.
 */
public record RatingResponse(Integer id, Integer designerId, int score, String comment, String customerName,
                             Instant createdAt, Instant updatedAt, Integer projectId, String projectTitle,
                             boolean projectRemoved, boolean mine) {

    public static RatingResponse from(Rating r, Integer callerId) {
        var p = r.getProject();
        return new RatingResponse(r.getId(), r.getDesigner().getId(), r.getScore(), r.getComment(),
                r.getCustomer().getFullName(), r.getCreatedAt(), r.getUpdatedAt(),
                p == null ? null : p.getId(), p == null ? null : p.getTitle(),
                p != null && p.isDeleted(), callerId != null && r.isWrittenBy(callerId));
    }
}

package com.prestige.renovation.dto.enquiry;

import java.time.Instant;

import com.prestige.renovation.entity.Enquiry;
import com.prestige.renovation.entity.EnquiryStatus;

/** Full enquiry for the designer, with the customer's current contact details (FR-ENQ-2). */
public record EnquiryDetail(Integer id, String message, EnquiryStatus status, Instant createdAt, Instant readAt,
                            Instant repliedAt, String customerName, String customerEmail, String customerPhone,
                            Integer projectId, String projectTitle, boolean projectRemoved) {

    public static EnquiryDetail from(Enquiry e) {
        return new EnquiryDetail(e.getId(), e.getMessage(), e.getStatus(), e.getCreatedAt(), e.getReadAt(),
                e.getRepliedAt(), e.getCustomer().getFullName(), e.getCustomer().getEmail(),
                e.getCustomer().getPhone(), e.getProject().getId(), e.getProject().getTitle(),
                e.getProject().isDeleted());
    }
}

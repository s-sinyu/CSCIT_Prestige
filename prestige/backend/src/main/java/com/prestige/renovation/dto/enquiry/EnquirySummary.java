package com.prestige.renovation.dto.enquiry;

import java.time.Instant;

import com.prestige.renovation.entity.Enquiry;
import com.prestige.renovation.entity.EnquiryStatus;

public record EnquirySummary(Integer id, String customerName, Integer projectId, String projectTitle,
                             String snippet, EnquiryStatus status, Instant createdAt) {

    public static EnquirySummary from(Enquiry e) {
        String msg = e.getMessage();
        String snippet = msg.length() <= 100 ? msg : msg.substring(0, 100) + "...";
        return new EnquirySummary(e.getId(), e.getCustomer().getFullName(), e.getProject().getId(),
                e.getProject().getTitle(), snippet, e.getStatus(), e.getCreatedAt());
    }
}

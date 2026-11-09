package com.prestige.renovation.dto.enquiry;

import java.time.Instant;

import com.prestige.renovation.entity.EnquiryStatus;

public record EnquiryCreated(Integer id, Integer projectId, EnquiryStatus status, Instant createdAt) {
}

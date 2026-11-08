package com.prestige.renovation.control.enquiry;

import java.time.Clock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.dto.enquiry.EnquiryDetail;
import com.prestige.renovation.entity.Enquiry;
import com.prestige.renovation.entity.EnquiryStatus;

/** Control for user story: Interior Designer marks an enquiry READ or REPLIED; never back to NEW (FR-ENQ-3). */
@Service
public class UpdateEnquiryStatusController {

    private final Clock clock;

    public UpdateEnquiryStatusController(Clock clock) {
        this.clock = clock;
    }

    @Transactional
    public EnquiryDetail updateStatus(Integer designerId, Integer enquiryId, EnquiryStatus status) {
        Enquiry enquiry = EnquiryOwnership.requireOwned(designerId, enquiryId);
        if (status == EnquiryStatus.NEW || !enquiry.changeStatus(status, clock.instant())) {
            throw ApiException.badRequest("status",
                    "Cannot change status from " + enquiry.getStatus() + " to " + status);
        }
        return EnquiryDetail.from(enquiry.save());
    }
}

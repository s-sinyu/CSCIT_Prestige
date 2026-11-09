package com.prestige.renovation.control.enquiry;

import java.time.Clock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.dto.enquiry.EnquiryDetail;
import com.prestige.renovation.entity.Enquiry;
import com.prestige.renovation.entity.EnquiryStatus;

/**
 * Control for user story: Interior Designer opens an enquiry to see the full message and customer contact
 * details (FR-ENQ-2). Opening a NEW enquiry marks it READ (FR-ENQ-3).
 */
@Service
public class ViewEnquiryDetailController {

    private final Clock clock;

    public ViewEnquiryDetailController(Clock clock) {
        this.clock = clock;
    }

    @Transactional
    public EnquiryDetail view(Integer designerId, Integer enquiryId) {
        Enquiry enquiry = EnquiryOwnership.requireOwned(designerId, enquiryId);
        if (enquiry.getStatus() == EnquiryStatus.NEW) {
            enquiry.changeStatus(EnquiryStatus.READ, clock.instant());
            enquiry.save();
        }
        return EnquiryDetail.from(enquiry);
    }
}

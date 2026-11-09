package com.prestige.renovation.control.enquiry;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.entity.Enquiry;

/** A designer may only see and update enquiries about their own IDPs (FR-ENQ-4); others are reported as 404. */
final class EnquiryOwnership {

    private EnquiryOwnership() {
    }

    static Enquiry requireOwned(Integer designerId, Integer enquiryId) {
        return Enquiry.findById(enquiryId)
                .filter(e -> e.belongsToDesigner(designerId))
                .orElseThrow(() -> ApiException.notFound("Enquiry not found"));
    }
}

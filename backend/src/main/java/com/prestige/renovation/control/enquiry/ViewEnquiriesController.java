package com.prestige.renovation.control.enquiry;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.dto.PageResponse;
import com.prestige.renovation.dto.enquiry.EnquirySummary;
import com.prestige.renovation.entity.Enquiry;
import com.prestige.renovation.entity.EnquiryStatus;

/** Control for user story: Interior Designer views enquiries received on own IDPs, filtered by status (FR-ENQ-1, FR-ENQ-4). */
@Service
public class ViewEnquiriesController {

    @Transactional(readOnly = true)
    public PageResponse<EnquirySummary> list(Integer designerId, EnquiryStatus status, Pageable pageable) {
        return PageResponse.of(Enquiry.findForDesigner(designerId, status, pageable), EnquirySummary::from);
    }
}

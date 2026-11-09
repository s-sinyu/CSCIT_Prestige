package com.prestige.renovation.boundary.enquiry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.enquiry.ViewEnquiriesController;
import com.prestige.renovation.dto.PageResponse;
import com.prestige.renovation.dto.enquiry.EnquirySummary;
import com.prestige.renovation.entity.EnquiryStatus;

/** Boundary for user story: Interior Designer views the enquiry inbox. */
@RestController
public class ViewEnquiriesBoundary {

    private final ViewEnquiriesController controller;

    public ViewEnquiriesBoundary(ViewEnquiriesController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/designer/enquiries")
    public PageResponse<EnquirySummary> list(@AuthenticationPrincipal AuthUser me,
                                             @RequestParam(required = false) EnquiryStatus status,
                                             @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return controller.list(me.id(), status, pageable);
    }
}

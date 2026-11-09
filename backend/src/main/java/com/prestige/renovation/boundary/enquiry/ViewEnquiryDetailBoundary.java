package com.prestige.renovation.boundary.enquiry;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.enquiry.ViewEnquiryDetailController;
import com.prestige.renovation.dto.enquiry.EnquiryDetail;

/** Boundary for user story: Interior Designer opens an enquiry. */
@RestController
public class ViewEnquiryDetailBoundary {

    private final ViewEnquiryDetailController controller;

    public ViewEnquiryDetailBoundary(ViewEnquiryDetailController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/designer/enquiries/{id}")
    public EnquiryDetail view(@AuthenticationPrincipal AuthUser me, @PathVariable Integer id) {
        return controller.view(me.id(), id);
    }
}

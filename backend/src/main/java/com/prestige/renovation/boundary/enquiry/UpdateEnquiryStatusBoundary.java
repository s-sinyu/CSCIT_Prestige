package com.prestige.renovation.boundary.enquiry;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.enquiry.UpdateEnquiryStatusController;
import com.prestige.renovation.dto.enquiry.EnquiryDetail;
import com.prestige.renovation.dto.enquiry.EnquiryStatusRequest;

import jakarta.validation.Valid;

/** Boundary for user story: Interior Designer marks an enquiry READ or REPLIED. */
@RestController
public class UpdateEnquiryStatusBoundary {

    private final UpdateEnquiryStatusController controller;

    public UpdateEnquiryStatusBoundary(UpdateEnquiryStatusController controller) {
        this.controller = controller;
    }

    @PatchMapping("/api/designer/enquiries/{id}/status")
    public EnquiryDetail updateStatus(@AuthenticationPrincipal AuthUser me, @PathVariable Integer id,
                                      @Valid @RequestBody EnquiryStatusRequest request) {
        return controller.updateStatus(me.id(), id, request.status());
    }
}

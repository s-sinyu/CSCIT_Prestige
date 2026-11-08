package com.prestige.renovation.boundary.enquiry;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.enquiry.SendEnquiryController;
import com.prestige.renovation.dto.enquiry.EnquiryCreated;
import com.prestige.renovation.dto.enquiry.EnquiryRequest;

import jakarta.validation.Valid;

/** Boundary for user story: Customer sends an enquiry about an IDP. */
@RestController
public class SendEnquiryBoundary {

    private final SendEnquiryController controller;

    public SendEnquiryBoundary(SendEnquiryController controller) {
        this.controller = controller;
    }

    @PostMapping("/api/projects/{id}/enquiries")
    @ResponseStatus(HttpStatus.CREATED)
    public EnquiryCreated send(@AuthenticationPrincipal AuthUser me, @PathVariable Integer id,
                               @Valid @RequestBody EnquiryRequest request) {
        return controller.send(me.id(), id, request);
    }
}

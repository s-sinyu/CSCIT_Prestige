package com.prestige.renovation.control.enquiry;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.enquiry.EnquiryCreated;
import com.prestige.renovation.dto.enquiry.EnquiryRequest;
import com.prestige.renovation.entity.Enquiry;
import com.prestige.renovation.entity.InteriorDesignProject;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;

/** Control for user story: Customer sends an enquiry about an IDP to its designer (FR-CUS-5). */
@Service
public class SendEnquiryController {

    @Transactional
    public EnquiryCreated send(Integer customerId, Integer projectId, EnquiryRequest request) {
        User customer = ControlSupport.requireRole(customerId, Role.CUSTOMER);
        InteriorDesignProject project = InteriorDesignProject.findVisibleById(projectId)
                .orElseThrow(() -> ApiException.notFound("Project not found"));
        Enquiry enquiry = new Enquiry(project, customer, request.message().trim()).save();
        return new EnquiryCreated(enquiry.getId(), project.getId(), enquiry.getStatus(), enquiry.getCreatedAt());
    }
}

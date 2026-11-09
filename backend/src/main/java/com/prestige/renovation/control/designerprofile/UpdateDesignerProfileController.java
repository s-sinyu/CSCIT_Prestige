package com.prestige.renovation.control.designerprofile;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.designer.DesignerProfileRequest;
import com.prestige.renovation.dto.designer.DesignerProfileResponse;
import com.prestige.renovation.entity.DesignerProfile;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;

/** Control for user story: Interior Designer edits bio, years of experience and specialisation (FR-CO-5). */
@Service
public class UpdateDesignerProfileController {

    @Transactional
    public DesignerProfileResponse update(Integer designerId, DesignerProfileRequest request) {
        User designer = ControlSupport.requireRole(designerId, Role.INTERIOR_DESIGNER);
        DesignerProfile profile = DesignerProfile.findByDesigner(designerId)
                .orElseGet(() -> new DesignerProfile(designer));
        profile.update(ControlSupport.clean(request.bio()), request.yearsOfExperience(),
                ControlSupport.clean(request.specialisation()));
        return DesignerProfileResponse.from(profile.save());
    }
}

package com.prestige.renovation.control.designerprofile;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.dto.designer.DesignerProfileResponse;
import com.prestige.renovation.entity.DesignerProfile;

/** Control for user story: Interior Designer views own designer profile (FR-CO-5). */
@Service
public class ViewDesignerProfileController {

    @Transactional(readOnly = true)
    public DesignerProfileResponse view(Integer designerId) {
        return DesignerProfileResponse.from(DesignerProfile.findByDesigner(designerId).orElse(null));
    }
}

package com.prestige.renovation.dto.designer;

import com.prestige.renovation.entity.DesignerProfile;

public record DesignerProfileResponse(String bio, Integer yearsOfExperience, String specialisation) {

    public static final DesignerProfileResponse EMPTY = new DesignerProfileResponse(null, null, null);

    public static DesignerProfileResponse from(DesignerProfile p) {
        return p == null ? EMPTY : new DesignerProfileResponse(p.getBio(), p.getYearsOfExperience(), p.getSpecialisation());
    }
}

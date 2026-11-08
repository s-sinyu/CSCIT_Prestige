package com.prestige.renovation.control.common;

import com.prestige.renovation.dto.company.CompanyResponse;
import com.prestige.renovation.dto.designer.DesignerInfo;
import com.prestige.renovation.dto.designer.DesignerProfileResponse;
import com.prestige.renovation.entity.DesignerCompany;
import com.prestige.renovation.entity.DesignerProfile;
import com.prestige.renovation.entity.Rating;
import com.prestige.renovation.entity.User;

/** Public designer information block: company, profile and rating summary. */
public final class DesignerInfoAssembler {

    private DesignerInfoAssembler() {
    }

    public static DesignerInfo build(User designer) {
        DesignerCompany company = DesignerCompany.findByDesigner(designer.getId()).orElse(null);
        DesignerProfile profile = DesignerProfile.findByDesigner(designer.getId()).orElse(null);
        Rating.Summary summary = Rating.summaryFor(designer.getId());
        return new DesignerInfo(designer.getId(), designer.getFullName(),
                ProjectCardAssembler.displayName(designer, company),
                DesignerProfileResponse.from(profile), CompanyResponse.from(company),
                summary.average(), summary.count());
    }
}

package com.prestige.renovation.control.company;

import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.company.CompanyRequest;
import com.prestige.renovation.entity.DesignerCompany;

/** Copies validated request fields onto the entity (shared by create and update). */
final class CompanyFields {

    private CompanyFields() {
    }

    static void apply(DesignerCompany company, CompanyRequest r) {
        company.update(r.companyName().trim(), ControlSupport.clean(r.registrationNumber()),
                ControlSupport.clean(r.description()), ControlSupport.clean(r.address()),
                ControlSupport.clean(r.contactEmail()), ControlSupport.clean(r.contactPhone()),
                ControlSupport.clean(r.websiteUrl()));
    }
}

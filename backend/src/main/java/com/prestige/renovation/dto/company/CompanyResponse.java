package com.prestige.renovation.dto.company;

import com.prestige.renovation.dto.Urls;
import com.prestige.renovation.entity.DesignerCompany;

public record CompanyResponse(Integer id, String companyName, String registrationNumber, String description,
                              String address, String contactEmail, String contactPhone, String websiteUrl,
                              String logoUrl) {

    public static CompanyResponse from(DesignerCompany c) {
        if (c == null) {
            return null;
        }
        return new CompanyResponse(c.getId(), c.getCompanyName(), c.getRegistrationNumber(), c.getDescription(),
                c.getAddress(), c.getContactEmail(), c.getContactPhone(), c.getWebsiteUrl(), Urls.file(c.getLogoKey()));
    }
}

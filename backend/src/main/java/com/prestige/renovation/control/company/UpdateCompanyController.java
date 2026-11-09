package com.prestige.renovation.control.company;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.dto.company.CompanyRequest;
import com.prestige.renovation.dto.company.CompanyResponse;
import com.prestige.renovation.entity.DesignerCompany;

/** Control for user story: Interior Designer updates own company information (FR-CO-2). */
@Service
public class UpdateCompanyController {

    @Transactional
    public CompanyResponse update(Integer designerId, CompanyRequest request) {
        DesignerCompany company = DesignerCompany.findByDesigner(designerId)
                .orElseThrow(() -> ApiException.notFound("You have not added company information yet"));
        CompanyFields.apply(company, request);
        return CompanyResponse.from(company.save());
    }
}

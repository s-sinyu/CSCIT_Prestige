package com.prestige.renovation.control.company;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.dto.company.CompanyResponse;
import com.prestige.renovation.entity.DesignerCompany;

/** Control for user story: Interior Designer views own company information (FR-CO-2). */
@Service
public class ViewCompanyController {

    @Transactional(readOnly = true)
    public CompanyResponse view(Integer designerId) {
        return DesignerCompany.findByDesigner(designerId).map(CompanyResponse::from)
                .orElseThrow(() -> ApiException.notFound("You have not added company information yet"));
    }
}

package com.prestige.renovation.control.company;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.company.CompanyRequest;
import com.prestige.renovation.dto.company.CompanyResponse;
import com.prestige.renovation.entity.DesignerCompany;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;

/** Control for user story: Interior Designer creates company information (FR-CO-1, FR-CO-2). */
@Service
public class CreateCompanyController {

    @Transactional
    public CompanyResponse create(Integer designerId, CompanyRequest request) {
        User designer = ControlSupport.requireRole(designerId, Role.INTERIOR_DESIGNER);
        if (DesignerCompany.existsForDesigner(designerId)) {
            throw ApiException.conflict("You already have company information. Edit it instead.");
        }
        DesignerCompany company = new DesignerCompany(designer);
        CompanyFields.apply(company, request);
        return CompanyResponse.from(company.save());
    }
}

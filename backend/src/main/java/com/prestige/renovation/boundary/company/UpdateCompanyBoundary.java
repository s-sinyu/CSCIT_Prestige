package com.prestige.renovation.boundary.company;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.company.UpdateCompanyController;
import com.prestige.renovation.dto.company.CompanyRequest;
import com.prestige.renovation.dto.company.CompanyResponse;

import jakarta.validation.Valid;

/** Boundary for user story: Interior Designer updates own company information. */
@RestController
public class UpdateCompanyBoundary {

    private final UpdateCompanyController controller;

    public UpdateCompanyBoundary(UpdateCompanyController controller) {
        this.controller = controller;
    }

    @PutMapping("/api/designer/company")
    public CompanyResponse update(@AuthenticationPrincipal AuthUser me, @Valid @RequestBody CompanyRequest request) {
        return controller.update(me.id(), request);
    }
}

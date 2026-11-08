package com.prestige.renovation.boundary.company;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.company.ViewCompanyController;
import com.prestige.renovation.dto.company.CompanyResponse;

/** Boundary for user story: Interior Designer views own company information. */
@RestController
public class ViewCompanyBoundary {

    private final ViewCompanyController controller;

    public ViewCompanyBoundary(ViewCompanyController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/designer/company")
    public CompanyResponse view(@AuthenticationPrincipal AuthUser me) {
        return controller.view(me.id());
    }
}

package com.prestige.renovation.boundary.company;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.company.CreateCompanyController;
import com.prestige.renovation.dto.company.CompanyRequest;
import com.prestige.renovation.dto.company.CompanyResponse;

import jakarta.validation.Valid;

/** Boundary for user story: Interior Designer creates company information. */
@RestController
public class CreateCompanyBoundary {

    private final CreateCompanyController controller;

    public CreateCompanyBoundary(CreateCompanyController controller) {
        this.controller = controller;
    }

    @PostMapping("/api/designer/company")
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyResponse create(@AuthenticationPrincipal AuthUser me, @Valid @RequestBody CompanyRequest request) {
        return controller.create(me.id(), request);
    }
}

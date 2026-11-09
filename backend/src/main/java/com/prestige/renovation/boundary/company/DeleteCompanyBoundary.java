package com.prestige.renovation.boundary.company;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.company.DeleteCompanyController;

/** Boundary for user story: Interior Designer deletes own company information. */
@RestController
public class DeleteCompanyBoundary {

    private final DeleteCompanyController controller;

    public DeleteCompanyBoundary(DeleteCompanyController controller) {
        this.controller = controller;
    }

    @DeleteMapping("/api/designer/company")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser me) {
        controller.delete(me.id());
    }
}

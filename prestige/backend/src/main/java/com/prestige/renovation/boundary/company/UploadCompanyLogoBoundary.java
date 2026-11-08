package com.prestige.renovation.boundary.company;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.company.UploadCompanyLogoController;
import com.prestige.renovation.dto.company.CompanyResponse;

/** Boundary for user story: Interior Designer uploads the company logo. */
@RestController
public class UploadCompanyLogoBoundary {

    private final UploadCompanyLogoController controller;

    public UploadCompanyLogoBoundary(UploadCompanyLogoController controller) {
        this.controller = controller;
    }

    @PostMapping(value = "/api/designer/company/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompanyResponse upload(@AuthenticationPrincipal AuthUser me, @RequestParam("file") MultipartFile file) {
        return controller.upload(me.id(), file);
    }
}

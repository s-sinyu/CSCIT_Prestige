package com.prestige.renovation.control.company;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.common.storage.StorageService;
import com.prestige.renovation.entity.DesignerCompany;

/** Control for user story: Interior Designer deletes own company information; IDPs remain (FR-CO-2). */
@Service
public class DeleteCompanyController {

    private final StorageService storage;

    public DeleteCompanyController(StorageService storage) {
        this.storage = storage;
    }

    @Transactional
    public void delete(Integer designerId) {
        DesignerCompany company = DesignerCompany.findByDesigner(designerId)
                .orElseThrow(() -> ApiException.notFound("You have not added company information yet"));
        String logo = company.getLogoKey();
        company.delete();
        if (logo != null) {
            storage.delete(logo);
        }
    }
}

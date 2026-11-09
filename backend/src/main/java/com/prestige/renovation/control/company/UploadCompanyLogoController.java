package com.prestige.renovation.control.company;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.common.storage.ImageValidator;
import com.prestige.renovation.common.storage.StorageService;
import com.prestige.renovation.dto.company.CompanyResponse;
import com.prestige.renovation.entity.DesignerCompany;

/** Control for user story: Interior Designer uploads or replaces the company logo (FR-CO-1). */
@Service
public class UploadCompanyLogoController {

    private final StorageService storage;
    private final ImageValidator validator;

    public UploadCompanyLogoController(StorageService storage, ImageValidator validator) {
        this.storage = storage;
        this.validator = validator;
    }

    @Transactional
    public CompanyResponse upload(Integer designerId, MultipartFile file) {
        DesignerCompany company = DesignerCompany.findByDesigner(designerId)
                .orElseThrow(() -> ApiException.notFound("Add your company information before uploading a logo"));
        ImageValidator.ValidImage image = validator.validate(file);
        String oldKey = company.getLogoKey();
        company.setLogoKey(storage.store(image.data(), image.contentType(), image.extension()));
        DesignerCompany saved = company.save();
        if (oldKey != null) {
            storage.delete(oldKey);
        }
        return CompanyResponse.from(saved);
    }
}

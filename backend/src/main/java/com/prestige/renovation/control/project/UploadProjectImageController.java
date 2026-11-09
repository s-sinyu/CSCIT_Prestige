package com.prestige.renovation.control.project;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.prestige.renovation.common.config.AppProperties;
import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.common.storage.ImageValidator;
import com.prestige.renovation.common.storage.StorageService;
import com.prestige.renovation.dto.project.ImageResponse;
import com.prestige.renovation.entity.InteriorDesignProject;
import com.prestige.renovation.entity.ProjectImage;

/**
 * Control for user story: Interior Designer uploads an image to own IDP (FR-IDP-3, FR-IDP-5).
 * The first image becomes the cover. At most 10 images per IDP.
 */
@Service
public class UploadProjectImageController {

    private final StorageService storage;
    private final ImageValidator validator;
    private final int maxImages;

    public UploadProjectImageController(StorageService storage, ImageValidator validator, AppProperties properties) {
        this.storage = storage;
        this.validator = validator;
        this.maxImages = properties.images() == null || properties.images().maxPerProject() <= 0
                ? 10 : properties.images().maxPerProject();
    }

    @Transactional
    public ImageResponse upload(Integer designerId, Integer projectId, MultipartFile file) {
        InteriorDesignProject project = DesignerProjectSupport.requireOwned(designerId, projectId);
        long existing = ProjectImage.countByProject(projectId);
        if (existing >= maxImages) {
            throw ApiException.badRequest("file", "A project can have at most " + maxImages + " images");
        }
        ImageValidator.ValidImage image = validator.validate(file);
        String key = storage.store(image.data(), image.contentType(), image.extension());
        ProjectImage saved = new ProjectImage(project, key, ProjectImage.nextSortOrder(projectId), existing == 0).save();
        return ImageResponse.from(saved);
    }
}

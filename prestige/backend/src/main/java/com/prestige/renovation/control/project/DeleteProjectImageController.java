package com.prestige.renovation.control.project;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.common.storage.StorageService;
import com.prestige.renovation.entity.ProjectImage;

/**
 * Control for user story: Interior Designer deletes an image from own IDP (FR-IDP-3).
 * If the cover is deleted, the next image in order becomes the cover.
 */
@Service
public class DeleteProjectImageController {

    private final StorageService storage;

    public DeleteProjectImageController(StorageService storage) {
        this.storage = storage;
    }

    @Transactional
    public void delete(Integer designerId, Integer projectId, Integer imageId) {
        DesignerProjectSupport.requireOwned(designerId, projectId);
        List<ProjectImage> images = ProjectImage.findByProject(projectId);
        ProjectImage target = images.stream().filter(i -> i.getId().equals(imageId)).findFirst()
                .orElseThrow(() -> ApiException.notFound("Image not found"));
        boolean wasCover = target.isCover();
        String key = target.getFileKey();
        target.delete();
        storage.delete(key);
        if (wasCover) {
            images.stream().filter(i -> !i.getId().equals(imageId)).findFirst().ifPresent(next -> {
                next.setCover(true);
                next.save();
            });
        }
    }
}

package com.prestige.renovation.control.project;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.dto.project.ImageResponse;
import com.prestige.renovation.entity.ProjectImage;

/** Control for user story: Interior Designer chooses the cover image of own IDP (FR-IDP-3). */
@Service
public class SetCoverImageController {

    @Transactional
    public List<ImageResponse> setCover(Integer designerId, Integer projectId, Integer imageId) {
        DesignerProjectSupport.requireOwned(designerId, projectId);
        List<ProjectImage> images = ProjectImage.findByProject(projectId);
        if (images.stream().noneMatch(i -> i.getId().equals(imageId))) {
            throw ApiException.notFound("Image not found");
        }
        for (ProjectImage image : images) {
            boolean cover = image.getId().equals(imageId);
            if (image.isCover() != cover) {
                image.setCover(cover);
                image.save();
            }
        }
        return images.stream().map(ImageResponse::from).toList();
    }
}

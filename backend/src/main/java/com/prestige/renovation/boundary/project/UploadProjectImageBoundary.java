package com.prestige.renovation.boundary.project;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.project.UploadProjectImageController;
import com.prestige.renovation.dto.project.ImageResponse;

/** Boundary for user story: Interior Designer uploads an image to own IDP. */
@RestController
public class UploadProjectImageBoundary {

    private final UploadProjectImageController controller;

    public UploadProjectImageBoundary(UploadProjectImageController controller) {
        this.controller = controller;
    }

    @PostMapping(value = "/api/designer/projects/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ImageResponse upload(@AuthenticationPrincipal AuthUser me, @PathVariable Integer id,
                                @RequestParam("file") MultipartFile file) {
        return controller.upload(me.id(), id, file);
    }
}

package com.prestige.renovation.dto.project;

import com.prestige.renovation.dto.Urls;
import com.prestige.renovation.entity.ProjectImage;

public record ImageResponse(Integer id, String url, boolean cover, int sortOrder) {

    public static ImageResponse from(ProjectImage i) {
        return new ImageResponse(i.getId(), Urls.file(i.getFileKey()), i.isCover(), i.getSortOrder());
    }
}

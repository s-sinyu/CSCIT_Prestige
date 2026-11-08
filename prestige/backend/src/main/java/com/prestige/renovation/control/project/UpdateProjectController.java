package com.prestige.renovation.control.project;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.dto.project.DesignerProjectDetail;
import com.prestige.renovation.dto.project.ProjectRequest;
import com.prestige.renovation.entity.InteriorDesignProject;

/** Control for user story: Interior Designer edits one of own IDPs (FR-IDP-2). */
@Service
public class UpdateProjectController {

    @Transactional
    public DesignerProjectDetail update(Integer designerId, Integer projectId, ProjectRequest request) {
        InteriorDesignProject project = DesignerProjectSupport.requireOwned(designerId, projectId);
        DesignerProjectSupport.apply(project,
                DesignerProjectSupport.resolveCategory(request.categoryId(), project.getCategory()), request);
        return DesignerProjectSupport.toDetail(project.save());
    }
}

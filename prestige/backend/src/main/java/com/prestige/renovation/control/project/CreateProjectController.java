package com.prestige.renovation.control.project;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.project.DesignerProjectDetail;
import com.prestige.renovation.dto.project.ProjectRequest;
import com.prestige.renovation.entity.InteriorDesignProject;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;

/** Control for user story: Interior Designer creates an IDP in their portfolio (FR-IDP-1, FR-IDP-2). */
@Service
public class CreateProjectController {

    @Transactional
    public DesignerProjectDetail create(Integer designerId, ProjectRequest request) {
        User designer = ControlSupport.requireRole(designerId, Role.INTERIOR_DESIGNER);
        InteriorDesignProject project = new InteriorDesignProject(designer);
        DesignerProjectSupport.apply(project, DesignerProjectSupport.resolveCategory(request.categoryId(), null), request);
        return DesignerProjectSupport.toDetail(project.save());
    }
}

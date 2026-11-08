package com.prestige.renovation.control.project;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.dto.project.DesignerProjectDetail;

/** Control for user story: Interior Designer views one of own IDPs with its views and shortlists (FR-IDP-2). */
@Service
public class ViewMyProjectController {

    @Transactional(readOnly = true)
    public DesignerProjectDetail view(Integer designerId, Integer projectId) {
        return DesignerProjectSupport.toDetail(DesignerProjectSupport.requireOwned(designerId, projectId));
    }
}

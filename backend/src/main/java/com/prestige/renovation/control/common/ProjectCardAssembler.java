package com.prestige.renovation.control.common;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.prestige.renovation.dto.Urls;
import com.prestige.renovation.dto.project.ProjectCard;
import com.prestige.renovation.entity.DesignerCompany;
import com.prestige.renovation.entity.InteriorDesignProject;
import com.prestige.renovation.entity.ProjectImage;
import com.prestige.renovation.entity.User;

/**
 * Builds result cards for a page of IDPs with two batch queries (companies, cover images) instead of
 * one query per card, avoiding N+1 queries (PRD section 4).
 */
public final class ProjectCardAssembler {

    private ProjectCardAssembler() {
    }

    public static List<ProjectCard> toCards(List<InteriorDesignProject> projects) {
        Set<Integer> designerIds = projects.stream().map(p -> p.getDesigner().getId()).collect(Collectors.toSet());
        Set<Integer> projectIds = projects.stream().map(InteriorDesignProject::getId).collect(Collectors.toSet());
        Map<Integer, DesignerCompany> companies = DesignerCompany.findByDesigners(designerIds);
        Map<Integer, String> covers = ProjectImage.coverKeys(projectIds);
        return projects.stream().map(p -> new ProjectCard(
                p.getId(), p.getTitle(), p.getCategory().getId(), p.getCategory().getName(), p.getStatus(),
                p.getBudget(), p.getDesigner().getId(),
                displayName(p.getDesigner(), companies.get(p.getDesigner().getId())),
                Urls.file(covers.get(p.getId())), p.getCreatedAt())).toList();
    }

    /** Company name if the designer has one, otherwise the designer's full name. */
    public static String displayName(User designer, DesignerCompany company) {
        return company != null ? company.getCompanyName() : designer.getFullName();
    }
}

package com.prestige.renovation.control.project;

import java.util.List;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.project.DesignerProjectDetail;
import com.prestige.renovation.dto.project.ImageResponse;
import com.prestige.renovation.dto.project.ProjectRequest;
import com.prestige.renovation.entity.Category;
import com.prestige.renovation.entity.Favourite;
import com.prestige.renovation.entity.InteriorDesignProject;
import com.prestige.renovation.entity.ProjectImage;
import com.prestige.renovation.entity.ProjectView;

/** Shared rules for the designer's IDP user stories. */
public final class DesignerProjectSupport {

    private DesignerProjectSupport() {
    }

    /**
     * Ownership check (FR-IDP-2): another designer's IDP, or a deleted one, is reported as 404 so that
     * the existence of other designers' projects is not revealed. Applied consistently to every designer endpoint.
     */
    public static InteriorDesignProject requireOwned(Integer designerId, Integer projectId) {
        return InteriorDesignProject.findOwned(projectId, designerId)
                .orElseThrow(() -> ApiException.notFound("Project not found"));
    }

    /**
     * Resolves the category for a create/edit. Only active categories can be chosen (FR-PM-3); an IDP that
     * already uses a now-inactive category may keep it when edited.
     */
    static Category resolveCategory(Integer categoryId, Category current) {
        Category category = Category.findById(categoryId)
                .orElseThrow(() -> ApiException.badRequest("categoryId", "Please choose a valid category"));
        boolean keepingCurrent = current != null && current.getId().equals(category.getId());
        if (!category.isActive() && !keepingCurrent) {
            throw ApiException.badRequest("categoryId", "This category is no longer available. Please choose another.");
        }
        return category;
    }

    static void apply(InteriorDesignProject project, Category category, ProjectRequest r) {
        project.update(category, r.title().trim(), ControlSupport.clean(r.description()), r.status(), r.budget(),
                r.startDate(), r.endDate(), ControlSupport.clean(r.location()));
    }

    public static DesignerProjectDetail toDetail(InteriorDesignProject p) {
        List<ImageResponse> images = ProjectImage.findByProject(p.getId()).stream().map(ImageResponse::from).toList();
        return new DesignerProjectDetail(p.getId(), p.getTitle(), p.getDescription(), p.getCategory().getId(),
                p.getCategory().getName(), p.getCategory().isActive(), p.getStatus(), p.getBudget(),
                p.getStartDate(), p.getEndDate(), p.getLocation(), images,
                ProjectView.countForProject(p.getId()), Favourite.shortlistCount(p.getId()),
                p.getCreatedAt(), p.getUpdatedAt());
    }
}

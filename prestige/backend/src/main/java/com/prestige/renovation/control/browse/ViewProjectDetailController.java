package com.prestige.renovation.control.browse;

import java.time.Clock;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.control.common.DesignerInfoAssembler;
import com.prestige.renovation.dto.project.ImageResponse;
import com.prestige.renovation.dto.project.ProjectDetail;
import com.prestige.renovation.entity.Favourite;
import com.prestige.renovation.entity.InteriorDesignProject;
import com.prestige.renovation.entity.ProjectImage;
import com.prestige.renovation.entity.ProjectView;
import com.prestige.renovation.entity.Role;

/**
 * Control for user story: Visitor or customer views an IDP's detail page (FR-CUS-3).
 * For a logged-in customer the view is counted at most once per calendar day (FR-ANA-1) and the
 * response says whether the IDP is in their favourites.
 */
@Service
public class ViewProjectDetailController {

    private final Clock clock;

    public ViewProjectDetailController(Clock clock) {
        this.clock = clock;
    }

    @Transactional
    public ProjectDetail view(Integer projectId, AuthUser caller) {
        InteriorDesignProject p = InteriorDesignProject.findVisibleById(projectId)
                .orElseThrow(() -> ApiException.notFound("Project not found"));

        Boolean favourited = null;
        if (caller != null && caller.is(Role.CUSTOMER)) {
            LocalDate today = LocalDate.now(clock);
            ProjectView.record(p.getId(), caller.id(), today, clock.instant());
            favourited = Favourite.exists(caller.id(), p.getId());
        }

        return new ProjectDetail(p.getId(), p.getTitle(), p.getDescription(), p.getCategory().getId(),
                p.getCategory().getName(), p.getStatus(), p.getBudget(), p.getStartDate(), p.getEndDate(),
                p.getLocation(),
                ProjectImage.findByProject(p.getId()).stream().map(ImageResponse::from).toList(),
                p.getCreatedAt(), DesignerInfoAssembler.build(p.getDesigner()), favourited);
    }
}

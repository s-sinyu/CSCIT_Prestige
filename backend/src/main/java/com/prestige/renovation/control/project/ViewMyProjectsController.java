package com.prestige.renovation.control.project;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.dto.PageResponse;
import com.prestige.renovation.dto.Urls;
import com.prestige.renovation.dto.project.DesignerProjectSummary;
import com.prestige.renovation.entity.Favourite;
import com.prestige.renovation.entity.InteriorDesignProject;
import com.prestige.renovation.entity.ProjectImage;
import com.prestige.renovation.entity.ProjectView;

/** Control for user story: Interior Designer lists own IDPs with views and shortlists per IDP (FR-IDP-2, FR-ANA-3). */
@Service
public class ViewMyProjectsController {

    @Transactional(readOnly = true)
    public PageResponse<DesignerProjectSummary> list(Integer designerId, Pageable pageable) {
        Page<InteriorDesignProject> page = InteriorDesignProject.findOwnedByDesigner(designerId, pageable);
        List<Integer> ids = page.getContent().stream().map(InteriorDesignProject::getId).toList();
        Map<Integer, Long> views = ProjectView.countsForProjects(ids);
        Map<Integer, Long> shortlists = Favourite.shortlistCounts(ids);
        Map<Integer, String> covers = ProjectImage.coverKeys(ids);
        List<DesignerProjectSummary> rows = page.getContent().stream().map(p -> new DesignerProjectSummary(
                p.getId(), p.getTitle(), p.getCategory().getName(), p.getStatus(), p.getBudget(),
                Urls.file(covers.get(p.getId())), views.getOrDefault(p.getId(), 0L),
                shortlists.getOrDefault(p.getId(), 0L), p.getCreatedAt(), p.getUpdatedAt())).toList();
        return PageResponse.of(page, rows);
    }
}

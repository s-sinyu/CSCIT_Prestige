package com.prestige.renovation.control.browse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.control.common.ProjectCardAssembler;
import com.prestige.renovation.dto.PageResponse;
import com.prestige.renovation.dto.project.ProjectCard;
import com.prestige.renovation.entity.InteriorDesignProject;

/**
 * Control for user story: Visitor or customer searches and filters IDPs (FR-CUS-1, FR-CUS-2).
 * Only visible IDPs are returned (FR-CUS-6).
 */
@Service
public class SearchProjectsController {

    @Transactional(readOnly = true)
    public PageResponse<ProjectCard> search(InteriorDesignProject.SearchCriteria criteria, Pageable pageable) {
        if (criteria.budgetMin() != null && criteria.budgetMax() != null
                && criteria.budgetMin().compareTo(criteria.budgetMax()) > 0) {
            throw ApiException.badRequest("budgetMax", "Maximum budget must not be less than minimum budget");
        }
        Page<InteriorDesignProject> page = InteriorDesignProject.search(criteria, pageable);
        return PageResponse.of(page, ProjectCardAssembler.toCards(page.getContent()));
    }
}

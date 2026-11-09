package com.prestige.renovation.boundary.browse;

import java.math.BigDecimal;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.control.browse.SearchProjectsController;
import com.prestige.renovation.dto.PageResponse;
import com.prestige.renovation.dto.project.ProjectCard;
import com.prestige.renovation.entity.InteriorDesignProject;
import com.prestige.renovation.entity.ProjectStatus;

/** Boundary for user story: Visitor or customer searches and filters IDPs. */
@RestController
public class SearchProjectsBoundary {

    private final SearchProjectsController controller;

    public SearchProjectsBoundary(SearchProjectsController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/projects")
    public PageResponse<ProjectCard> search(@RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) Integer categoryId,
                                            @RequestParam(required = false) BigDecimal budgetMin,
                                            @RequestParam(required = false) BigDecimal budgetMax,
                                            @RequestParam(required = false) ProjectStatus status,
                                            @RequestParam(required = false) String designerName,
                                            @RequestParam(defaultValue = "newest") String sort,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "9") int size) {
        var criteria = new InteriorDesignProject.SearchCriteria(keyword, categoryId, budgetMin, budgetMax, status,
                designerName, sort);
        // Ordering is applied inside the search itself (budget sorts keep projects without a budget last)
        return controller.search(criteria, PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50)));
    }
}
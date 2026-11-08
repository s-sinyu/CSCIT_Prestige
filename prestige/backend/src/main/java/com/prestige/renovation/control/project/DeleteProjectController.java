package com.prestige.renovation.control.project;

import java.time.Clock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.entity.InteriorDesignProject;

/**
 * Control for user story: Interior Designer deletes one of own IDPs (FR-IDP-4).
 * Soft delete keeps the row so favourites, enquiries, ratings and historical report figures stay intact.
 */
@Service
public class DeleteProjectController {

    private final Clock clock;

    public DeleteProjectController(Clock clock) {
        this.clock = clock;
    }

    @Transactional
    public void delete(Integer designerId, Integer projectId) {
        InteriorDesignProject project = DesignerProjectSupport.requireOwned(designerId, projectId);
        project.softDelete(clock.instant());
        project.save();
    }
}

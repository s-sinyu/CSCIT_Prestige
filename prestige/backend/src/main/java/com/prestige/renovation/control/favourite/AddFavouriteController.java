package com.prestige.renovation.control.favourite;

import java.time.Clock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.entity.Favourite;
import com.prestige.renovation.entity.InteriorDesignProject;
import com.prestige.renovation.entity.Role;

/** Control for user story: Customer saves an IDP to the favourite list; adding twice keeps one (FR-CUS-4). */
@Service
public class AddFavouriteController {

    private final Clock clock;

    public AddFavouriteController(Clock clock) {
        this.clock = clock;
    }

    @Transactional
    public void add(Integer customerId, Integer projectId) {
        ControlSupport.requireRole(customerId, Role.CUSTOMER);
        InteriorDesignProject project = InteriorDesignProject.findVisibleById(projectId)
                .orElseThrow(() -> ApiException.notFound("Project not found"));
        Favourite.add(customerId, project.getId(), clock.instant());
    }
}

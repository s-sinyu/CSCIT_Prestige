package com.prestige.renovation.control.analytics;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.dto.designer.DashboardResponse;
import com.prestige.renovation.entity.Enquiry;
import com.prestige.renovation.entity.EnquiryStatus;
import com.prestige.renovation.entity.Favourite;
import com.prestige.renovation.entity.InteriorDesignProject;
import com.prestige.renovation.entity.ProjectView;

/** Control for user story: Interior Designer views dashboard totals of views, shortlists and enquiries (FR-ANA-3). */
@Service
public class ViewDesignerDashboardController {

    @Transactional(readOnly = true)
    public DashboardResponse view(Integer designerId) {
        return new DashboardResponse(
                InteriorDesignProject.countOwnedByDesigner(designerId),
                ProjectView.totalForDesigner(designerId),
                Favourite.shortlistTotalForDesigner(designerId),
                Enquiry.countForDesigner(designerId, EnquiryStatus.NEW),
                Enquiry.countForDesigner(designerId));
    }
}

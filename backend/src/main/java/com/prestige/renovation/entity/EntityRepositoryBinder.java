package com.prestige.renovation.entity;

import org.springframework.stereotype.Component;

import com.prestige.renovation.entity.repository.AvailabilityRepository;
import com.prestige.renovation.entity.repository.CategoryRepository;
import com.prestige.renovation.entity.repository.DesignerCompanyRepository;
import com.prestige.renovation.entity.repository.DesignerProfileRepository;
import com.prestige.renovation.entity.repository.EnquiryRepository;
import com.prestige.renovation.entity.repository.FavouriteRepository;
import com.prestige.renovation.entity.repository.IssueReportRepository;
import com.prestige.renovation.entity.repository.ProjectImageRepository;
import com.prestige.renovation.entity.repository.ProjectRepository;
import com.prestige.renovation.entity.repository.ProjectViewRepository;
import com.prestige.renovation.entity.repository.RatingRepository;
import com.prestige.renovation.entity.repository.StoredFileRepository;
import com.prestige.renovation.entity.repository.UserRepository;

/**
 * Wires each entity class to the Spring Data repository it uses for its data-access methods
 * (e.g. {@code Favourite.add(...)}, {@code User.findByEmail(...)}).
 *
 * In the BCE design the entity classes own both their data and their persistence operations;
 * Spring Data repositories are the technical mechanism behind those operations.
 */
@Component
public class EntityRepositoryBinder {

    public EntityRepositoryBinder(UserRepository users,
                                  DesignerProfileRepository profiles,
                                  DesignerCompanyRepository companies,
                                  CategoryRepository categories,
                                  ProjectRepository projects,
                                  ProjectImageRepository images,
                                  StoredFileRepository files,
                                  FavouriteRepository favourites,
                                  ProjectViewRepository views,
                                  EnquiryRepository enquiries,
                                  AvailabilityRepository availability,
                                  RatingRepository ratings,
                                  IssueReportRepository issueReports) {
        User.bind(users);
        DesignerProfile.bind(profiles);
        DesignerCompany.bind(companies);
        Category.bind(categories, projects);
        InteriorDesignProject.bind(projects);
        ProjectImage.bind(images);
        StoredFile.bind(files);
        Favourite.bind(favourites);
        ProjectView.bind(views);
        Enquiry.bind(enquiries);
        Availability.bind(availability);
        Rating.bind(ratings);
        IssueReport.bind(issueReports);
    }
}

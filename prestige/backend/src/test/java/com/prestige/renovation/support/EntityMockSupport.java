package com.prestige.renovation.support;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;

import com.prestige.renovation.entity.EntityRepositoryBinder;
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
 * Base class for unit tests of control classes: every entity's data-access methods are bound to a fresh
 * Mockito mock, so tests run without a database and can stub or verify persistence calls.
 */
public abstract class EntityMockSupport {

    protected UserRepository users = mock(UserRepository.class);
    protected DesignerProfileRepository profiles = mock(DesignerProfileRepository.class);
    protected DesignerCompanyRepository companies = mock(DesignerCompanyRepository.class);
    protected CategoryRepository categories = mock(CategoryRepository.class);
    protected ProjectRepository projects = mock(ProjectRepository.class);
    protected ProjectImageRepository images = mock(ProjectImageRepository.class);
    protected StoredFileRepository files = mock(StoredFileRepository.class);
    protected FavouriteRepository favourites = mock(FavouriteRepository.class);
    protected ProjectViewRepository views = mock(ProjectViewRepository.class);
    protected EnquiryRepository enquiries = mock(EnquiryRepository.class);
    protected AvailabilityRepository availability = mock(AvailabilityRepository.class);
    protected RatingRepository ratings = mock(RatingRepository.class);
    protected IssueReportRepository issueReports = mock(IssueReportRepository.class);

    @BeforeEach
    void bindEntityMocks() {
        new EntityRepositoryBinder(users, profiles, companies, categories, projects, images, files, favourites,
                views, enquiries, availability, ratings, issueReports);
    }
}

import { Route, Routes } from 'react-router-dom';
import AppLayout from '../components/AppLayout';
import ProtectedRoute from './ProtectedRoute';
import LoginPage from '../features/auth/LoginPage';
import RegisterPage from '../features/auth/RegisterPage';
import BrowsePage from '../features/public/BrowsePage';
import ProjectDetailPage from '../features/public/ProjectDetailPage';
import NotAuthorisedPage from '../features/public/NotAuthorisedPage';
import NotFoundPage from '../features/public/NotFoundPage';
import FavouritesPage from '../features/customer/FavouritesPage';
import DesignerAvailabilityPage from '../features/customer/DesignerAvailabilityPage';
import DashboardPage from '../features/designer/DashboardPage';
import MyProjectsPage from '../features/designer/MyProjectsPage';
import ProjectEditorPage from '../features/designer/ProjectEditorPage';
import CompanyPage from '../features/designer/CompanyPage';
import DesignerProfilePage from '../features/designer/DesignerProfilePage';
import AvailabilityPage from '../features/designer/AvailabilityPage';
import EnquiriesPage from '../features/designer/EnquiriesPage';
import EnquiryDetailPage from '../features/designer/EnquiryDetailPage';
import SubmitReportPage from '../features/issuereports/SubmitReportPage';
import MyReportsPage from '../features/issuereports/MyReportsPage';
import CategoriesPage from '../features/management/CategoriesPage';
import ReportsPage from '../features/management/ReportsPage';
import UsersPage from '../features/admin/UsersPage';
import AdminIssueReportsPage from '../features/admin/AdminIssueReportsPage';
import ProfilePage from '../features/profile/ProfilePage';

export default function AppRoutes() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        {/* Public */}
        <Route index element={<BrowsePage />} />
        <Route path="projects/:id" element={<ProjectDetailPage />} />
        <Route path="login" element={<LoginPage />} />
        <Route path="register" element={<RegisterPage />} />
        <Route path="not-authorised" element={<NotAuthorisedPage />} />

        {/* Any logged-in user */}
        <Route element={<ProtectedRoute />}>
          <Route path="profile" element={<ProfilePage />} />
        </Route>

        {/* Customer */}
        <Route element={<ProtectedRoute roles={['CUSTOMER']} />}>
          <Route path="customer/favourites" element={<FavouritesPage />} />
          <Route path="customer/designers/:id/availability" element={<DesignerAvailabilityPage />} />
        </Route>

        {/* Customer and designer */}
        <Route element={<ProtectedRoute roles={['CUSTOMER', 'INTERIOR_DESIGNER']} />}>
          <Route path="reports/new" element={<SubmitReportPage />} />
          <Route path="reports/mine" element={<MyReportsPage />} />
        </Route>

        {/* Interior designer */}
        <Route element={<ProtectedRoute roles={['INTERIOR_DESIGNER']} />}>
          <Route path="designer/dashboard" element={<DashboardPage />} />
          <Route path="designer/projects" element={<MyProjectsPage />} />
          <Route path="designer/projects/new" element={<ProjectEditorPage />} />
          <Route path="designer/projects/:id/edit" element={<ProjectEditorPage />} />
          <Route path="designer/company" element={<CompanyPage />} />
          <Route path="designer/profile" element={<DesignerProfilePage />} />
          <Route path="designer/availability" element={<AvailabilityPage />} />
          <Route path="designer/enquiries" element={<EnquiriesPage />} />
          <Route path="designer/enquiries/:id" element={<EnquiryDetailPage />} />
        </Route>

        {/* Platform management */}
        <Route element={<ProtectedRoute roles={['PLATFORM_MANAGEMENT']} />}>
          <Route path="management/reports" element={<ReportsPage />} />
          <Route path="management/categories" element={<CategoriesPage />} />
        </Route>

        {/* User admin */}
        <Route element={<ProtectedRoute roles={['USER_ADMIN']} />}>
          <Route path="admin/users" element={<UsersPage />} />
          <Route path="admin/issue-reports" element={<AdminIssueReportsPage />} />
        </Route>

        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
}

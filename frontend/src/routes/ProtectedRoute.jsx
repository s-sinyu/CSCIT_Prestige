import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * Role-based route guard (PRD 8.3): visitors go to the login page, logged-in users with the wrong role
 * go to the "not authorised" page. The backend enforces the same rules; this only improves navigation.
 */
export default function ProtectedRoute({ roles, children }) {
  const { isAuthenticated, role } = useAuth();
  const location = useLocation();
  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location.pathname + location.search }} />;
  }
  if (roles && !roles.includes(role)) {
    return <Navigate to="/not-authorised" replace />;
  }
  return children ?? <Outlet />;
}

import { screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import ProtectedRoute from '../routes/ProtectedRoute';
import { renderWithProviders } from './renderWithProviders';

const page = (
  <ProtectedRoute roles={['INTERIOR_DESIGNER']}>
    <div>Designer page</div>
  </ProtectedRoute>
);

describe('ProtectedRoute', () => {
  it('sends visitors to the login page', () => {
    renderWithProviders(page, { route: '/designer/projects' });
    expect(screen.getByText('Login page')).toBeInTheDocument();
  });

  it('sends users with the wrong role to the not-authorised page', () => {
    renderWithProviders(page, { role: 'CUSTOMER', route: '/designer/projects' });
    expect(screen.getByText('Not authorised page')).toBeInTheDocument();
  });

  it('shows the page to the right role', () => {
    renderWithProviders(page, { role: 'INTERIOR_DESIGNER', route: '/designer/projects' });
    expect(screen.getByText('Designer page')).toBeInTheDocument();
  });

  it('allows any logged-in user when no roles are given', () => {
    renderWithProviders(<ProtectedRoute><div>Profile</div></ProtectedRoute>, { role: 'USER_ADMIN' });
    expect(screen.getByText('Profile')).toBeInTheDocument();
  });
});

import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import LoginPage from '../features/auth/LoginPage';
import RegisterPage from '../features/auth/RegisterPage';
import { authApi } from '../api/authApi';
import { renderWithProviders } from './renderWithProviders';

vi.mock('../api/authApi', () => ({ authApi: { register: vi.fn(), login: vi.fn() } }));

describe('LoginPage', () => {
  it('validates required fields before calling the API', async () => {
    const login = vi.fn();
    renderWithProviders(<LoginPage />, { auth: { login } });
    await userEvent.click(screen.getByRole('button', { name: 'Log in' }));
    expect(screen.getByText('Email is required')).toBeInTheDocument();
    expect(screen.getByText('Password is required')).toBeInTheDocument();
    expect(login).not.toHaveBeenCalled();
  });

  it('shows the server error message when login fails', async () => {
    const login = vi.fn().mockRejectedValue({ response: { status: 401, data: { message: 'Invalid email or password' } } });
    renderWithProviders(<LoginPage />, { auth: { login } });
    await userEvent.type(screen.getByLabelText('Email'), 'ann@test.sg');
    await userEvent.type(screen.getByLabelText('Password'), 'wrong');
    await userEvent.click(screen.getByRole('button', { name: 'Log in' }));
    expect(await screen.findByText('Invalid email or password')).toBeInTheDocument();
    expect(login).toHaveBeenCalledWith('ann@test.sg', 'wrong');
  });
});

describe('RegisterPage', () => {
  beforeEach(() => vi.clearAllMocks());

  it('checks password rules and confirmation on the client', async () => {
    renderWithProviders(<RegisterPage />);
    await userEvent.type(screen.getByLabelText('Full name'), 'Ann');
    await userEvent.type(screen.getByLabelText('Email'), 'ann@test.sg');
    await userEvent.type(screen.getByLabelText('Password'), 'short');
    await userEvent.type(screen.getByLabelText('Confirm password'), 'other');
    await userEvent.click(screen.getByRole('button', { name: 'Create account' }));
    expect(screen.getByText(/at least 8 characters and contain a letter and a digit/)).toBeInTheDocument();
    expect(screen.getByText('Passwords do not match')).toBeInTheDocument();
    expect(authApi.register).not.toHaveBeenCalled();
  });

  it('shows field errors returned by the server', async () => {
    authApi.register.mockRejectedValue({ response: { status: 409, data: {
      message: 'An account with this email already exists', fieldErrors: { email: 'An account with this email already exists' } } } });
    renderWithProviders(<RegisterPage />);
    await userEvent.click(screen.getByLabelText('Interior Designer'));
    await userEvent.type(screen.getByLabelText('Full name'), 'Ann');
    await userEvent.type(screen.getByLabelText('Email'), 'ann@test.sg');
    await userEvent.type(screen.getByLabelText('Password'), 'Password1');
    await userEvent.type(screen.getByLabelText('Confirm password'), 'Password1');
    await userEvent.click(screen.getByRole('button', { name: 'Create account' }));
    await waitFor(() => expect(authApi.register).toHaveBeenCalledWith(expect.objectContaining({
      role: 'INTERIOR_DESIGNER', email: 'ann@test.sg', fullName: 'Ann', phone: null })));
    expect(await screen.findAllByText('An account with this email already exists')).not.toHaveLength(0);
  });
});

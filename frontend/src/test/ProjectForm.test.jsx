import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import ProjectForm, { toRequest, validateProject, EMPTY_PROJECT } from '../features/designer/ProjectForm';

const categories = [{ id: 1, name: 'Kitchen' }, { id: 2, name: 'Bathroom' }];

describe('ProjectForm', () => {
  it('create mode shows required-field errors and does not submit', async () => {
    const onSubmit = vi.fn();
    render(<ProjectForm categories={categories} onSubmit={onSubmit} />);
    await userEvent.click(screen.getByRole('button', { name: 'Create project' }));
    expect(screen.getByText('Title is required')).toBeInTheDocument();
    expect(screen.getByText('Category is required')).toBeInTheDocument();
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('edit mode is pre-filled and submits the request body', async () => {
    const onSubmit = vi.fn();
    render(<ProjectForm mode="edit" categories={categories} onSubmit={onSubmit}
      initial={{ ...EMPTY_PROJECT, title: 'Loft', categoryId: '2', status: 'COMPLETED', budget: '1200.50' }} />);
    expect(screen.getByLabelText(/Title/)).toHaveValue('Loft');
    await userEvent.click(screen.getByRole('button', { name: 'Save changes' }));
    expect(onSubmit).toHaveBeenCalledWith(expect.objectContaining({ title: 'Loft', categoryId: 2, status: 'COMPLETED', budget: 1200.5 }));
  });

  it('offers the current inactive category in edit mode', () => {
    render(<ProjectForm mode="edit" categories={categories} onSubmit={vi.fn()} currentCategory={{ id: 9, name: 'Retired' }}
      initial={{ ...EMPTY_PROJECT, title: 'Loft', categoryId: '9' }} />);
    expect(screen.getByText('Retired (no longer offered)')).toBeInTheDocument();
  });

  it('validates budget and dates like the server', () => {
    expect(validateProject({ ...EMPTY_PROJECT, title: 'T', categoryId: '1', budget: '-1' }).budget).toBeTruthy();
    expect(validateProject({ ...EMPTY_PROJECT, title: 'T', categoryId: '1', budget: '1.234' }).budget).toBeTruthy();
    expect(validateProject({ ...EMPTY_PROJECT, title: 'T', categoryId: '1', startDate: '2026-05-10', endDate: '2026-05-01' }).endDate)
      .toBe('End date must not be before start date');
    expect(validateProject({ ...EMPTY_PROJECT, title: 'T', categoryId: '1', budget: '100.5' })).toEqual({});
  });

  it('converts empty optional fields to null', () => {
    expect(toRequest({ ...EMPTY_PROJECT, title: ' T ', categoryId: '1' })).toEqual({
      title: 'T', description: null, categoryId: 1, status: 'CURRENT', budget: null, startDate: null, endDate: null, location: null,
    });
  });
});

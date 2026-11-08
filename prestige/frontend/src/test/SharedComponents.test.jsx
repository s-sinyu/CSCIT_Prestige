import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import ConfirmDialog from '../components/ConfirmDialog';
import MonthCalendar, { shiftMonth } from '../components/MonthCalendar';
import ReportView from '../features/management/ReportView';
import { checkImage } from '../components/ImageUploader';

describe('ConfirmDialog', () => {
  it('calls confirm or cancel', async () => {
    const onConfirm = vi.fn();
    const onCancel = vi.fn();
    render(<ConfirmDialog open title="Delete project?" message="Cannot be undone" onConfirm={onConfirm} onCancel={onCancel} />);
    expect(screen.getByText('Cannot be undone')).toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Delete' }));
    await userEvent.click(screen.getByRole('button', { name: 'Cancel' }));
    expect(onConfirm).toHaveBeenCalledTimes(1);
    expect(onCancel).toHaveBeenCalledTimes(1);
  });
});

describe('MonthCalendar', () => {
  const days = Array.from({ length: 30 }, (_, i) => ({
    date: `2026-11-${String(i + 1).padStart(2, '0')}`,
    status: i + 1 === 20 ? 'UNAVAILABLE' : 'AVAILABLE',
  }));

  it('shows unavailable dates and lets the designer toggle only today or later', async () => {
    const onToggle = vi.fn();
    render(<MonthCalendar month="2026-11" days={days} today="2026-11-10" onToggle={onToggle} onPrev={vi.fn()} onNext={vi.fn()} />);
    expect(screen.getByLabelText('2026-11-20 unavailable')).toHaveAttribute('data-status', 'UNAVAILABLE');
    await userEvent.click(screen.getByLabelText('2026-11-12 available'));
    expect(onToggle).toHaveBeenCalledWith({ date: '2026-11-12', status: 'AVAILABLE' });
    await userEvent.click(screen.getByLabelText('2026-11-05 available (past)'));
    expect(onToggle).toHaveBeenCalledTimes(1);
  });

  it('is read-only without onToggle', () => {
    render(<MonthCalendar month="2026-11" days={days} today="2026-11-10" onPrev={vi.fn()} onNext={vi.fn()} />);
    expect(screen.queryAllByRole('button', { name: /2026-11/ })).toHaveLength(0);
  });

  it('moves between months across years', () => {
    expect(shiftMonth('2026-12', 1)).toBe('2027-01');
    expect(shiftMonth('2026-01', -1)).toBe('2025-12');
  });
});

describe('ReportView', () => {
  it('renders every report section from API data', () => {
    const report = {
      idpsCreated: { total: 4, byCategory: [{ categoryId: 1, categoryName: 'Kitchen', active: true, count: 4 },
        { categoryId: 2, categoryName: 'Bathroom', active: false, count: 0 }] },
      engagement: { totalViews: 25, totalFavouritesSaved: 6, topViewed: [
        { projectId: 9, title: 'Loft', designerName: 'Kaze Studio', categoryName: 'Kitchen', views: 12, favouritesSaved: 2 }] },
      registrations: { byRole: { CUSTOMER: 7, INTERIOR_DESIGNER: 2, PLATFORM_MANAGEMENT: 0, USER_ADMIN: 0 }, total: 9 },
      activity: { enquiriesReceived: 3, issueReportsCreatedOpen: 2, issueReportsCreatedRectified: 1, issueReportsCurrentlyOpen: 5 },
    };
    render(<ReportView report={report} />);
    expect(screen.getByText('Bathroom (inactive)')).toBeInTheDocument();
    const top = screen.getByRole('table', { name: 'top viewed IDPs' });
    expect(within(top).getByText('Loft')).toBeInTheDocument();
    expect(within(top).getByText('Kaze Studio')).toBeInTheDocument();
    const roles = screen.getByRole('table', { name: 'registrations by role' });
    expect(within(roles).getByText('Customer')).toBeInTheDocument();
    expect(within(roles).getByText('9')).toBeInTheDocument();
    const activity = screen.getByRole('table', { name: 'enquiries and issue reports' });
    expect(within(activity).getByText('5')).toBeInTheDocument();
  });
});

describe('Image checks', () => {
  it('accepts JPEG/PNG/WebP up to 5 MB only', () => {
    expect(checkImage({ name: 'a.png', type: 'image/png', size: 1000 })).toBe('');
    expect(checkImage({ name: 'a.gif', type: 'image/gif', size: 1000 })).toMatch(/only JPEG, PNG or WebP/);
    expect(checkImage({ name: 'big.jpg', type: 'image/jpeg', size: 6 * 1024 * 1024 })).toMatch(/too large/);
  });
});

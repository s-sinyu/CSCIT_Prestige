import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import FavouriteButton from '../features/customer/FavouriteButton';
import EnquiryDialog from '../features/customer/EnquiryDialog';
import RatingFormDialog from '../features/customer/RatingFormDialog';
import { customerApi } from '../api/customerApi';

vi.mock('../api/customerApi', () => ({
  customerApi: {
    addFavourite: vi.fn(), removeFavourite: vi.fn(), sendEnquiry: vi.fn(), createRating: vi.fn(), updateRating: vi.fn(),
  },
}));

beforeEach(() => vi.clearAllMocks());

describe('FavouriteButton', () => {
  it('toggles between saved and not saved', async () => {
    customerApi.addFavourite.mockResolvedValue({});
    customerApi.removeFavourite.mockResolvedValue({});
    render(<FavouriteButton projectId={7} />);
    await userEvent.click(screen.getByRole('button', { name: 'Save to favourites' }));
    expect(customerApi.addFavourite).toHaveBeenCalledWith(7);
    const saved = await screen.findByRole('button', { name: 'Saved to favourites' });
    expect(saved).toHaveAttribute('aria-pressed', 'true');
    await userEvent.click(saved);
    expect(customerApi.removeFavourite).toHaveBeenCalledWith(7);
    expect(await screen.findByRole('button', { name: 'Save to favourites' })).toBeInTheDocument();
  });

  it('stays unchanged when the request fails', async () => {
    customerApi.addFavourite.mockRejectedValue({ response: { status: 404, data: { message: 'Project not found' } } });
    render(<FavouriteButton projectId={7} />);
    await userEvent.click(screen.getByRole('button', { name: 'Save to favourites' }));
    await waitFor(() => expect(screen.getByRole('button', { name: 'Save to favourites' })).toBeEnabled());
  });
});

describe('EnquiryDialog', () => {
  it('requires a message', async () => {
    render(<EnquiryDialog open projectId={3} projectTitle="Loft" onClose={vi.fn()} />);
    await userEvent.click(screen.getByRole('button', { name: 'Send enquiry' }));
    expect(screen.getByText('Message is required')).toBeInTheDocument();
    expect(customerApi.sendEnquiry).not.toHaveBeenCalled();
  });

  it('sends the trimmed message and closes', async () => {
    customerApi.sendEnquiry.mockResolvedValue({ id: 1 });
    const onClose = vi.fn();
    const onSent = vi.fn();
    render(<EnquiryDialog open projectId={3} projectTitle="Loft" onClose={onClose} onSent={onSent} />);
    await userEvent.type(screen.getByLabelText('Message'), '  Is May free?  ');
    await userEvent.click(screen.getByRole('button', { name: 'Send enquiry' }));
    await waitFor(() => expect(onClose).toHaveBeenCalled());
    expect(customerApi.sendEnquiry).toHaveBeenCalledWith(3, 'Is May free?');
    expect(onSent).toHaveBeenCalled();
  });
});

describe('RatingFormDialog', () => {
  it('creates a project rating when the switch is on', async () => {
    customerApi.createRating.mockResolvedValue({ id: 5 });
    const onSaved = vi.fn();
    render(<RatingFormDialog open designerId={2} designerName="Kaze" project={{ id: 9, title: 'Loft' }}
      existing={null} onClose={vi.fn()} onSaved={onSaved} />);
    await userEvent.click(screen.getByRole('checkbox', { name: /about the project "Loft"/ }));
    await userEvent.type(screen.getByLabelText('Comment (optional)'), 'Great work');
    await userEvent.click(screen.getByRole('button', { name: 'Save rating' }));
    await waitFor(() => expect(customerApi.createRating).toHaveBeenCalledWith({ designerId: 2, projectId: 9, score: 5, comment: 'Great work' }));
    expect(onSaved).toHaveBeenCalled();
  });

  it('edits an existing rating', async () => {
    customerApi.updateRating.mockResolvedValue({ id: 5 });
    render(<RatingFormDialog open designerId={2} designerName="Kaze" existing={{ id: 5, score: 3, comment: 'ok' }}
      onClose={vi.fn()} onSaved={vi.fn()} />);
    expect(screen.getByText('Edit your rating')).toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Save rating' }));
    await waitFor(() => expect(customerApi.updateRating).toHaveBeenCalledWith(5, { score: 3, comment: 'ok' }));
  });

  it('shows the conflict message for a duplicate rating', async () => {
    customerApi.createRating.mockRejectedValue({ response: { status: 409, data: { message: 'You have already rated this designer.' } } });
    render(<RatingFormDialog open designerId={2} designerName="Kaze" existing={null} onClose={vi.fn()} />);
    await userEvent.click(screen.getByRole('button', { name: 'Save rating' }));
    expect(await screen.findByText('You have already rated this designer.')).toBeInTheDocument();
  });
});

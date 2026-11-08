import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import SearchFilters from '../features/public/SearchFilters';
import PaginationBar from '../components/PaginationBar';

describe('SearchFilters', () => {
  it('submits the combined filters', async () => {
    const onSearch = vi.fn();
    render(<SearchFilters categories={[{ id: 3, name: 'Kitchen' }]} onSearch={onSearch} />);
    await userEvent.type(screen.getByLabelText('Search projects'), 'scandi');
    await userEvent.type(screen.getByLabelText('Min budget (SGD)'), '1000');
    await userEvent.type(screen.getByLabelText('Max budget (SGD)'), '5000');
    await userEvent.click(screen.getByRole('button', { name: 'Search' }));
    expect(onSearch).toHaveBeenCalledWith(expect.objectContaining({ keyword: 'scandi', budgetMin: '1000', budgetMax: '5000' }));
  });

  it('rejects a maximum budget below the minimum', async () => {
    const onSearch = vi.fn();
    render(<SearchFilters onSearch={onSearch} />);
    await userEvent.type(screen.getByLabelText('Min budget (SGD)'), '5000');
    await userEvent.type(screen.getByLabelText('Max budget (SGD)'), '100');
    await userEvent.click(screen.getByRole('button', { name: 'Search' }));
    expect(screen.getByText('Maximum budget must not be less than minimum budget')).toBeInTheDocument();
    expect(onSearch).not.toHaveBeenCalled();
  });

  it('clears the filters', async () => {
    const onSearch = vi.fn();
    render(<SearchFilters initial={{ keyword: 'old' }} onSearch={onSearch} />);
    await userEvent.click(screen.getByRole('button', { name: 'Clear filters' }));
    expect(onSearch).toHaveBeenCalledWith(expect.objectContaining({ keyword: '' }));
    expect(screen.getByLabelText('Search projects')).toHaveValue('');
  });
});

describe('PaginationBar', () => {
  it('converts the 1-based UI page to the 0-based API page', async () => {
    const onChange = vi.fn();
    render(<PaginationBar page={0} totalPages={3} totalElements={25} onChange={onChange} />);
    expect(screen.getByText('25 results')).toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Go to page 2' }));
    expect(onChange).toHaveBeenCalledWith(1);
  });

  it('shows only the count when there is a single page', () => {
    render(<PaginationBar page={0} totalPages={1} totalElements={1} onChange={vi.fn()} />);
    expect(screen.getByText('1 result')).toBeInTheDocument();
    expect(screen.queryByRole('navigation')).not.toBeInTheDocument();
  });
});

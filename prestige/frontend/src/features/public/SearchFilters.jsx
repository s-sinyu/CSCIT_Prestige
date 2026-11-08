import { useState } from 'react';
import { Box, Button, MenuItem, TextField } from '@mui/material';
import Grid from '@mui/material/Grid2';
import SearchIcon from '@mui/icons-material/Search';

export const EMPTY_FILTERS = { keyword: '', categoryId: '', status: '', budgetMin: '', budgetMax: '', designerName: '', sort: 'newest' };

/** Combinable search filters (FR-CUS-1). Calls onSearch(filters) when submitted. */
export default function SearchFilters({ categories = [], initial = EMPTY_FILTERS, onSearch }) {
  const [filters, setFilters] = useState({ ...EMPTY_FILTERS, ...initial });
  const [error, setError] = useState('');
  const set = (key) => (e) => setFilters({ ...filters, [key]: e.target.value });

  const submit = (event) => {
    event.preventDefault();
    const { budgetMin, budgetMax } = filters;
    if ((budgetMin !== '' && Number(budgetMin) < 0) || (budgetMax !== '' && Number(budgetMax) < 0)) {
      setError('Budget cannot be negative');
      return;
    }
    if (budgetMin !== '' && budgetMax !== '' && Number(budgetMin) > Number(budgetMax)) {
      setError('Maximum budget must not be less than minimum budget');
      return;
    }
    setError('');
    onSearch(filters);
  };

  const reset = () => {
    setFilters(EMPTY_FILTERS);
    setError('');
    onSearch(EMPTY_FILTERS);
  };

  return (
    <Box component="form" role="search" onSubmit={submit} noValidate sx={{ mb: 3 }}>
      <Grid container spacing={2}>
        <Grid size={{ xs: 12, md: 6 }}>
          <TextField fullWidth label="Search projects" placeholder="e.g. Scandinavian kitchen" value={filters.keyword} onChange={set('keyword')} />
        </Grid>
        <Grid size={{ xs: 12, md: 6 }}>
          <TextField fullWidth label="Designer or company" value={filters.designerName} onChange={set('designerName')} />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <TextField select fullWidth label="Category" value={filters.categoryId} onChange={set('categoryId')}>
            <MenuItem value="">All categories</MenuItem>
            {categories.map((c) => <MenuItem key={c.id} value={String(c.id)}>{c.name}</MenuItem>)}
          </TextField>
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 2 }}>
          <TextField select fullWidth label="Status" value={filters.status} onChange={set('status')}>
            <MenuItem value="">Any status</MenuItem>
            <MenuItem value="CURRENT">Current</MenuItem>
            <MenuItem value="COMPLETED">Completed</MenuItem>
          </TextField>
        </Grid>
        <Grid size={{ xs: 6, md: 2 }}>
          <TextField fullWidth type="number" label="Min budget (SGD)" value={filters.budgetMin} onChange={set('budgetMin')}
            slotProps={{ htmlInput: { min: 0 } }} />
        </Grid>
        <Grid size={{ xs: 6, md: 2 }}>
          <TextField fullWidth type="number" label="Max budget (SGD)" value={filters.budgetMax} onChange={set('budgetMax')}
            slotProps={{ htmlInput: { min: 0 } }} error={!!error} helperText={error} />
        </Grid>
        <Grid size={{ xs: 12, md: 3 }}>
          <TextField select fullWidth label="Sort by" value={filters.sort} onChange={set('sort')}>
            <MenuItem value="newest">Newest first</MenuItem>
            <MenuItem value="budgetAsc">Budget: low to high</MenuItem>
            <MenuItem value="budgetDesc">Budget: high to low</MenuItem>
          </TextField>
        </Grid>
      </Grid>
      <Box sx={{ display: 'flex', gap: 1, mt: 2 }}>
        <Button type="submit" variant="contained" startIcon={<SearchIcon />}>Search</Button>
        <Button onClick={reset}>Clear filters</Button>
      </Box>
    </Box>
  );
}

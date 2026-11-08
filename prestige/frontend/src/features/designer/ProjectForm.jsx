import { useState } from 'react';
import { Box, Button, MenuItem, TextField, InputAdornment } from '@mui/material';
import Grid from '@mui/material/Grid2';
import { compact, validateMax, validateRequired } from '../../utils/validation';

export const EMPTY_PROJECT = { title: '', description: '', categoryId: '', status: 'CURRENT', budget: '', startDate: '', endDate: '', location: '' };

/** Client-side checks mirroring the backend IDP rules (FR-IDP-1). */
export function validateProject(v) {
  const budget = v.budget === '' ? null : Number(v.budget);
  return compact({
    title: validateRequired(v.title, 'Title', 150),
    categoryId: v.categoryId === '' ? 'Category is required' : '',
    status: v.status ? '' : 'Status is required',
    budget: budget !== null && (Number.isNaN(budget) || budget < 0) ? 'Budget must be 0 or more'
      : budget !== null && !/^\d+(\.\d{1,2})?$/.test(String(v.budget)) ? 'Budget must have at most 2 decimal places' : '',
    endDate: v.startDate && v.endDate && v.endDate < v.startDate ? 'End date must not be before start date' : '',
    location: validateMax(v.location, 'Location', 150),
  });
}

/** Converts form values into the API request body. */
export function toRequest(v) {
  return {
    title: v.title.trim(),
    description: v.description.trim() || null,
    categoryId: Number(v.categoryId),
    status: v.status,
    budget: v.budget === '' ? null : Number(v.budget),
    startDate: v.startDate || null,
    endDate: v.endDate || null,
    location: v.location.trim() || null,
  };
}

/**
 * Create/edit form for an IDP. Inactive categories are not offered, except the project's current one
 * which it may keep (FR-PM-3).
 */
export default function ProjectForm({ mode = 'create', initial = EMPTY_PROJECT, categories = [], currentCategory,
  serverErrors = {}, busy = false, onSubmit, onCancel }) {
  const [values, setValues] = useState({ ...EMPTY_PROJECT, ...initial });
  const [errors, setErrors] = useState({});
  const set = (key) => (e) => setValues({ ...values, [key]: e.target.value });
  const shown = { ...serverErrors, endDate: serverErrors.endDate || serverErrors.endDateValid, ...errors };

  const options = [...categories];
  if (currentCategory && !options.some((c) => c.id === currentCategory.id)) {
    options.push({ ...currentCategory, name: `${currentCategory.name} (no longer offered)` });
  }

  const submit = (event) => {
    event.preventDefault();
    const found = validateProject(values);
    setErrors(found);
    if (Object.keys(found).length === 0) onSubmit(toRequest(values));
  };

  return (
    <Box component="form" noValidate onSubmit={submit} aria-label={mode === 'create' ? 'create project' : 'edit project'}>
      <Grid container spacing={2}>
        <Grid size={12}>
          <TextField label="Title" required fullWidth value={values.title} onChange={set('title')}
            error={!!shown.title} helperText={shown.title} slotProps={{ htmlInput: { maxLength: 150 } }} />
        </Grid>
        <Grid size={{ xs: 12, sm: 6 }}>
          <TextField select label="Category" required fullWidth value={values.categoryId} onChange={set('categoryId')}
            error={!!shown.categoryId} helperText={shown.categoryId}>
            {options.map((c) => <MenuItem key={c.id} value={String(c.id)}>{c.name}</MenuItem>)}
          </TextField>
        </Grid>
        <Grid size={{ xs: 12, sm: 6 }}>
          <TextField select label="Status" required fullWidth value={values.status} onChange={set('status')}
            error={!!shown.status} helperText={shown.status}>
            <MenuItem value="CURRENT">Current</MenuItem>
            <MenuItem value="COMPLETED">Completed</MenuItem>
          </TextField>
        </Grid>
        <Grid size={{ xs: 12, sm: 6 }}>
          <TextField label="Budget" type="number" fullWidth value={values.budget} onChange={set('budget')}
            error={!!shown.budget} helperText={shown.budget || 'Optional'}
            slotProps={{ htmlInput: { min: 0, step: '0.01' }, input: { startAdornment: <InputAdornment position="start">SGD</InputAdornment> } }} />
        </Grid>
        <Grid size={{ xs: 12, sm: 6 }}>
          <TextField label="Location" fullWidth value={values.location} onChange={set('location')}
            error={!!shown.location} helperText={shown.location || 'Optional, e.g. Tampines'} />
        </Grid>
        <Grid size={{ xs: 12, sm: 6 }}>
          <TextField label="Start date" type="date" fullWidth value={values.startDate} onChange={set('startDate')}
            slotProps={{ inputLabel: { shrink: true } }} />
        </Grid>
        <Grid size={{ xs: 12, sm: 6 }}>
          <TextField label="End date" type="date" fullWidth value={values.endDate} onChange={set('endDate')}
            error={!!shown.endDate} helperText={shown.endDate} slotProps={{ inputLabel: { shrink: true } }} />
        </Grid>
        <Grid size={12}>
          <TextField label="Description" multiline minRows={4} fullWidth value={values.description} onChange={set('description')} />
        </Grid>
      </Grid>
      <Box sx={{ display: 'flex', gap: 1, mt: 3 }}>
        <Button type="submit" variant="contained" disabled={busy}>
          {mode === 'create' ? 'Create project' : 'Save changes'}
        </Button>
        {onCancel && <Button onClick={onCancel} disabled={busy}>Cancel</Button>}
      </Box>
    </Box>
  );
}

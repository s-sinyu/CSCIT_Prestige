import { useEffect, useState } from 'react';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, FormControlLabel, Switch, TextField } from '@mui/material';
import { compact, validateMax, validateRequired } from '../../utils/validation';

/** Create or edit a category (FR-PM-1). */
export default function CategoryDialog({ open, category, serverErrors = {}, busy, onSave, onClose }) {
  const [values, setValues] = useState({ name: '', description: '', active: true });
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (open) {
      setValues({ name: category?.name ?? '', description: category?.description ?? '', active: category?.active ?? true });
      setErrors({});
    }
  }, [open, category]);

  const shown = { ...serverErrors, ...errors };
  const save = () => {
    const found = compact({ name: validateRequired(values.name, 'Name', 100), description: validateMax(values.description, 'Description', 500) });
    setErrors(found);
    if (!Object.keys(found).length) {
      onSave({ name: values.name.trim(), description: values.description.trim() || null, active: values.active });
    }
  };

  return (
    <Dialog open={open} onClose={busy ? undefined : onClose} fullWidth maxWidth="sm">
      <DialogTitle>{category ? 'Edit category' : 'New category'}</DialogTitle>
      <DialogContent>
        <TextField label="Name" fullWidth margin="normal" value={values.name} onChange={(e) => setValues({ ...values, name: e.target.value })}
          error={!!shown.name} helperText={shown.name} />
        <TextField label="Description (optional)" fullWidth margin="normal" multiline minRows={2} value={values.description}
          onChange={(e) => setValues({ ...values, description: e.target.value })} error={!!shown.description} helperText={shown.description} />
        <FormControlLabel control={<Switch checked={values.active} onChange={(e) => setValues({ ...values, active: e.target.checked })} />}
          label="Active (offered to designers and in search filters)" />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={busy}>Cancel</Button>
        <Button variant="contained" onClick={save} disabled={busy}>Save</Button>
      </DialogActions>
    </Dialog>
  );
}

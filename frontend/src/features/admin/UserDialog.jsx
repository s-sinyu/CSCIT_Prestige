import { useEffect, useState } from 'react';
import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, TextField } from '@mui/material';
import { compact, validateEmail, validatePassword, validatePhone, validateRequired } from '../../utils/validation';

/**
 * Create a Platform Management account (user = null) or edit an existing account's details (FR-ADM-1, FR-ADM-4).
 */
export default function UserDialog({ open, user, serverErrors = {}, busy, onSave, onClose }) {
  const creating = !user;
  const [values, setValues] = useState({ email: '', fullName: '', phone: '', password: '' });
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (open) {
      setValues({ email: user?.email ?? '', fullName: user?.fullName ?? '', phone: user?.phone ?? '', password: '' });
      setErrors({});
    }
  }, [open, user]);

  const shown = { ...serverErrors, ...errors };
  const set = (key) => (e) => setValues({ ...values, [key]: e.target.value });

  const save = () => {
    const found = compact({
      email: validateEmail(values.email),
      fullName: validateRequired(values.fullName, 'Full name', 150),
      phone: validatePhone(values.phone),
      password: creating ? validatePassword(values.password) : '',
    });
    setErrors(found);
    if (Object.keys(found).length) return;
    const body = { email: values.email.trim(), fullName: values.fullName.trim(), phone: values.phone.trim() || null };
    onSave(creating ? { ...body, password: values.password } : body);
  };

  return (
    <Dialog open={open} onClose={busy ? undefined : onClose} fullWidth maxWidth="sm">
      <DialogTitle>{creating ? 'New Platform Management account' : `Edit ${user.fullName}`}</DialogTitle>
      <DialogContent>
        {creating && <DialogContentText>Share the initial password with the new manager securely. They can change it after logging in.</DialogContentText>}
        <TextField label="Email" fullWidth margin="normal" value={values.email} onChange={set('email')} error={!!shown.email} helperText={shown.email} />
        <TextField label="Full name" fullWidth margin="normal" value={values.fullName} onChange={set('fullName')} error={!!shown.fullName} helperText={shown.fullName} />
        <TextField label="Phone (optional)" fullWidth margin="normal" value={values.phone} onChange={set('phone')} error={!!shown.phone} helperText={shown.phone} />
        {creating && (
          <TextField label="Initial password" type="password" fullWidth margin="normal" autoComplete="new-password" value={values.password}
            onChange={set('password')} error={!!shown.password} helperText={shown.password || 'At least 8 characters with a letter and a digit'} />
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={busy}>Cancel</Button>
        <Button variant="contained" onClick={save} disabled={busy}>{creating ? 'Create account' : 'Save'}</Button>
      </DialogActions>
    </Dialog>
  );
}

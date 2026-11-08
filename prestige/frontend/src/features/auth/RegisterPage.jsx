import { useState } from 'react';
import { Alert, Box, Button, FormControl, FormControlLabel, FormLabel, Paper, Radio, RadioGroup, TextField,
  Typography, Link } from '@mui/material';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { authApi } from '../../api/authApi';
import { errorMessage, fieldErrors } from '../../api/axiosClient';
import { HOME_BY_ROLE, useAuth } from '../../context/AuthContext';
import { useNotify } from '../../context/NotifyContext';
import { compact, validateEmail, validatePassword, validatePhone, validateRequired } from '../../utils/validation';

/** Visitors register as a Customer or an Interior Designer only (FR-AUTH-1). */
export default function RegisterPage() {
  const { login } = useAuth();
  const notify = useNotify();
  const navigate = useNavigate();
  const [form, setForm] = useState({ role: 'CUSTOMER', fullName: '', email: '', phone: '', password: '', confirm: '' });
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState('');
  const [busy, setBusy] = useState(false);
  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });

  const submit = async (event) => {
    event.preventDefault();
    const found = compact({
      fullName: validateRequired(form.fullName, 'Full name', 150),
      email: validateEmail(form.email),
      phone: validatePhone(form.phone),
      password: validatePassword(form.password),
      confirm: form.confirm === form.password ? '' : 'Passwords do not match',
    });
    setErrors(found);
    if (Object.keys(found).length) return;
    setBusy(true);
    setServerError('');
    try {
      await authApi.register({ role: form.role, fullName: form.fullName.trim(), email: form.email.trim(),
        phone: form.phone.trim() || null, password: form.password });
      const user = await login(form.email.trim(), form.password);
      notify('Welcome to Prestige!');
      navigate(HOME_BY_ROLE[user.role] || '/', { replace: true });
    } catch (error) {
      setErrors(fieldErrors(error));
      setServerError(errorMessage(error, 'Registration failed'));
    } finally {
      setBusy(false);
    }
  };

  return (
    <Box sx={{ display: 'flex', justifyContent: 'center' }}>
      <Paper variant="outlined" sx={{ p: { xs: 3, sm: 4 }, width: '100%', maxWidth: 480 }}>
        <Typography variant="h5" component="h1" gutterBottom>Create your account</Typography>
        {serverError && <Alert severity="error" sx={{ mb: 2 }}>{serverError}</Alert>}
        <Box component="form" noValidate onSubmit={submit}>
          <FormControl sx={{ mt: 1 }}>
            <FormLabel id="role-label">I am a</FormLabel>
            <RadioGroup row aria-labelledby="role-label" value={form.role} onChange={set('role')}>
              <FormControlLabel value="CUSTOMER" control={<Radio />} label="Customer" />
              <FormControlLabel value="INTERIOR_DESIGNER" control={<Radio />} label="Interior Designer" />
            </RadioGroup>
          </FormControl>
          <TextField label="Full name" fullWidth margin="normal" value={form.fullName} onChange={set('fullName')}
            error={!!errors.fullName} helperText={errors.fullName} autoComplete="name" />
          <TextField label="Email" type="email" fullWidth margin="normal" value={form.email} onChange={set('email')}
            error={!!errors.email} helperText={errors.email} autoComplete="email" />
          <TextField label="Phone (optional)" fullWidth margin="normal" value={form.phone} onChange={set('phone')}
            error={!!errors.phone} helperText={errors.phone} autoComplete="tel" />
          <TextField label="Password" type="password" fullWidth margin="normal" value={form.password}
            onChange={set('password')} error={!!errors.password} autoComplete="new-password"
            helperText={errors.password || 'At least 8 characters with a letter and a digit'} />
          <TextField label="Confirm password" type="password" fullWidth margin="normal" value={form.confirm}
            onChange={set('confirm')} error={!!errors.confirm} helperText={errors.confirm} autoComplete="new-password" />
          <Button type="submit" variant="contained" fullWidth size="large" sx={{ mt: 2 }} disabled={busy}>
            {busy ? 'Creating account...' : 'Create account'}
          </Button>
        </Box>
        <Typography variant="body2" sx={{ mt: 2 }}>
          Already registered? <Link component={RouterLink} to="/login">Log in</Link>
        </Typography>
      </Paper>
    </Box>
  );
}

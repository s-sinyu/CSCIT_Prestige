import { useState } from 'react';
import { Alert, Box, Button, Paper, TextField, Typography, Link } from '@mui/material';
import { Link as RouterLink, useLocation, useNavigate } from 'react-router-dom';
import { HOME_BY_ROLE, useAuth } from '../../context/AuthContext';
import { errorMessage } from '../../api/axiosClient';
import { compact, validateEmail } from '../../utils/validation';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ email: '', password: '' });
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState('');
  const [busy, setBusy] = useState(false);

  const submit = async (event) => {
    event.preventDefault();
    const found = compact({ email: validateEmail(form.email), password: form.password ? '' : 'Password is required' });
    setErrors(found);
    if (Object.keys(found).length) return;
    setBusy(true);
    setServerError('');
    try {
      const user = await login(form.email.trim(), form.password);
      navigate(location.state?.from || HOME_BY_ROLE[user.role] || '/', { replace: true });
    } catch (error) {
      setServerError(errorMessage(error, 'Login failed'));
    } finally {
      setBusy(false);
    }
  };

  return (
    <Box sx={{ display: 'flex', justifyContent: 'center' }}>
      <Paper variant="outlined" sx={{ p: { xs: 3, sm: 4 }, width: '100%', maxWidth: 420 }}>
        <Typography variant="h5" component="h1" gutterBottom>Log in</Typography>
        {location.state?.expired && <Alert severity="info" sx={{ mb: 2 }}>Your session has ended. Please log in again.</Alert>}
        {serverError && <Alert severity="error" sx={{ mb: 2 }}>{serverError}</Alert>}
        <Box component="form" noValidate onSubmit={submit}>
          <TextField label="Email" type="email" fullWidth margin="normal" autoComplete="email"
            value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })}
            error={!!errors.email} helperText={errors.email} />
          <TextField label="Password" type="password" fullWidth margin="normal" autoComplete="current-password"
            value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })}
            error={!!errors.password} helperText={errors.password} />
          <Button type="submit" variant="contained" fullWidth size="large" sx={{ mt: 2 }} disabled={busy}>
            {busy ? 'Logging in...' : 'Log in'}
          </Button>
        </Box>
        <Typography variant="body2" sx={{ mt: 2 }}>
          New here? <Link component={RouterLink} to="/register">Create an account</Link>
        </Typography>
      </Paper>
    </Box>
  );
}

import { useEffect, useState } from 'react';
import { Box, Button, Paper, TextField, Typography } from '@mui/material';
import Grid from '@mui/material/Grid2';
import { authApi } from '../../api/authApi';
import { errorMessage, fieldErrors } from '../../api/axiosClient';
import { useAuth } from '../../context/AuthContext';
import { useNotify } from '../../context/NotifyContext';
import PageHeader from '../../components/PageHeader';
import { ROLE_LABELS } from '../../utils/format';
import { compact, validatePassword, validatePhone, validateRequired } from '../../utils/validation';

/** Edit own name and phone, change password (FR-AUTH-5). Email and role cannot be changed here. */
export default function ProfilePage() {
  const { user, updateUser } = useAuth();
  const notify = useNotify();
  const [details, setDetails] = useState({ fullName: user?.fullName ?? '', phone: user?.phone ?? '' });
  const [detailErrors, setDetailErrors] = useState({});
  const [pw, setPw] = useState({ currentPassword: '', newPassword: '', confirm: '' });
  const [pwErrors, setPwErrors] = useState({});
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    authApi.me().then((me) => {
      updateUser(me);
      setDetails({ fullName: me.fullName ?? '', phone: me.phone ?? '' });
    }).catch(() => {});
  }, [updateUser]);

  const saveDetails = async (event) => {
    event.preventDefault();
    const found = compact({ fullName: validateRequired(details.fullName, 'Full name', 150), phone: validatePhone(details.phone) });
    setDetailErrors(found);
    if (Object.keys(found).length) return;
    setBusy(true);
    try {
      updateUser(await authApi.updateMe({ fullName: details.fullName.trim(), phone: details.phone.trim() || null }));
      notify('Profile updated');
    } catch (e) {
      setDetailErrors(fieldErrors(e));
      notify(errorMessage(e), 'error');
    } finally {
      setBusy(false);
    }
  };

  const changePassword = async (event) => {
    event.preventDefault();
    const found = compact({
      currentPassword: pw.currentPassword ? '' : 'Current password is required',
      newPassword: validatePassword(pw.newPassword),
      confirm: pw.confirm === pw.newPassword ? '' : 'Passwords do not match',
    });
    setPwErrors(found);
    if (Object.keys(found).length) return;
    setBusy(true);
    try {
      await authApi.changePassword(pw.currentPassword, pw.newPassword);
      setPw({ currentPassword: '', newPassword: '', confirm: '' });
      notify('Password changed');
    } catch (e) {
      setPwErrors(fieldErrors(e));
      notify(errorMessage(e), 'error');
    } finally {
      setBusy(false);
    }
  };

  return (
    <>
      <PageHeader title="My profile" subtitle={`${user?.email} - ${ROLE_LABELS[user?.role] ?? ''}`} />
      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 6 }}>
          <Paper variant="outlined" sx={{ p: 3 }}>
            <Typography variant="h6" component="h2">Details</Typography>
            <Box component="form" noValidate onSubmit={saveDetails}>
              <TextField label="Email" fullWidth margin="normal" value={user?.email ?? ''} disabled helperText="Contact the admin to change your email" />
              <TextField label="Full name" fullWidth margin="normal" value={details.fullName}
                onChange={(e) => setDetails({ ...details, fullName: e.target.value })}
                error={!!detailErrors.fullName} helperText={detailErrors.fullName} />
              <TextField label="Phone" fullWidth margin="normal" value={details.phone}
                onChange={(e) => setDetails({ ...details, phone: e.target.value })}
                error={!!detailErrors.phone} helperText={detailErrors.phone} />
              <Button type="submit" variant="contained" sx={{ mt: 2 }} disabled={busy}>Save details</Button>
            </Box>
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, md: 6 }}>
          <Paper variant="outlined" sx={{ p: 3 }}>
            <Typography variant="h6" component="h2">Change password</Typography>
            <Box component="form" noValidate onSubmit={changePassword}>
              {[['currentPassword', 'Current password', 'current-password'], ['newPassword', 'New password', 'new-password'],
                ['confirm', 'Confirm new password', 'new-password']].map(([key, label, auto]) => (
                <TextField key={key} label={label} type="password" fullWidth margin="normal" autoComplete={auto} value={pw[key]}
                  onChange={(e) => setPw({ ...pw, [key]: e.target.value })} error={!!pwErrors[key]} helperText={pwErrors[key]} />
              ))}
              <Button type="submit" variant="contained" sx={{ mt: 2 }} disabled={busy}>Change password</Button>
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </>
  );
}

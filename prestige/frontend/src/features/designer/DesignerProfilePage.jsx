import { useCallback, useEffect, useState } from 'react';
import { Box, Button, Paper, TextField } from '@mui/material';
import { designerApi } from '../../api/designerApi';
import { errorMessage, fieldErrors } from '../../api/axiosClient';
import { useNotify } from '../../context/NotifyContext';
import PageHeader from '../../components/PageHeader';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import { compact, validateMax } from '../../utils/validation';

/** Designer profile: bio, years of experience, specialisation (FR-CO-5). */
export default function DesignerProfilePage() {
  const notify = useNotify();
  const [values, setValues] = useState(null);
  const [errors, setErrors] = useState({});
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  const load = useCallback(async () => {
    setError('');
    try {
      const p = await designerApi.profile();
      setValues({ bio: p.bio ?? '', yearsOfExperience: p.yearsOfExperience ?? '', specialisation: p.specialisation ?? '' });
    } catch (e) {
      setError(errorMessage(e));
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const save = async (event) => {
    event.preventDefault();
    const years = values.yearsOfExperience === '' ? null : Number(values.yearsOfExperience);
    const found = compact({
      bio: validateMax(values.bio, 'Bio', 2000),
      specialisation: validateMax(values.specialisation, 'Specialisation', 150),
      yearsOfExperience: years !== null && (!Number.isInteger(years) || years < 0 || years > 80)
        ? 'Years of experience must be a whole number between 0 and 80' : '',
    });
    setErrors(found);
    if (Object.keys(found).length) return;
    setBusy(true);
    try {
      await designerApi.updateProfile({ bio: values.bio.trim() || null, yearsOfExperience: years,
        specialisation: values.specialisation.trim() || null });
      notify('Profile saved');
    } catch (e) {
      setErrors(fieldErrors(e));
      notify(errorMessage(e), 'error');
    } finally {
      setBusy(false);
    }
  };

  if (error) return <ErrorAlert message={error} onRetry={load} />;
  if (!values) return <Loading />;

  return (
    <>
      <PageHeader title="Designer profile" subtitle="Shown with your company information on your project pages" />
      <Paper variant="outlined" sx={{ p: 3, maxWidth: 720 }}>
        <Box component="form" noValidate onSubmit={save}>
          <TextField label="Specialisation" fullWidth margin="normal" value={values.specialisation}
            placeholder="e.g. Japandi HDB interiors" onChange={(e) => setValues({ ...values, specialisation: e.target.value })}
            error={!!errors.specialisation} helperText={errors.specialisation} />
          <TextField label="Years of experience" type="number" margin="normal" value={values.yearsOfExperience}
            onChange={(e) => setValues({ ...values, yearsOfExperience: e.target.value })}
            error={!!errors.yearsOfExperience} helperText={errors.yearsOfExperience}
            slotProps={{ htmlInput: { min: 0, max: 80, step: 1 } }} />
          <TextField label="Bio" multiline minRows={5} fullWidth margin="normal" value={values.bio}
            onChange={(e) => setValues({ ...values, bio: e.target.value })}
            error={!!errors.bio} helperText={errors.bio || `${values.bio.length}/2000`} />
          <Button type="submit" variant="contained" sx={{ mt: 2 }} disabled={busy}>Save profile</Button>
        </Box>
      </Paper>
    </>
  );
}

import { useCallback, useEffect, useState } from 'react';
import { Avatar, Box, Button, Paper, Stack, TextField, Typography } from '@mui/material';
import Grid from '@mui/material/Grid2';
import { designerApi } from '../../api/designerApi';
import { errorMessage, fieldErrors } from '../../api/axiosClient';
import { fileUrl } from '../../api/config';
import { useNotify } from '../../context/NotifyContext';
import PageHeader from '../../components/PageHeader';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import ConfirmDialog from '../../components/ConfirmDialog';
import ImageUploader from '../../components/ImageUploader';
import { compact, validateEmail, validateMax, validatePhone, validateRequired, validateUrl } from '../../utils/validation';

const EMPTY = { companyName: '', registrationNumber: '', description: '', address: '', contactEmail: '', contactPhone: '', websiteUrl: '' };
const FIELDS = [
  ['companyName', 'Company name', { required: true }],
  ['registrationNumber', 'Registration number (optional)'],
  ['contactEmail', 'Contact email (optional)'],
  ['contactPhone', 'Contact phone (optional)'],
  ['websiteUrl', 'Website (optional)', { placeholder: 'https://' }],
  ['address', 'Address (optional)'],
];

/** Designer company information: create, view, update, delete and logo (FR-CO-1, FR-CO-2). */
export default function CompanyPage() {
  const notify = useNotify();
  const [company, setCompany] = useState(undefined); // undefined = loading, null = none yet
  const [values, setValues] = useState(EMPTY);
  const [errors, setErrors] = useState({});
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  const [confirm, setConfirm] = useState(false);

  const fill = (c) => {
    setCompany(c);
    setValues(c ? Object.fromEntries(Object.keys(EMPTY).map((k) => [k, c[k] ?? ''])) : EMPTY);
  };

  const load = useCallback(async () => {
    setError('');
    try {
      fill(await designerApi.company());
    } catch (e) {
      if (e.response?.status === 404) fill(null);
      else setError(errorMessage(e));
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const save = async (event) => {
    event.preventDefault();
    const found = compact({
      companyName: validateRequired(values.companyName, 'Company name', 150),
      registrationNumber: validateMax(values.registrationNumber, 'Registration number', 50),
      contactEmail: values.contactEmail ? validateEmail(values.contactEmail) : '',
      contactPhone: validatePhone(values.contactPhone),
      websiteUrl: validateUrl(values.websiteUrl),
    });
    setErrors(found);
    if (Object.keys(found).length) return;
    setBusy(true);
    const body = Object.fromEntries(Object.entries(values).map(([k, v]) => [k, v.trim() || null]));
    try {
      fill(company ? await designerApi.updateCompany(body) : await designerApi.createCompany(body));
      notify('Company information saved');
    } catch (e) {
      setErrors(fieldErrors(e));
      notify(errorMessage(e, 'Could not save'), 'error');
    } finally {
      setBusy(false);
    }
  };

  const remove = async () => {
    setBusy(true);
    try {
      await designerApi.deleteCompany();
      fill(null);
      setConfirm(false);
      notify('Company information deleted');
    } catch (e) {
      notify(errorMessage(e), 'error');
    } finally {
      setBusy(false);
    }
  };

  const uploadLogo = async (file) => {
    try {
      fill(await designerApi.uploadLogo(file));
      notify('Logo updated');
    } catch (e) {
      notify(errorMessage(e, 'Upload failed'), 'error');
    }
  };

  if (error) return <ErrorAlert message={error} onRetry={load} />;
  if (company === undefined) return <Loading />;

  return (
    <>
      <PageHeader title="Company information"
        subtitle={company ? 'Shown on your project pages' : 'Optional: add your company so customers can find you by company name'} />
      <Paper variant="outlined" sx={{ p: 3 }}>
        {company && (
          <Stack direction="row" spacing={2} alignItems="center" sx={{ mb: 3 }}>
            <Avatar src={fileUrl(company.logoUrl) || undefined} sx={{ width: 72, height: 72 }} alt="Company logo">
              {company.companyName.charAt(0)}
            </Avatar>
            <ImageUploader onUpload={uploadLogo} label={company.logoUrl ? 'Replace logo' : 'Upload logo'} />
          </Stack>
        )}
        <Box component="form" noValidate onSubmit={save}>
          <Grid container spacing={2}>
            {FIELDS.map(([key, label, opts = {}]) => (
              <Grid key={key} size={{ xs: 12, sm: 6 }}>
                <TextField label={label} required={opts.required} placeholder={opts.placeholder} fullWidth
                  value={values[key]} onChange={(e) => setValues({ ...values, [key]: e.target.value })}
                  error={!!errors[key]} helperText={errors[key]} />
              </Grid>
            ))}
            <Grid size={12}>
              <TextField label="About the company (optional)" multiline minRows={4} fullWidth value={values.description}
                onChange={(e) => setValues({ ...values, description: e.target.value })}
                error={!!errors.description} helperText={errors.description} />
            </Grid>
          </Grid>
          <Stack direction="row" spacing={1} sx={{ mt: 3 }}>
            <Button type="submit" variant="contained" disabled={busy}>{company ? 'Save changes' : 'Create company'}</Button>
            {company && <Button color="error" onClick={() => setConfirm(true)} disabled={busy}>Delete company information</Button>}
          </Stack>
        </Box>
        {!company && <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>You can upload a logo after creating the company.</Typography>}
      </Paper>
      <ConfirmDialog open={confirm} title="Delete company information?"
        message="Your company details and logo will be removed. Your projects stay published under your own name."
        busy={busy} onConfirm={remove} onCancel={() => setConfirm(false)} />
    </>
  );
}

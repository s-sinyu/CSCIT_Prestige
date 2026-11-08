import { useState } from 'react';
import { Alert, Box, Button, FormControl, FormControlLabel, FormLabel, Paper, Radio, RadioGroup, TextField } from '@mui/material';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { issueReportApi } from '../../api/issueReportApi';
import { errorMessage, fieldErrors } from '../../api/axiosClient';
import { useNotify } from '../../context/NotifyContext';
import PageHeader from '../../components/PageHeader';
import { compact, validateRequired } from '../../utils/validation';

/** Report a problem with a project/designer or with the platform (FR-RPT-1). */
export default function SubmitReportPage() {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const notify = useNotify();
  const projectId = params.get('projectId');
  const designerId = params.get('designerId');
  const projectTitle = params.get('title');
  const [values, setValues] = useState({ type: projectId || designerId ? 'IDP_OR_DESIGNER' : 'PLATFORM_ISSUE', title: '', description: '' });
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState('');
  const [busy, setBusy] = useState(false);
  const hasTarget = !!(projectId || designerId);

  const submit = async (event) => {
    event.preventDefault();
    const found = compact({
      title: validateRequired(values.title, 'Title', 150),
      description: validateRequired(values.description, 'Description', 2000),
      type: values.type === 'IDP_OR_DESIGNER' && !hasTarget
        ? 'To report a project or designer, open its page and use "Report this project"' : '',
    });
    setErrors(found);
    if (Object.keys(found).length) return;
    setBusy(true);
    setServerError('');
    try {
      await issueReportApi.submit({
        type: values.type,
        targetProjectId: values.type === 'IDP_OR_DESIGNER' && projectId ? Number(projectId) : null,
        targetDesignerId: values.type === 'IDP_OR_DESIGNER' && designerId ? Number(designerId) : null,
        title: values.title.trim(),
        description: values.description.trim(),
      });
      notify('Report submitted. You can follow its status in My reports.');
      navigate('/reports/mine');
    } catch (e) {
      setErrors(fieldErrors(e));
      setServerError(errorMessage(e));
    } finally {
      setBusy(false);
    }
  };

  return (
    <>
      <PageHeader title="Report an issue" subtitle="Our admin team reviews every report" />
      <Paper variant="outlined" sx={{ p: 3, maxWidth: 720 }}>
        {serverError && <Alert severity="error" sx={{ mb: 2 }}>{serverError}</Alert>}
        <Box component="form" noValidate onSubmit={submit}>
          <FormControl error={!!errors.type}>
            <FormLabel id="type-label">What is the problem about?</FormLabel>
            <RadioGroup aria-labelledby="type-label" value={values.type} onChange={(e) => setValues({ ...values, type: e.target.value })}>
              <FormControlLabel value="IDP_OR_DESIGNER" control={<Radio />} disabled={!hasTarget}
                label={hasTarget ? `This project or designer${projectTitle ? `: ${projectTitle}` : ''}` : 'A project or designer (open its page to report it)'} />
              <FormControlLabel value="PLATFORM_ISSUE" control={<Radio />} label="The platform (a bug or general problem)" />
            </RadioGroup>
            {errors.type && <Alert severity="warning">{errors.type}</Alert>}
          </FormControl>
          <TextField label="Title" fullWidth margin="normal" value={values.title}
            onChange={(e) => setValues({ ...values, title: e.target.value })} error={!!errors.title} helperText={errors.title}
            slotProps={{ htmlInput: { maxLength: 150 } }} />
          <TextField label="Description" fullWidth margin="normal" multiline minRows={5} value={values.description}
            onChange={(e) => setValues({ ...values, description: e.target.value })} error={!!errors.description}
            helperText={errors.description || `${values.description.length}/2000`} slotProps={{ htmlInput: { maxLength: 2000 } }} />
          <Button type="submit" variant="contained" sx={{ mt: 2 }} disabled={busy}>Submit report</Button>
        </Box>
      </Paper>
    </>
  );
}

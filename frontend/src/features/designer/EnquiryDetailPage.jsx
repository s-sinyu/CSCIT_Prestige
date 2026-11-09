import { useCallback, useEffect, useState } from 'react';
import { Alert, Button, Divider, Link, Paper, Stack, Typography } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { designerApi } from '../../api/designerApi';
import { errorMessage } from '../../api/axiosClient';
import { useNotify } from '../../context/NotifyContext';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import StatusChip from '../../components/StatusChip';
import { formatDateTime } from '../../utils/format';

/** Enquiry detail with the customer's contact details; opening it marks it READ (FR-ENQ-2, FR-ENQ-3). */
export default function EnquiryDetailPage() {
  const { id } = useParams();
  const notify = useNotify();
  const [enquiry, setEnquiry] = useState(null);
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  const load = useCallback(async () => {
    setError('');
    try {
      setEnquiry(await designerApi.enquiry(id));
    } catch (e) {
      setError(e.response?.status === 404 ? 'Enquiry not found.' : errorMessage(e));
    }
  }, [id]);

  useEffect(() => { load(); }, [load]);

  const markReplied = async () => {
    setBusy(true);
    try {
      setEnquiry(await designerApi.setEnquiryStatus(id, 'REPLIED'));
      notify('Marked as replied');
    } catch (e) {
      notify(errorMessage(e), 'error');
    } finally {
      setBusy(false);
    }
  };

  if (error) return <ErrorAlert message={error} />;
  if (!enquiry) return <Loading />;

  return (
    <>
      <Button component={RouterLink} to="/designer/enquiries" startIcon={<ArrowBackIcon />} sx={{ mb: 2 }}>All enquiries</Button>
      <Paper variant="outlined" sx={{ p: 3, maxWidth: 760 }}>
        <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 1 }}>
          <Typography variant="h5" component="h1">Enquiry from {enquiry.customerName}</Typography>
          <StatusChip status={enquiry.status} size="medium" />
        </Stack>
        <Typography color="text.secondary">
          About{' '}
          {enquiry.projectRemoved ? `${enquiry.projectTitle} (removed project)` : (
            <Link component={RouterLink} to={`/projects/${enquiry.projectId}`}>{enquiry.projectTitle}</Link>
          )}{' '}- received {formatDateTime(enquiry.createdAt)}
        </Typography>
        <Divider sx={{ my: 2 }} />
        <Typography sx={{ whiteSpace: 'pre-line' }}>{enquiry.message}</Typography>
        <Divider sx={{ my: 2 }} />
        <Typography variant="subtitle2" gutterBottom>Contact the customer</Typography>
        <Typography>Email: <Link href={`mailto:${enquiry.customerEmail}`}>{enquiry.customerEmail}</Link></Typography>
        <Typography>Phone: {enquiry.customerPhone || 'not provided'}</Typography>
        {enquiry.status !== 'REPLIED' ? (
          <Button variant="contained" sx={{ mt: 3 }} onClick={markReplied} disabled={busy}>Mark as replied</Button>
        ) : (
          <Alert severity="success" sx={{ mt: 3 }}>Replied {formatDateTime(enquiry.repliedAt)}</Alert>
        )}
      </Paper>
    </>
  );
}

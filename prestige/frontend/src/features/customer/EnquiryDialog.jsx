import { useState } from 'react';
import { Alert, Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, TextField } from '@mui/material';
import { customerApi } from '../../api/customerApi';
import { errorMessage, fieldErrors } from '../../api/axiosClient';
import { validateRequired } from '../../utils/validation';

/** Sends an enquiry about an IDP; the designer sees the customer's name, email and phone (FR-CUS-5). */
export default function EnquiryDialog({ open, projectId, projectTitle, onClose, onSent }) {
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [serverError, setServerError] = useState('');
  const [busy, setBusy] = useState(false);

  const close = () => {
    setMessage('');
    setError('');
    setServerError('');
    onClose();
  };

  const send = async () => {
    const problem = validateRequired(message, 'Message', 2000);
    setError(problem);
    if (problem) return;
    setBusy(true);
    setServerError('');
    try {
      await customerApi.sendEnquiry(projectId, message.trim());
      setMessage('');
      onSent?.();
      onClose();
    } catch (e) {
      setError(fieldErrors(e).message || '');
      setServerError(errorMessage(e, 'Could not send the enquiry'));
    } finally {
      setBusy(false);
    }
  };

  return (
    <Dialog open={open} onClose={busy ? undefined : close} fullWidth maxWidth="sm">
      <DialogTitle>Send an enquiry</DialogTitle>
      <DialogContent>
        <DialogContentText sx={{ mb: 2 }}>
          About <strong>{projectTitle}</strong>. The designer will see your name, email and phone number and reply to you directly.
        </DialogContentText>
        {serverError && <Alert severity="error" sx={{ mb: 2 }}>{serverError}</Alert>}
        <TextField label="Message" multiline minRows={4} fullWidth value={message}
          onChange={(e) => setMessage(e.target.value)} error={!!error}
          helperText={error || `${message.length}/2000`} slotProps={{ htmlInput: { maxLength: 2000 } }} />
      </DialogContent>
      <DialogActions>
        <Button onClick={close} disabled={busy}>Cancel</Button>
        <Button variant="contained" onClick={send} disabled={busy}>{busy ? 'Sending...' : 'Send enquiry'}</Button>
      </DialogActions>
    </Dialog>
  );
}

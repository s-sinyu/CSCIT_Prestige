import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from '@mui/material';

/** Confirmation for destructive actions (PRD 8.3, FR-IDP-6). */
export default function ConfirmDialog({
  open, title = 'Are you sure?', message, confirmLabel = 'Delete', onConfirm, onCancel, busy = false,
}) {
  return (
    <Dialog open={open} onClose={busy ? undefined : onCancel} aria-labelledby="confirm-title">
      <DialogTitle id="confirm-title">{title}</DialogTitle>
      {message && (
        <DialogContent>
          <DialogContentText>{message}</DialogContentText>
        </DialogContent>
      )}
      <DialogActions>
        <Button onClick={onCancel} disabled={busy}>Cancel</Button>
        <Button onClick={onConfirm} color="error" variant="contained" disabled={busy}>{confirmLabel}</Button>
      </DialogActions>
    </Dialog>
  );
}

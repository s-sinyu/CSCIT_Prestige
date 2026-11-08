import { Alert, Button } from '@mui/material';

export default function ErrorAlert({ message, onRetry }) {
  if (!message) return null;
  return (
    <Alert severity="error" sx={{ my: 2 }} action={onRetry && <Button color="inherit" size="small" onClick={onRetry}>Retry</Button>}>
      {message}
    </Alert>
  );
}

import { Button } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import EmptyState from '../../components/EmptyState';

export default function NotAuthorisedPage() {
  return (
    <EmptyState
      title="Not authorised"
      message="Your account does not have access to this page."
      action={<Button component={RouterLink} to="/" variant="contained">Go to home</Button>}
    />
  );
}

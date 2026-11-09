import { Button } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import EmptyState from '../../components/EmptyState';

export default function NotFoundPage() {
  return (
    <EmptyState
      title="Page not found"
      message="The page you are looking for does not exist."
      action={<Button component={RouterLink} to="/" variant="contained">Browse projects</Button>}
    />
  );
}

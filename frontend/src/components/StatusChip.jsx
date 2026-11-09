import { Chip } from '@mui/material';
import { titleCase } from '../utils/format';

const COLORS = {
  ACTIVE: 'success', SUSPENDED: 'warning', DELETED: 'default',
  NEW: 'info', READ: 'default', REPLIED: 'success',
  OPEN: 'warning', RECTIFIED: 'success',
  CURRENT: 'info', COMPLETED: 'success',
  AVAILABLE: 'success', UNAVAILABLE: 'default',
};

export default function StatusChip({ status, size = 'small' }) {
  return <Chip size={size} label={titleCase(status)} color={COLORS[status] || 'default'} variant="outlined" />;
}

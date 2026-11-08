import { Box, Typography } from '@mui/material';
import InboxOutlinedIcon from '@mui/icons-material/InboxOutlined';

export default function EmptyState({ title = 'Nothing here yet', message, action }) {
  return (
    <Box sx={{ textAlign: 'center', py: 6, color: 'text.secondary' }}>
      <InboxOutlinedIcon sx={{ fontSize: 48, mb: 1 }} />
      <Typography variant="h6" color="text.primary">{title}</Typography>
      {message && <Typography sx={{ mt: 0.5 }}>{message}</Typography>}
      {action && <Box sx={{ mt: 2 }}>{action}</Box>}
    </Box>
  );
}

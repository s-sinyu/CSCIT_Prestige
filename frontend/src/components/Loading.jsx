import { Box, CircularProgress, Typography } from '@mui/material';

export default function Loading({ label = 'Loading...' }) {
  return (
    <Box role="status" sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 2, py: 6 }}>
      <CircularProgress size={28} />
      <Typography color="text.secondary">{label}</Typography>
    </Box>
  );
}

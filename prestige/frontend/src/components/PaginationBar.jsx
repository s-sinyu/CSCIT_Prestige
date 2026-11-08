import { Box, Pagination, Typography } from '@mui/material';

/** Page navigation for a PageResponse ({ page, totalPages, totalElements }). Pages are 0-based in the API. */
export default function PaginationBar({ page, totalPages, totalElements, onChange }) {
  if (!totalPages || totalPages <= 1) {
    return totalElements ? (
      <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
        {totalElements} result{totalElements === 1 ? '' : 's'}
      </Typography>
    ) : null;
  }
  return (
    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mt: 3, flexWrap: 'wrap', gap: 1 }}>
      <Typography variant="body2" color="text.secondary">
        {totalElements} result{totalElements === 1 ? '' : 's'}
      </Typography>
      <Pagination
        count={totalPages}
        page={page + 1}
        onChange={(_, p) => onChange(p - 1)}
        color="primary"
        aria-label="pagination"
      />
    </Box>
  );
}

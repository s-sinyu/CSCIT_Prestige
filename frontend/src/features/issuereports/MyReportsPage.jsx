import { useCallback, useEffect, useState } from 'react';
import { Button, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { issueReportApi } from '../../api/issueReportApi';
import { errorMessage } from '../../api/axiosClient';
import PageHeader from '../../components/PageHeader';
import PaginationBar from '../../components/PaginationBar';
import Loading from '../../components/Loading';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import StatusChip from '../../components/StatusChip';
import { formatDate } from '../../utils/format';

/** The reporter's own issue reports and their status (FR-RPT-3). */
export default function MyReportsPage() {
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setError('');
    try {
      setData(await issueReportApi.mine(page));
    } catch (e) {
      setError(errorMessage(e));
    }
  }, [page]);

  useEffect(() => { load(); }, [load]);

  return (
    <>
      <PageHeader title="My reports" subtitle="Problems you reported and whether they have been rectified"
        action={<Button variant="contained" component={RouterLink} to="/reports/new">Report a platform issue</Button>} />
      <ErrorAlert message={error} onRetry={load} />
      {!data && !error && <Loading />}
      {data && data.content.length === 0 && <EmptyState title="No reports" message="You have not reported any issues." />}
      {data && data.content.length > 0 && (
        <>
          <TableContainer component={Paper} variant="outlined">
            <Table aria-label="my reports">
              <TableHead>
                <TableRow>
                  <TableCell>Report</TableCell>
                  <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>About</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>Submitted</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {data.content.map((r) => (
                  <TableRow key={r.id}>
                    <TableCell>
                      <Typography fontWeight={600}>{r.title}</Typography>
                      <Typography variant="body2" color="text.secondary" sx={{ whiteSpace: 'pre-line' }}>{r.description}</Typography>
                    </TableCell>
                    <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>
                      {r.type === 'PLATFORM_ISSUE' ? 'Platform' : (r.targetProjectTitle || r.targetDesignerName)}
                    </TableCell>
                    <TableCell>
                      <StatusChip status={r.status} />
                      {r.rectifiedAt && <Typography variant="caption" display="block" color="text.secondary">on {formatDate(r.rectifiedAt)}</Typography>}
                    </TableCell>
                    <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>{formatDate(r.createdAt)}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          <PaginationBar page={page} totalPages={data.totalPages} totalElements={data.totalElements} onChange={setPage} />
        </>
      )}
    </>
  );
}

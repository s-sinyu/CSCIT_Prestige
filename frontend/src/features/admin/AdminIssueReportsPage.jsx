import { useCallback, useEffect, useState } from 'react';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Link, MenuItem, Paper, Stack, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, TextField, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { adminApi } from '../../api/adminApi';
import { errorMessage } from '../../api/axiosClient';
import { useNotify } from '../../context/NotifyContext';
import PageHeader from '../../components/PageHeader';
import PaginationBar from '../../components/PaginationBar';
import Loading from '../../components/Loading';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import StatusChip from '../../components/StatusChip';
import { ROLE_LABELS, formatDateTime } from '../../utils/format';

/** User Admin view of all issue reports with "Mark as rectified" (FR-RPT-4, FR-RPT-5). */
export default function AdminIssueReportsPage() {
  const notify = useNotify();
  const [status, setStatus] = useState('OPEN');
  const [type, setType] = useState('');
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const [error, setError] = useState('');
  const [selected, setSelected] = useState(null);
  const [busy, setBusy] = useState(false);

  const load = useCallback(async () => {
    setError('');
    try {
      setData(await adminApi.issueReports({ status, type, page, size: 20 }));
    } catch (e) {
      setError(errorMessage(e));
    }
  }, [status, type, page]);

  useEffect(() => { load(); }, [load]);

  const rectify = async () => {
    setBusy(true);
    try {
      setSelected(await adminApi.rectify(selected.id));
      notify('Report marked as rectified');
      load();
    } catch (e) {
      notify(errorMessage(e), 'error');
    } finally {
      setBusy(false);
    }
  };

  const target = (r) => {
    if (r.type === 'PLATFORM_ISSUE') return 'Platform';
    if (r.targetProjectId) return <Link component={RouterLink} to={`/projects/${r.targetProjectId}`}>{r.targetProjectTitle}</Link>;
    return r.targetDesignerName;
  };

  return (
    <>
      <PageHeader title="Issue reports" subtitle="Problems reported by customers and designers"
        action={(
          <Stack direction="row" spacing={1}>
            <TextField select size="small" label="Status" value={status} sx={{ minWidth: 140 }}
              onChange={(e) => { setPage(0); setStatus(e.target.value); }}>
              <MenuItem value="">All</MenuItem><MenuItem value="OPEN">Open</MenuItem><MenuItem value="RECTIFIED">Rectified</MenuItem>
            </TextField>
            <TextField select size="small" label="Type" value={type} sx={{ minWidth: 170 }}
              onChange={(e) => { setPage(0); setType(e.target.value); }}>
              <MenuItem value="">All types</MenuItem><MenuItem value="IDP_OR_DESIGNER">Project or designer</MenuItem>
              <MenuItem value="PLATFORM_ISSUE">Platform</MenuItem>
            </TextField>
          </Stack>
        )} />
      <ErrorAlert message={error} onRetry={load} />
      {!data && !error && <Loading />}
      {data && data.content.length === 0 && <EmptyState title="No reports" message="Nothing matches these filters." />}
      {data && data.content.length > 0 && (
        <>
          <TableContainer component={Paper} variant="outlined">
            <Table aria-label="issue reports">
              <TableHead>
                <TableRow>
                  <TableCell>Report</TableCell>
                  <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>Reporter</TableCell>
                  <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>About</TableCell>
                  <TableCell>Status</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {data.content.map((r) => (
                  <TableRow key={r.id} hover sx={{ cursor: 'pointer' }} onClick={() => setSelected(r)}>
                    <TableCell>
                      <Typography fontWeight={600}>{r.title}</Typography>
                      <Typography variant="caption" color="text.secondary">{formatDateTime(r.createdAt)}</Typography>
                    </TableCell>
                    <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>{r.reporterName}<br />
                      <Typography variant="caption" color="text.secondary">{ROLE_LABELS[r.reporterRole]}</Typography>
                    </TableCell>
                    <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }} onClick={(e) => e.stopPropagation()}>{target(r)}</TableCell>
                    <TableCell><StatusChip status={r.status} /></TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          <PaginationBar page={page} totalPages={data.totalPages} totalElements={data.totalElements} onChange={setPage} />
        </>
      )}
      <Dialog open={!!selected} onClose={() => setSelected(null)} fullWidth maxWidth="sm">
        {selected && (
          <>
            <DialogTitle>{selected.title}</DialogTitle>
            <DialogContent>
              <Stack spacing={1}>
                <StatusChip status={selected.status} />
                <Typography sx={{ whiteSpace: 'pre-line' }}>{selected.description}</Typography>
                <Typography variant="body2"><strong>About:</strong> {target(selected)}</Typography>
                <Typography variant="body2"><strong>Reporter:</strong> {selected.reporterName} ({selected.reporterEmail}, {ROLE_LABELS[selected.reporterRole]})</Typography>
                <Typography variant="body2"><strong>Submitted:</strong> {formatDateTime(selected.createdAt)}</Typography>
                {selected.rectifiedAt && (
                  <Typography variant="body2"><strong>Rectified:</strong> {formatDateTime(selected.rectifiedAt)} by {selected.rectifiedByName}</Typography>
                )}
              </Stack>
            </DialogContent>
            <DialogActions>
              <Button onClick={() => setSelected(null)}>Close</Button>
              {selected.status === 'OPEN' && <Button variant="contained" onClick={rectify} disabled={busy}>Mark as rectified</Button>}
            </DialogActions>
          </>
        )}
      </Dialog>
    </>
  );
}

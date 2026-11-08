import { useCallback, useEffect, useState } from 'react';
import { MenuItem, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography } from '@mui/material';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { designerApi } from '../../api/designerApi';
import { errorMessage } from '../../api/axiosClient';
import PageHeader from '../../components/PageHeader';
import PaginationBar from '../../components/PaginationBar';
import Loading from '../../components/Loading';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import StatusChip from '../../components/StatusChip';
import { formatDateTime } from '../../utils/format';

/** Enquiry inbox, newest first, filterable by status (FR-ENQ-1). */
export default function EnquiriesPage() {
  const navigate = useNavigate();
  const [params, setParams] = useSearchParams();
  const status = params.get('status') || '';
  const page = Number(params.get('page') || 0);
  const [data, setData] = useState(null);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setError('');
    try {
      setData(await designerApi.enquiries({ page, size: 20, ...(status ? { status } : {}) }));
    } catch (e) {
      setError(errorMessage(e));
    }
  }, [page, status]);

  useEffect(() => { load(); }, [load]);

  return (
    <>
      <PageHeader title="Enquiries" subtitle="Reply to customers by email or phone, then mark the enquiry as replied"
        action={(
          <TextField select size="small" label="Status" value={status} sx={{ minWidth: 160 }}
            onChange={(e) => setParams(e.target.value ? { status: e.target.value } : {})}>
            <MenuItem value="">All</MenuItem>
            <MenuItem value="NEW">New</MenuItem>
            <MenuItem value="READ">Read</MenuItem>
            <MenuItem value="REPLIED">Replied</MenuItem>
          </TextField>
        )} />
      <ErrorAlert message={error} onRetry={load} />
      {!data && !error && <Loading />}
      {data && data.content.length === 0 && <EmptyState title="No enquiries" message="Enquiries from customers will appear here." />}
      {data && data.content.length > 0 && (
        <>
          <TableContainer component={Paper} variant="outlined">
            <Table aria-label="enquiries">
              <TableHead>
                <TableRow>
                  <TableCell>Customer</TableCell>
                  <TableCell>Project</TableCell>
                  <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>Message</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>Received</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {data.content.map((e) => (
                  <TableRow key={e.id} hover sx={{ cursor: 'pointer' }} onClick={() => navigate(`/designer/enquiries/${e.id}`)}>
                    <TableCell><Typography fontWeight={e.status === 'NEW' ? 700 : 400}>{e.customerName}</Typography></TableCell>
                    <TableCell>{e.projectTitle}</TableCell>
                    <TableCell sx={{ display: { xs: 'none', md: 'table-cell' }, color: 'text.secondary' }}>{e.snippet}</TableCell>
                    <TableCell><StatusChip status={e.status} /></TableCell>
                    <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>{formatDateTime(e.createdAt)}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          <PaginationBar page={page} totalPages={data.totalPages} totalElements={data.totalElements}
            onChange={(p) => setParams({ ...(status ? { status } : {}), page: String(p) })} />
        </>
      )}
    </>
  );
}

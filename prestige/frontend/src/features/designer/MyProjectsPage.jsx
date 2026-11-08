import { useCallback, useEffect, useState } from 'react';
import { Avatar, Box, Button, IconButton, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Tooltip, Typography } from '@mui/material';
import EditIcon from '@mui/icons-material/EditOutlined';
import DeleteIcon from '@mui/icons-material/DeleteOutline';
import OpenIcon from '@mui/icons-material/OpenInNew';
import { Link as RouterLink } from 'react-router-dom';
import { designerApi } from '../../api/designerApi';
import { errorMessage } from '../../api/axiosClient';
import { fileUrl } from '../../api/config';
import { useNotify } from '../../context/NotifyContext';
import PageHeader from '../../components/PageHeader';
import PaginationBar from '../../components/PaginationBar';
import Loading from '../../components/Loading';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import ConfirmDialog from '../../components/ConfirmDialog';
import StatusChip from '../../components/StatusChip';
import { formatDate, formatMoney } from '../../utils/format';

/** The designer's own IDPs with views and shortlists per IDP (FR-IDP-2, FR-ANA-3). */
export default function MyProjectsPage() {
  const notify = useNotify();
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const [error, setError] = useState('');
  const [toDelete, setToDelete] = useState(null);
  const [busy, setBusy] = useState(false);

  const load = useCallback(async () => {
    setError('');
    try {
      setData(await designerApi.projects(page));
    } catch (e) {
      setError(errorMessage(e, 'Could not load your projects'));
    }
  }, [page]);

  useEffect(() => { load(); }, [load]);

  const remove = async () => {
    setBusy(true);
    try {
      await designerApi.deleteProject(toDelete.id);
      notify('Project deleted');
      setToDelete(null);
      load();
    } catch (e) {
      notify(errorMessage(e), 'error');
    } finally {
      setBusy(false);
    }
  };

  return (
    <>
      <PageHeader title="My projects" subtitle="Your portfolio of current and past projects"
        action={<Button variant="contained" component={RouterLink} to="/designer/projects/new">New project</Button>} />
      <ErrorAlert message={error} onRetry={load} />
      {!data && !error && <Loading />}
      {data && data.content.length === 0 && (
        <EmptyState title="No projects yet" message="Add your first project to start getting enquiries."
          action={<Button variant="contained" component={RouterLink} to="/designer/projects/new">Add a project</Button>} />
      )}
      {data && data.content.length > 0 && (
        <>
          <TableContainer component={Paper} variant="outlined">
            <Table aria-label="my projects">
              <TableHead>
                <TableRow>
                  <TableCell>Project</TableCell>
                  <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>Category</TableCell>
                  <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>Budget</TableCell>
                  <TableCell align="right">Views</TableCell>
                  <TableCell align="right">Shortlists</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {data.content.map((p) => (
                  <TableRow key={p.id} hover>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                        <Avatar variant="rounded" src={fileUrl(p.coverImageUrl) || undefined} alt="">{p.title.charAt(0)}</Avatar>
                        <Box>
                          <Typography fontWeight={600}>{p.title}</Typography>
                          <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
                            <StatusChip status={p.status} />
                            <Typography variant="caption" color="text.secondary">Added {formatDate(p.createdAt)}</Typography>
                          </Box>
                        </Box>
                      </Box>
                    </TableCell>
                    <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>{p.categoryName}</TableCell>
                    <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>{formatMoney(p.budget)}</TableCell>
                    <TableCell align="right">{p.views}</TableCell>
                    <TableCell align="right">{p.shortlists}</TableCell>
                    <TableCell align="right" sx={{ whiteSpace: 'nowrap' }}>
                      <Tooltip title="View public page">
                        <IconButton component={RouterLink} to={`/projects/${p.id}`} aria-label={`view ${p.title}`}><OpenIcon /></IconButton>
                      </Tooltip>
                      <Tooltip title="Edit">
                        <IconButton component={RouterLink} to={`/designer/projects/${p.id}/edit`} aria-label={`edit ${p.title}`}><EditIcon /></IconButton>
                      </Tooltip>
                      <Tooltip title="Delete">
                        <IconButton onClick={() => setToDelete(p)} aria-label={`delete ${p.title}`}><DeleteIcon /></IconButton>
                      </Tooltip>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          <PaginationBar page={page} totalPages={data.totalPages} totalElements={data.totalElements} onChange={setPage} />
        </>
      )}
      <ConfirmDialog open={!!toDelete} title={`Delete "${toDelete?.title}"?`}
        message="The project will be removed from your portfolio and from search. Enquiries you received about it stay in your inbox."
        busy={busy} onConfirm={remove} onCancel={() => setToDelete(null)} />
    </>
  );
}

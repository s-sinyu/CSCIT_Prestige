import { useCallback, useEffect, useState } from 'react';
import { Box, Button, Chip, FormControlLabel, IconButton, MenuItem, Paper, Stack, Switch, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, TextField, Tooltip, Typography } from '@mui/material';
import EditIcon from '@mui/icons-material/EditOutlined';
import DeleteIcon from '@mui/icons-material/DeleteOutline';
import BlockIcon from '@mui/icons-material/Block';
import RestoreIcon from '@mui/icons-material/SettingsBackupRestore';
import { adminApi } from '../../api/adminApi';
import { errorMessage, fieldErrors } from '../../api/axiosClient';
import { useAuth } from '../../context/AuthContext';
import { useNotify } from '../../context/NotifyContext';
import PageHeader from '../../components/PageHeader';
import PaginationBar from '../../components/PaginationBar';
import Loading from '../../components/Loading';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import ConfirmDialog from '../../components/ConfirmDialog';
import StatusChip from '../../components/StatusChip';
import UserDialog from './UserDialog';
import { ROLE_LABELS, formatDate } from '../../utils/format';

/** User Admin account management (FR-ADM-1 to 7). */
export default function UsersPage() {
  const { user: me } = useAuth();
  const notify = useNotify();
  const [filters, setFilters] = useState({ q: '', role: '', status: '', includeDeleted: false });
  const [applied, setApplied] = useState(filters);
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const [error, setError] = useState('');
  const [dialog, setDialog] = useState({ open: false, user: null });
  const [serverErrors, setServerErrors] = useState({});
  const [confirm, setConfirm] = useState(null); // { action, user }
  const [busy, setBusy] = useState(false);

  const load = useCallback(async () => {
    setError('');
    try {
      setData(await adminApi.users({ ...applied, includeDeleted: applied.includeDeleted || '', page, size: 20 }));
    } catch (e) {
      setError(errorMessage(e));
    }
  }, [applied, page]);

  useEffect(() => { load(); }, [load]);

  const search = (event) => {
    event.preventDefault();
    setPage(0);
    setApplied(filters);
  };

  const save = async (values) => {
    setBusy(true);
    setServerErrors({});
    try {
      if (dialog.user) await adminApi.updateUser(dialog.user.id, values);
      else await adminApi.createManager(values);
      notify(dialog.user ? 'Account updated' : 'Platform Management account created');
      setDialog({ open: false, user: null });
      load();
    } catch (e) {
      setServerErrors(fieldErrors(e));
      notify(errorMessage(e), 'error');
    } finally {
      setBusy(false);
    }
  };

  const runConfirmed = async () => {
    const { action, user } = confirm;
    setBusy(true);
    try {
      if (action === 'delete') await adminApi.deleteUser(user.id);
      else await adminApi.setStatus(user.id, action === 'suspend' ? 'SUSPENDED' : 'ACTIVE');
      notify(action === 'delete' ? 'Account deleted' : action === 'suspend' ? 'Account suspended' : 'Account reactivated');
      setConfirm(null);
      load();
    } catch (e) {
      notify(errorMessage(e), 'error');
    } finally {
      setBusy(false);
    }
  };

  const confirmText = {
    suspend: 'The user will be logged out and cannot log in. A suspended designer\'s projects, profile and ratings are hidden from the public.',
    reactivate: 'The user can log in again and their public content reappears.',
    delete: 'The account is disabled and hidden. Its data is kept and the email stays reserved. This cannot be undone from the app.',
  };

  return (
    <>
      <PageHeader title="Users" subtitle="Manage all accounts on the platform"
        action={<Button variant="contained" onClick={() => { setServerErrors({}); setDialog({ open: true, user: null }); }}>New Platform Management account</Button>} />
      <Paper variant="outlined" sx={{ p: 2, mb: 3 }}>
        <Box component="form" onSubmit={search} role="search">
          <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} alignItems={{ md: 'center' }}>
            <TextField size="small" label="Name or email" value={filters.q} onChange={(e) => setFilters({ ...filters, q: e.target.value })} sx={{ flexGrow: 1 }} />
            <TextField select size="small" label="Role" value={filters.role} onChange={(e) => setFilters({ ...filters, role: e.target.value })} sx={{ minWidth: 190 }}>
              <MenuItem value="">All roles</MenuItem>
              {Object.entries(ROLE_LABELS).map(([k, v]) => <MenuItem key={k} value={k}>{v}</MenuItem>)}
            </TextField>
            <TextField select size="small" label="Status" value={filters.status} onChange={(e) => setFilters({ ...filters, status: e.target.value })} sx={{ minWidth: 150 }}>
              <MenuItem value="">Any status</MenuItem>
              <MenuItem value="ACTIVE">Active</MenuItem>
              <MenuItem value="SUSPENDED">Suspended</MenuItem>
            </TextField>
            <FormControlLabel control={<Switch checked={filters.includeDeleted} onChange={(e) => setFilters({ ...filters, includeDeleted: e.target.checked })} />} label="Show deleted" />
            <Button type="submit" variant="contained">Search</Button>
          </Stack>
        </Box>
      </Paper>
      <ErrorAlert message={error} onRetry={load} />
      {!data && !error && <Loading />}
      {data && data.content.length === 0 && <EmptyState title="No accounts match" />}
      {data && data.content.length > 0 && (
        <>
          <TableContainer component={Paper} variant="outlined">
            <Table aria-label="users">
              <TableHead>
                <TableRow>
                  <TableCell>Name</TableCell>
                  <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>Role</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell sx={{ display: { xs: 'none', lg: 'table-cell' } }}>Joined</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {data.content.map((u) => {
                  const self = u.id === me?.id;
                  return (
                    <TableRow key={u.id} hover>
                      <TableCell>
                        <Typography fontWeight={600}>{u.fullName}{self ? ' (you)' : ''}</Typography>
                        <Typography variant="body2" color="text.secondary">{u.email}</Typography>
                      </TableCell>
                      <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>{ROLE_LABELS[u.role]}</TableCell>
                      <TableCell>{u.deleted ? <Chip size="small" label="Deleted" /> : <StatusChip status={u.status} />}</TableCell>
                      <TableCell sx={{ display: { xs: 'none', lg: 'table-cell' } }}>{formatDate(u.createdAt)}</TableCell>
                      <TableCell align="right" sx={{ whiteSpace: 'nowrap' }}>
                        <Tooltip title="Edit">
                          <IconButton aria-label={`edit ${u.email}`} onClick={() => { setServerErrors({}); setDialog({ open: true, user: u }); }}><EditIcon /></IconButton>
                        </Tooltip>
                        {!u.deleted && !self && (u.status === 'ACTIVE' ? (
                          <Tooltip title="Suspend">
                            <IconButton aria-label={`suspend ${u.email}`} onClick={() => setConfirm({ action: 'suspend', user: u })}><BlockIcon /></IconButton>
                          </Tooltip>
                        ) : (
                          <Tooltip title="Reactivate">
                            <IconButton aria-label={`reactivate ${u.email}`} onClick={() => setConfirm({ action: 'reactivate', user: u })}><RestoreIcon /></IconButton>
                          </Tooltip>
                        ))}
                        {!u.deleted && !self && (
                          <Tooltip title="Delete">
                            <IconButton aria-label={`delete ${u.email}`} onClick={() => setConfirm({ action: 'delete', user: u })}><DeleteIcon /></IconButton>
                          </Tooltip>
                        )}
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </TableContainer>
          <PaginationBar page={page} totalPages={data.totalPages} totalElements={data.totalElements} onChange={setPage} />
        </>
      )}
      <UserDialog open={dialog.open} user={dialog.user} serverErrors={serverErrors} busy={busy} onSave={save}
        onClose={() => setDialog({ open: false, user: null })} />
      <ConfirmDialog open={!!confirm} busy={busy}
        title={confirm ? `${confirm.action.charAt(0).toUpperCase()}${confirm.action.slice(1)} ${confirm.user.fullName}?` : ''}
        message={confirm ? confirmText[confirm.action] : ''}
        confirmLabel={confirm ? `${confirm.action.charAt(0).toUpperCase()}${confirm.action.slice(1)}` : ''}
        onConfirm={runConfirmed} onCancel={() => setConfirm(null)} />
    </>
  );
}

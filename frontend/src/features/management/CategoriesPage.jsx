import { useCallback, useEffect, useState } from 'react';
import { Alert, Button, IconButton, Paper, Switch, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip } from '@mui/material';
import EditIcon from '@mui/icons-material/EditOutlined';
import DeleteIcon from '@mui/icons-material/DeleteOutline';
import { managementApi } from '../../api/managementApi';
import { errorMessage, fieldErrors } from '../../api/axiosClient';
import { useNotify } from '../../context/NotifyContext';
import PageHeader from '../../components/PageHeader';
import PaginationBar from '../../components/PaginationBar';
import Loading from '../../components/Loading';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import ConfirmDialog from '../../components/ConfirmDialog';
import CategoryDialog from './CategoryDialog';

/** Category management (FR-PM-1 to 4). Categories in use cannot be deleted; deactivate them instead. */
export default function CategoriesPage() {
  const notify = useNotify();
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const [error, setError] = useState('');
  const [dialog, setDialog] = useState({ open: false, category: null });
  const [serverErrors, setServerErrors] = useState({});
  const [toDelete, setToDelete] = useState(null);
  const [inUse, setInUse] = useState(null);
  const [busy, setBusy] = useState(false);

  const load = useCallback(async () => {
    setError('');
    try {
      setData(await managementApi.categories(page));
    } catch (e) {
      setError(errorMessage(e));
    }
  }, [page]);

  useEffect(() => { load(); }, [load]);

  const save = async (values) => {
    setBusy(true);
    setServerErrors({});
    try {
      if (dialog.category) await managementApi.updateCategory(dialog.category.id, values);
      else await managementApi.createCategory(values);
      notify('Category saved');
      setDialog({ open: false, category: null });
      load();
    } catch (e) {
      setServerErrors(fieldErrors(e));
      notify(errorMessage(e), 'error');
    } finally {
      setBusy(false);
    }
  };

  const setActive = async (category, active) => {
    try {
      await managementApi.setCategoryActive(category.id, active);
      notify(active ? 'Category activated' : 'Category deactivated');
      load();
    } catch (e) {
      notify(errorMessage(e), 'error');
    }
  };

  const remove = async () => {
    setBusy(true);
    try {
      await managementApi.deleteCategory(toDelete.id);
      notify('Category deleted');
      setToDelete(null);
      load();
    } catch (e) {
      if (e.response?.status === 409) {
        setInUse({ category: toDelete, message: errorMessage(e) });
        setToDelete(null);
      } else {
        notify(errorMessage(e), 'error');
      }
    } finally {
      setBusy(false);
    }
  };

  return (
    <>
      <PageHeader title="Categories" subtitle="Inactive categories are hidden from designers and search filters"
        action={<Button variant="contained" onClick={() => { setServerErrors({}); setDialog({ open: true, category: null }); }}>New category</Button>} />
      {inUse && (
        <Alert severity="warning" sx={{ mb: 2 }} onClose={() => setInUse(null)}
          action={inUse.category.active && (
            <Button color="inherit" size="small" onClick={() => { setActive(inUse.category, false); setInUse(null); }}>Deactivate instead</Button>
          )}>
          {inUse.message}
        </Alert>
      )}
      <ErrorAlert message={error} onRetry={load} />
      {!data && !error && <Loading />}
      {data && data.content.length === 0 && <EmptyState title="No categories" message="Create the first category." />}
      {data && data.content.length > 0 && (
        <>
          <TableContainer component={Paper} variant="outlined">
            <Table aria-label="categories">
              <TableHead>
                <TableRow>
                  <TableCell>Name</TableCell>
                  <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>Description</TableCell>
                  <TableCell>Active</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {data.content.map((c) => (
                  <TableRow key={c.id} hover>
                    <TableCell>{c.name}</TableCell>
                    <TableCell sx={{ display: { xs: 'none', md: 'table-cell' }, color: 'text.secondary' }}>{c.description}</TableCell>
                    <TableCell>
                      <Switch checked={c.active} onChange={(e) => setActive(c, e.target.checked)}
                        inputProps={{ 'aria-label': `${c.name} active` }} />
                    </TableCell>
                    <TableCell align="right" sx={{ whiteSpace: 'nowrap' }}>
                      <Tooltip title="Edit">
                        <IconButton aria-label={`edit ${c.name}`} onClick={() => { setServerErrors({}); setDialog({ open: true, category: c }); }}><EditIcon /></IconButton>
                      </Tooltip>
                      <Tooltip title="Delete">
                        <IconButton aria-label={`delete ${c.name}`} onClick={() => setToDelete(c)}><DeleteIcon /></IconButton>
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
      <CategoryDialog open={dialog.open} category={dialog.category} serverErrors={serverErrors} busy={busy}
        onSave={save} onClose={() => setDialog({ open: false, category: null })} />
      <ConfirmDialog open={!!toDelete} title={`Delete "${toDelete?.name}"?`}
        message="Only categories that no project has ever used can be deleted." busy={busy}
        onConfirm={remove} onCancel={() => setToDelete(null)} />
    </>
  );
}

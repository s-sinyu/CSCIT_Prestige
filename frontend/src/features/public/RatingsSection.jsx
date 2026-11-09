import { useCallback, useEffect, useState } from 'react';
import { Box, Button, Divider, FormControlLabel, IconButton, List, ListItem, Rating, Switch, Stack, Tooltip,
  Typography } from '@mui/material';
import DeleteIcon from '@mui/icons-material/DeleteOutline';
import EditIcon from '@mui/icons-material/EditOutlined';
import { publicApi } from '../../api/publicApi';
import { customerApi } from '../../api/customerApi';
import { adminApi } from '../../api/adminApi';
import { errorMessage } from '../../api/axiosClient';
import { useAuth } from '../../context/AuthContext';
import { useNotify } from '../../context/NotifyContext';
import ConfirmDialog from '../../components/ConfirmDialog';
import PaginationBar from '../../components/PaginationBar';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import RatingFormDialog from '../customer/RatingFormDialog';
import { formatDate } from '../../utils/format';

/**
 * A designer's average rating and ratings list (FR-RT-4). Customers can rate, edit and delete their own
 * ratings; the User Admin can delete any rating (FR-RT-5).
 */
export default function RatingsSection({ designer, project, onSummaryChange }) {
  const { role } = useAuth();
  const notify = useNotify();
  const [data, setData] = useState(null);
  const [page, setPage] = useState(0);
  const [onlyThisProject, setOnlyThisProject] = useState(false);
  const [error, setError] = useState('');
  const [editing, setEditing] = useState(null);
  const [formOpen, setFormOpen] = useState(false);
  const [toDelete, setToDelete] = useState(null);
  const [busy, setBusy] = useState(false);

  const load = useCallback(async () => {
    setError('');
    try {
      const result = await publicApi.designerRatings(designer.id,
        { page, size: 5, projectId: onlyThisProject ? project?.id : '' });
      setData(result);
      onSummaryChange?.({ average: result.average, count: result.count });
    } catch (e) {
      setError(errorMessage(e, 'Could not load ratings'));
    }
  }, [designer.id, page, onlyThisProject, project?.id, onSummaryChange]);

  useEffect(() => { load(); }, [load]);

  const remove = async () => {
    setBusy(true);
    try {
      if (role === 'USER_ADMIN') await adminApi.deleteRating(toDelete.id);
      else await customerApi.deleteRating(toDelete.id);
      notify('Rating deleted');
      setToDelete(null);
      load();
    } catch (e) {
      notify(errorMessage(e), 'error');
    } finally {
      setBusy(false);
    }
  };

  return (
    <Box component="section" aria-labelledby="ratings-heading">
      <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ sm: 'center' }} gap={1}>
        <Box>
          <Typography id="ratings-heading" variant="h6" component="h2">Ratings</Typography>
          {data && (
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <Rating value={data.average ? Number(data.average) : 0} precision={0.1} readOnly />
              <Typography>
                {data.count ? `${Number(data.average).toFixed(1)} from ${data.count} rating${data.count === 1 ? '' : 's'}` : 'No ratings yet'}
              </Typography>
            </Box>
          )}
        </Box>
        <Stack direction="row" gap={1} alignItems="center">
          {project && (
            <FormControlLabel control={<Switch checked={onlyThisProject}
              onChange={(e) => { setOnlyThisProject(e.target.checked); setPage(0); }} />} label="This project only" />
          )}
          {role === 'CUSTOMER' && (
            <Button variant="outlined" onClick={() => { setEditing(null); setFormOpen(true); }}>Rate designer</Button>
          )}
        </Stack>
      </Stack>
      <ErrorAlert message={error} onRetry={load} />
      {!data && !error && <Loading label="Loading ratings..." />}
      {data && data.ratings.content.length > 0 && (
        <List>
          {data.ratings.content.map((r, i) => (
            <Box key={r.id}>
              {i > 0 && <Divider component="li" />}
              <ListItem alignItems="flex-start" sx={{ px: 0 }}
                secondaryAction={(r.mine || role === 'USER_ADMIN') && (
                  <Box>
                    {r.mine && (
                      <Tooltip title="Edit">
                        <IconButton aria-label="edit rating" onClick={() => { setEditing(r); setFormOpen(true); }}><EditIcon /></IconButton>
                      </Tooltip>
                    )}
                    <Tooltip title="Delete">
                      <IconButton aria-label="delete rating" onClick={() => setToDelete(r)}><DeleteIcon /></IconButton>
                    </Tooltip>
                  </Box>
                )}>
                <Box sx={{ pr: 10 }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, flexWrap: 'wrap' }}>
                    <Rating value={r.score} readOnly size="small" />
                    <Typography variant="subtitle2">{r.customerName}</Typography>
                    <Typography variant="caption" color="text.secondary">{formatDate(r.createdAt)}</Typography>
                  </Box>
                  {r.projectTitle && (
                    <Typography variant="caption" color="text.secondary">
                      For project: {r.projectTitle}{r.projectRemoved ? ' (removed project)' : ''}
                    </Typography>
                  )}
                  {r.comment && <Typography variant="body2" sx={{ mt: 0.5, whiteSpace: 'pre-line' }}>{r.comment}</Typography>}
                </Box>
              </ListItem>
            </Box>
          ))}
        </List>
      )}
      {data && (
        <PaginationBar page={data.ratings.page} totalPages={data.ratings.totalPages}
          totalElements={data.ratings.totalElements} onChange={setPage} />
      )}
      <RatingFormDialog open={formOpen} designerId={designer.id} designerName={designer.displayName} project={project}
        existing={editing} onClose={() => setFormOpen(false)}
        onSaved={() => { notify(editing ? 'Rating updated' : 'Thanks for your rating'); load(); }} />
      <ConfirmDialog open={!!toDelete} title="Delete this rating?" message="This cannot be undone." busy={busy}
        onConfirm={remove} onCancel={() => setToDelete(null)} />
    </Box>
  );
}

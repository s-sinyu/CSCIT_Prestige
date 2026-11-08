import { useCallback, useEffect, useState } from 'react';
import { Button } from '@mui/material';
import Grid from '@mui/material/Grid2';
import { Link as RouterLink } from 'react-router-dom';
import { customerApi } from '../../api/customerApi';
import { errorMessage } from '../../api/axiosClient';
import { useNotify } from '../../context/NotifyContext';
import PageHeader from '../../components/PageHeader';
import ProjectCard from '../../components/ProjectCard';
import PaginationBar from '../../components/PaginationBar';
import Loading from '../../components/Loading';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';

/** Customer's favourite list, newest first (FR-CUS-4). */
export default function FavouritesPage() {
  const notify = useNotify();
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setError('');
    try {
      setData(await customerApi.favourites(page));
    } catch (e) {
      setError(errorMessage(e, 'Could not load favourites'));
    }
  }, [page]);

  useEffect(() => { load(); }, [load]);

  const remove = async (id) => {
    try {
      await customerApi.removeFavourite(id);
      notify('Removed from favourites');
      if (data.content.length === 1 && page > 0) setPage(page - 1);
      else load();
    } catch (e) {
      notify(errorMessage(e), 'error');
    }
  };

  return (
    <>
      <PageHeader title="My favourites" subtitle="Projects you saved for reference" />
      <ErrorAlert message={error} onRetry={load} />
      {!data && !error && <Loading />}
      {data && data.content.length === 0 && (
        <EmptyState title="No favourites yet" message="Save projects you like while browsing."
          action={<Button component={RouterLink} to="/" variant="contained">Browse projects</Button>} />
      )}
      {data && data.content.length > 0 && (
        <>
          <Grid container spacing={3}>
            {data.content.map((p) => (
              <Grid key={p.id} size={{ xs: 12, sm: 6, md: 4 }}>
                <ProjectCard project={p} action={<Button size="small" color="error" onClick={() => remove(p.id)}>Remove</Button>} />
              </Grid>
            ))}
          </Grid>
          <PaginationBar page={page} totalPages={data.totalPages} totalElements={data.totalElements} onChange={setPage} />
        </>
      )}
    </>
  );
}

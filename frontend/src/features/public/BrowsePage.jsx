import { useCallback, useEffect, useState } from 'react';
import { Typography, Box } from '@mui/material';
import Grid from '@mui/material/Grid2';
import { useSearchParams } from 'react-router-dom';
import { publicApi } from '../../api/publicApi';
import { errorMessage } from '../../api/axiosClient';
import ProjectCard from '../../components/ProjectCard';
import PaginationBar from '../../components/PaginationBar';
import Loading from '../../components/Loading';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import SearchFilters, { EMPTY_FILTERS } from './SearchFilters';

/** Home / Browse page: search, filters, result cards and pagination (FR-CUS-1, FR-CUS-2). */
export default function BrowsePage() {
  const [params, setParams] = useSearchParams();
  const [categories, setCategories] = useState([]);
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  const filters = Object.fromEntries(Object.keys(EMPTY_FILTERS).map((k) => [k, params.get(k) ?? EMPTY_FILTERS[k]]));
  const page = Number(params.get('page') || 0);
  const query = params.toString();

  useEffect(() => {
    publicApi.categories().then(setCategories).catch(() => setCategories([]));
  }, []);

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const current = new URLSearchParams(query);
      const search = Object.fromEntries(current.entries());
      setResult(await publicApi.searchProjects({ ...search, page: Number(search.page || 0), size: 9 }));
    } catch (e) {
      setError(errorMessage(e, 'Could not load projects'));
    } finally {
      setLoading(false);
    }
  }, [query]);

  useEffect(() => { load(); }, [load]);

  const search = (next) => {
    const entries = Object.entries(next).filter(([k, v]) => v !== '' && !(k === 'sort' && v === 'newest'));
    setParams(new URLSearchParams(entries));
  };

  const goToPage = (p) => {
    const next = new URLSearchParams(params);
    next.set('page', String(p));
    setParams(next);
    window.scrollTo?.({ top: 0, behavior: 'smooth' });
  };

  return (
    <>
      <Box sx={{ mb: 3 }}>
        <Typography variant="h4" component="h1">Find your interior designer</Typography>
        <Typography color="text.secondary" sx={{ mt: 0.5 }}>
          Browse real renovation projects from designers across Singapore.
        </Typography>
      </Box>
      <SearchFilters key={query} categories={categories} initial={filters} onSearch={search} />
      <ErrorAlert message={error} onRetry={load} />
      {loading && <Loading label="Loading projects..." />}
      {!loading && !error && result && result.content.length === 0 && (
        <EmptyState title="No projects found" message="Try removing some filters." />
      )}
      {!loading && result && result.content.length > 0 && (
        <>
          <Grid container spacing={3}>
            {result.content.map((p) => (
              <Grid key={p.id} size={{ xs: 12, sm: 6, md: 4 }}>
                <ProjectCard project={p} />
              </Grid>
            ))}
          </Grid>
          <PaginationBar page={page} totalPages={result.totalPages} totalElements={result.totalElements} onChange={goToPage} />
        </>
      )}
    </>
  );
}

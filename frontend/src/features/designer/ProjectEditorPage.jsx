import { useCallback, useEffect, useState } from 'react';
import { Box, Paper, Stack } from '@mui/material';
import { useNavigate, useParams } from 'react-router-dom';
import { designerApi } from '../../api/designerApi';
import { publicApi } from '../../api/publicApi';
import { errorMessage, fieldErrors } from '../../api/axiosClient';
import { useNotify } from '../../context/NotifyContext';
import PageHeader from '../../components/PageHeader';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import ProjectForm from './ProjectForm';
import ProjectImages from './ProjectImages';

/** Create a new IDP, or edit one of the designer's own IDPs including its photos. */
export default function ProjectEditorPage() {
  const { id } = useParams();
  const isEdit = !!id;
  const navigate = useNavigate();
  const notify = useNotify();
  const [categories, setCategories] = useState(null);
  const [project, setProject] = useState(null);
  const [error, setError] = useState('');
  const [serverErrors, setServerErrors] = useState({});
  const [busy, setBusy] = useState(false);

  const load = useCallback(async () => {
    setError('');
    try {
      const [cats, existing] = await Promise.all([publicApi.categories(), isEdit ? designerApi.project(id) : null]);
      setCategories(cats);
      setProject(existing);
    } catch (e) {
      setError(e.response?.status === 404 ? 'Project not found.' : errorMessage(e));
    }
  }, [id, isEdit]);

  useEffect(() => { load(); }, [load]);

  const refreshImages = async () => setProject(await designerApi.project(id));

  const submit = async (request) => {
    setBusy(true);
    setServerErrors({});
    try {
      if (isEdit) {
        setProject(await designerApi.updateProject(id, request));
        notify('Project saved');
      } else {
        const created = await designerApi.createProject(request);
        notify('Project created. Now add some photos.');
        navigate(`/designer/projects/${created.id}/edit`, { replace: true });
      }
    } catch (e) {
      setServerErrors(fieldErrors(e));
      notify(errorMessage(e, 'Could not save the project'), 'error');
    } finally {
      setBusy(false);
    }
  };

  if (error) return <ErrorAlert message={error} onRetry={load} />;
  if (!categories || (isEdit && !project)) return <Loading />;

  const initial = project ? {
    title: project.title, description: project.description ?? '', categoryId: String(project.categoryId),
    status: project.status, budget: project.budget ?? '', startDate: project.startDate ?? '',
    endDate: project.endDate ?? '', location: project.location ?? '',
  } : undefined;

  return (
    <>
      <PageHeader title={isEdit ? 'Edit project' : 'New project'}
        subtitle={isEdit ? `${project.views} views, shortlisted ${project.shortlists} times` : 'Describe the project, then add photos'} />
      <Stack spacing={3}>
        <Paper variant="outlined" sx={{ p: 3 }}>
          <ProjectForm key={project?.updatedAt ?? 'new'} mode={isEdit ? 'edit' : 'create'} initial={initial}
            categories={categories} serverErrors={serverErrors} busy={busy} onSubmit={submit}
            currentCategory={project ? { id: project.categoryId, name: project.categoryName } : undefined}
            onCancel={() => navigate('/designer/projects')} />
        </Paper>
        {isEdit && (
          <Box>
            <ProjectImages projectId={project.id} images={project.images} onChange={refreshImages} />
          </Box>
        )}
      </Stack>
    </>
  );
}

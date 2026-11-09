import { useCallback, useEffect, useState } from 'react';
import { Button, Card, CardContent, Typography } from '@mui/material';
import Grid from '@mui/material/Grid2';
import { Link as RouterLink } from 'react-router-dom';
import { designerApi } from '../../api/designerApi';
import { errorMessage } from '../../api/axiosClient';
import { useAuth } from '../../context/AuthContext';
import PageHeader from '../../components/PageHeader';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';

function Stat({ label, value, to }) {
  return (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Typography color="text.secondary" variant="body2">{label}</Typography>
        <Typography variant="h4" component="p" sx={{ my: 1 }}>{value}</Typography>
        {to && <Button component={RouterLink} to={to} size="small">View</Button>}
      </CardContent>
    </Card>
  );
}

/** Totals across the designer's IDPs (FR-ANA-3). */
export default function DashboardPage() {
  const { user } = useAuth();
  const [data, setData] = useState(null);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setError('');
    try {
      setData(await designerApi.dashboard());
    } catch (e) {
      setError(errorMessage(e, 'Could not load the dashboard'));
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  return (
    <>
      <PageHeader title={`Welcome, ${user?.fullName ?? ''}`} subtitle="How customers are engaging with your portfolio"
        action={<Button variant="contained" component={RouterLink} to="/designer/projects/new">New project</Button>} />
      <ErrorAlert message={error} onRetry={load} />
      {!data && !error && <Loading />}
      {data && (
        <Grid container spacing={3}>
          <Grid size={{ xs: 12, sm: 6, md: 4 }}><Stat label="Projects" value={data.totalProjects} to="/designer/projects" /></Grid>
          <Grid size={{ xs: 12, sm: 6, md: 4 }}><Stat label="Total views" value={data.totalViews} /></Grid>
          <Grid size={{ xs: 12, sm: 6, md: 4 }}><Stat label="Times shortlisted (current)" value={data.totalShortlists} /></Grid>
          <Grid size={{ xs: 12, sm: 6, md: 4 }}><Stat label="New enquiries" value={data.newEnquiries} to="/designer/enquiries?status=NEW" /></Grid>
          <Grid size={{ xs: 12, sm: 6, md: 4 }}><Stat label="All enquiries" value={data.totalEnquiries} to="/designer/enquiries" /></Grid>
        </Grid>
      )}
    </>
  );
}

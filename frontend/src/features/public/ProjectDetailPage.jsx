import { useCallback, useEffect, useState } from 'react';
import { Alert, Avatar, Box, Button, Card, CardContent, Chip, Divider, Link, Paper, Stack, Typography } from '@mui/material';
import Grid from '@mui/material/Grid2';
import ImageOutlinedIcon from '@mui/icons-material/ImageOutlined';
import CalendarIcon from '@mui/icons-material/CalendarMonthOutlined';
import MailIcon from '@mui/icons-material/MailOutline';
import FlagIcon from '@mui/icons-material/OutlinedFlag';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { publicApi } from '../../api/publicApi';
import { errorMessage } from '../../api/axiosClient';
import { fileUrl } from '../../api/config';
import { useAuth } from '../../context/AuthContext';
import { useNotify } from '../../context/NotifyContext';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import FavouriteButton from '../customer/FavouriteButton';
import EnquiryDialog from '../customer/EnquiryDialog';
import RatingsSection from './RatingsSection';
import { formatDate, formatMoney, titleCase } from '../../utils/format';

/** Public IDP detail page (FR-CUS-3). */
export default function ProjectDetailPage() {
  const { id } = useParams();
  const { role, isAuthenticated } = useAuth();
  const notify = useNotify();
  const [project, setProject] = useState(null);
  const [error, setError] = useState('');
  const [selected, setSelected] = useState(0);
  const [enquiryOpen, setEnquiryOpen] = useState(false);

  const load = useCallback(async () => {
    setError('');
    try {
      setProject(await publicApi.project(id));
      setSelected(0);
    } catch (e) {
      setError(e.response?.status === 404 ? 'This project is not available.' : errorMessage(e, 'Could not load the project'));
    }
  }, [id]);

  useEffect(() => { load(); }, [load]);

  if (error) return <ErrorAlert message={error} onRetry={error.includes('not available') ? undefined : load} />;
  if (!project) return <Loading label="Loading project..." />;

  const { designer } = project;
  const company = designer.company;
  const images = project.images || [];
  const image = images[selected];
  const isCustomer = role === 'CUSTOMER';
  const canReport = role === 'CUSTOMER' || role === 'INTERIOR_DESIGNER';

  return (
    <>
      <Grid container spacing={4}>
        <Grid size={{ xs: 12, md: 7 }}>
          <Paper variant="outlined" sx={{ overflow: 'hidden' }}>
            {image ? (
              <Box component="img" src={fileUrl(image.url)} alt={`${project.title} photo ${selected + 1}`}
                sx={{ width: '100%', aspectRatio: '16 / 10', objectFit: 'cover', display: 'block' }} />
            ) : (
              <Box sx={{ aspectRatio: '16 / 10', bgcolor: 'grey.100', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <ImageOutlinedIcon color="disabled" sx={{ fontSize: 64 }} />
              </Box>
            )}
          </Paper>
          {images.length > 1 && (
            <Stack direction="row" spacing={1} sx={{ mt: 1, overflowX: 'auto', pb: 1 }} aria-label="gallery">
              {images.map((img, i) => (
                <Box key={img.id} component="button" type="button" onClick={() => setSelected(i)}
                  aria-label={`Show photo ${i + 1}`}
                  sx={{ p: 0, border: 2, borderColor: i === selected ? 'primary.main' : 'transparent', borderRadius: 1,
                    cursor: 'pointer', bgcolor: 'transparent', flex: '0 0 auto' }}>
                  <Box component="img" src={fileUrl(img.url)} alt="" sx={{ width: 96, height: 64, objectFit: 'cover', display: 'block', borderRadius: 0.5 }} />
                </Box>
              ))}
            </Stack>
          )}
        </Grid>

        <Grid size={{ xs: 12, md: 5 }}>
          <Stack direction="row" spacing={1} sx={{ mb: 1 }}>
            <Chip label={project.categoryName} />
            <Chip variant="outlined" label={titleCase(project.status)} />
          </Stack>
          <Typography variant="h4" component="h1" gutterBottom>{project.title}</Typography>
          <Typography variant="h6" color="secondary.main" gutterBottom>{formatMoney(project.budget)}</Typography>
          <Stack spacing={0.5} sx={{ color: 'text.secondary', mb: 2 }}>
            {project.location && <Typography>Location: {project.location}</Typography>}
            {(project.startDate || project.endDate) && (
              <Typography>
                {project.startDate ? formatDate(project.startDate) : '?'} - {project.endDate ? formatDate(project.endDate) : 'ongoing'}
              </Typography>
            )}
          </Stack>
          {project.description && <Typography sx={{ whiteSpace: 'pre-line', mb: 3 }}>{project.description}</Typography>}

          {isCustomer && (
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1} sx={{ mb: 2 }} flexWrap="wrap" useFlexGap>
              <FavouriteButton projectId={project.id} initial={!!project.favourited} />
              <Button variant="contained" startIcon={<MailIcon />} onClick={() => setEnquiryOpen(true)}>Send enquiry</Button>
              <Button component={RouterLink} to={`/customer/designers/${designer.id}/availability`} startIcon={<CalendarIcon />}>
                Designer availability
              </Button>
            </Stack>
          )}
          {!isAuthenticated && (
            <Alert severity="info" sx={{ mb: 2 }}>
              <Link component={RouterLink} to="/login" state={{ from: `/projects/${project.id}` }}>Log in</Link> as a customer to save or enquire about this project.
            </Alert>
          )}
          {canReport && (
            <Button size="small" color="inherit" startIcon={<FlagIcon />} component={RouterLink}
              to={`/reports/new?projectId=${project.id}&designerId=${designer.id}&title=${encodeURIComponent(project.title)}`}>
              Report this project
            </Button>
          )}
        </Grid>
      </Grid>

      <Divider sx={{ my: 4 }} />

      <Grid container spacing={4}>
        <Grid size={{ xs: 12, md: 5 }}>
          <Card>
            <CardContent>
              <Typography variant="overline" color="text.secondary">Designer</Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, my: 1 }}>
                <Avatar src={fileUrl(company?.logoUrl) || undefined} alt={designer.displayName} sx={{ width: 56, height: 56 }}>
                  {designer.displayName?.charAt(0)}
                </Avatar>
                <Box>
                  <Typography variant="h6" component="h2">{designer.displayName}</Typography>
                  {company && <Typography variant="body2" color="text.secondary">by {designer.fullName}</Typography>}
                </Box>
              </Box>
              {designer.profile?.specialisation && <Typography><strong>Specialisation:</strong> {designer.profile.specialisation}</Typography>}
              {designer.profile?.yearsOfExperience != null && (
                <Typography><strong>Experience:</strong> {designer.profile.yearsOfExperience} years</Typography>
              )}
              {designer.profile?.bio && <Typography sx={{ mt: 1, whiteSpace: 'pre-line' }}>{designer.profile.bio}</Typography>}
              {company && (
                <Box sx={{ mt: 2 }}>
                  {company.description && <Typography variant="body2" sx={{ mb: 1, whiteSpace: 'pre-line' }}>{company.description}</Typography>}
                  {company.registrationNumber && <Typography variant="body2">Registration no.: {company.registrationNumber}</Typography>}
                  {company.address && <Typography variant="body2">{company.address}</Typography>}
                  {company.contactEmail && <Typography variant="body2">{company.contactEmail}</Typography>}
                  {company.contactPhone && <Typography variant="body2">{company.contactPhone}</Typography>}
                  {company.websiteUrl && (
                    <Link href={company.websiteUrl} target="_blank" rel="noopener noreferrer" variant="body2">{company.websiteUrl}</Link>
                  )}
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 7 }}>
          <RatingsSection designer={designer} project={project} />
        </Grid>
      </Grid>

      <EnquiryDialog open={enquiryOpen} projectId={project.id} projectTitle={project.title}
        onClose={() => setEnquiryOpen(false)} onSent={() => notify('Enquiry sent to the designer')} />
    </>
  );
}

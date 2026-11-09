import { Card, CardActionArea, CardContent, CardMedia, Chip, Stack, Typography, Box } from '@mui/material';
import ImageOutlinedIcon from '@mui/icons-material/ImageOutlined';
import { Link as RouterLink } from 'react-router-dom';
import { fileUrl } from '../api/config';
import { formatMoney, titleCase } from '../utils/format';

/** Search result card (FR-CUS-2): cover image, title, category, status, budget, designer/company. */
export default function ProjectCard({ project, action }) {
  return (
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <CardActionArea component={RouterLink} to={`/projects/${project.id}`} sx={{ flexGrow: 1, alignItems: 'stretch' }}>
        {project.coverImageUrl ? (
          <CardMedia component="img" height="180" image={fileUrl(project.coverImageUrl)} alt={project.title} />
        ) : (
          <Box sx={{ height: 180, bgcolor: 'grey.100', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <ImageOutlinedIcon color="disabled" fontSize="large" />
          </Box>
        )}
        <CardContent>
          <Typography variant="subtitle1" fontWeight={600} gutterBottom noWrap title={project.title}>
            {project.title}
          </Typography>
          <Typography variant="body2" color="text.secondary" noWrap>{project.designerName}</Typography>
          <Stack direction="row" spacing={1} sx={{ mt: 1.5, flexWrap: 'wrap', rowGap: 1 }}>
            <Chip size="small" label={project.categoryName} />
            <Chip size="small" variant="outlined" label={titleCase(project.status)} />
          </Stack>
          <Typography sx={{ mt: 1.5 }} fontWeight={600}>{formatMoney(project.budget)}</Typography>
        </CardContent>
      </CardActionArea>
      {action && <Box sx={{ px: 2, pb: 2 }}>{action}</Box>}
    </Card>
  );
}

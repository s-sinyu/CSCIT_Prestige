import { useState } from 'react';
import { Box, Chip, IconButton, Paper, Tooltip, Typography } from '@mui/material';
import Grid from '@mui/material/Grid2';
import DeleteIcon from '@mui/icons-material/DeleteOutline';
import StarIcon from '@mui/icons-material/StarOutline';
import { designerApi } from '../../api/designerApi';
import { errorMessage } from '../../api/axiosClient';
import { fileUrl } from '../../api/config';
import { useNotify } from '../../context/NotifyContext';
import ImageUploader from '../../components/ImageUploader';
import ConfirmDialog from '../../components/ConfirmDialog';

const MAX_IMAGES = 10;

/** Upload, delete and choose the cover image of an IDP (FR-IDP-3). */
export default function ProjectImages({ projectId, images, onChange }) {
  const notify = useNotify();
  const [busy, setBusy] = useState(false);
  const [toDelete, setToDelete] = useState(null);

  const upload = async (file) => {
    setBusy(true);
    try {
      await designerApi.uploadImage(projectId, file);
      notify('Image uploaded');
      await onChange();
    } catch (e) {
      notify(errorMessage(e, 'Upload failed'), 'error');
    } finally {
      setBusy(false);
    }
  };

  const makeCover = async (image) => {
    try {
      await designerApi.setCover(projectId, image.id);
      notify('Cover image updated');
      await onChange();
    } catch (e) {
      notify(errorMessage(e), 'error');
    }
  };

  const remove = async () => {
    setBusy(true);
    try {
      await designerApi.deleteImage(projectId, toDelete.id);
      notify('Image deleted');
      setToDelete(null);
      await onChange();
    } catch (e) {
      notify(errorMessage(e), 'error');
    } finally {
      setBusy(false);
    }
  };

  return (
    <Paper variant="outlined" sx={{ p: 3 }}>
      <Typography variant="h6" component="h2" gutterBottom>Photos ({images.length}/{MAX_IMAGES})</Typography>
      <Grid container spacing={2} sx={{ mb: 2 }}>
        {images.map((img) => (
          <Grid key={img.id} size={{ xs: 6, sm: 4, md: 3 }}>
            <Box sx={{ position: 'relative' }}>
              <Box component="img" src={fileUrl(img.url)} alt="" sx={{ width: '100%', aspectRatio: '4 / 3', objectFit: 'cover', borderRadius: 1, display: 'block' }} />
              {img.cover && <Chip label="Cover" size="small" color="secondary" sx={{ position: 'absolute', top: 6, left: 6 }} />}
              <Box sx={{ position: 'absolute', top: 4, right: 4, bgcolor: 'rgba(255,255,255,0.85)', borderRadius: 1 }}>
                {!img.cover && (
                  <Tooltip title="Make cover">
                    <IconButton size="small" aria-label="make cover" onClick={() => makeCover(img)}><StarIcon fontSize="small" /></IconButton>
                  </Tooltip>
                )}
                <Tooltip title="Delete">
                  <IconButton size="small" aria-label="delete image" onClick={() => setToDelete(img)}><DeleteIcon fontSize="small" /></IconButton>
                </Tooltip>
              </Box>
            </Box>
          </Grid>
        ))}
      </Grid>
      <ImageUploader multiple onUpload={upload} disabled={busy || images.length >= MAX_IMAGES}
        label={images.length >= MAX_IMAGES ? 'Maximum of 10 photos reached' : 'Add photos'} />
      <ConfirmDialog open={!!toDelete} title="Delete this photo?" busy={busy} onConfirm={remove} onCancel={() => setToDelete(null)} />
    </Paper>
  );
}

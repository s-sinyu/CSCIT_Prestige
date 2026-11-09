import { useEffect, useState } from 'react';
import { Alert, Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, FormControlLabel, Rating, Switch,
  TextField, Typography } from '@mui/material';
import { customerApi } from '../../api/customerApi';
import { errorMessage } from '../../api/axiosClient';

/**
 * Create or edit a rating (FR-RT-1 to 3). In create mode the customer may tie the rating to the current IDP.
 * existing = rating being edited, or null to create.
 */
export default function RatingFormDialog({ open, designerId, designerName, project, existing, onClose, onSaved }) {
  const [score, setScore] = useState(5);
  const [comment, setComment] = useState('');
  const [forProject, setForProject] = useState(false);
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    if (open) {
      setScore(existing?.score ?? 5);
      setComment(existing?.comment ?? '');
      setForProject(false);
      setError('');
    }
  }, [open, existing]);

  const save = async () => {
    if (!score) {
      setError('Please choose a score from 1 to 5');
      return;
    }
    if (comment.length > 1000) {
      setError('Comment must be at most 1000 characters');
      return;
    }
    setBusy(true);
    setError('');
    try {
      const saved = existing
        ? await customerApi.updateRating(existing.id, { score, comment: comment.trim() || null })
        : await customerApi.createRating({ designerId, projectId: forProject ? project?.id : null, score,
          comment: comment.trim() || null });
      onSaved?.(saved);
      onClose();
    } catch (e) {
      setError(errorMessage(e, 'Could not save the rating'));
    } finally {
      setBusy(false);
    }
  };

  return (
    <Dialog open={open} onClose={busy ? undefined : onClose} fullWidth maxWidth="sm">
      <DialogTitle>{existing ? 'Edit your rating' : `Rate ${designerName}`}</DialogTitle>
      <DialogContent>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
          <Typography component="legend" id="score-label">Score</Typography>
          <Rating name="score" value={score} onChange={(_, v) => setScore(v)} aria-labelledby="score-label" />
        </Box>
        {!existing && project && (
          <FormControlLabel control={<Switch checked={forProject} onChange={(e) => setForProject(e.target.checked)} />}
            label={`This rating is about the project "${project.title}"`} />
        )}
        <TextField label="Comment (optional)" multiline minRows={3} fullWidth sx={{ mt: 1 }} value={comment}
          onChange={(e) => setComment(e.target.value)} helperText={`${comment.length}/1000`}
          slotProps={{ htmlInput: { maxLength: 1000 } }} />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={busy}>Cancel</Button>
        <Button variant="contained" onClick={save} disabled={busy}>{busy ? 'Saving...' : 'Save rating'}</Button>
      </DialogActions>
    </Dialog>
  );
}

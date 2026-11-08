import { useRef, useState } from 'react';
import { Box, Button, Typography } from '@mui/material';
import UploadIcon from '@mui/icons-material/CloudUploadOutlined';

export const ACCEPTED_TYPES = ['image/jpeg', 'image/png', 'image/webp'];
export const MAX_BYTES = 5 * 1024 * 1024;

/** Checks a file before upload; the server validates again. Returns an error message or ''. */
export function checkImage(file) {
  if (!ACCEPTED_TYPES.includes(file.type)) return `${file.name}: only JPEG, PNG or WebP images are allowed`;
  if (file.size > MAX_BYTES) return `${file.name}: file is too large (maximum 5 MB)`;
  return '';
}

/** File picker for JPEG/PNG/WebP images up to 5 MB. Calls onUpload(file) for each valid file. */
export default function ImageUploader({ onUpload, multiple = false, disabled = false, label = 'Upload image' }) {
  const input = useRef(null);
  const [error, setError] = useState('');

  const handleChange = async (event) => {
    const files = Array.from(event.target.files || []);
    event.target.value = '';
    const problems = files.map(checkImage).filter(Boolean);
    setError(problems.join(' '));
    for (const file of files.filter((f) => !checkImage(f))) {
      // Upload one at a time so the server's image limit is respected in order.
      await onUpload(file);
    }
  };

  return (
    <Box>
      <input
        ref={input}
        type="file"
        hidden
        accept={ACCEPTED_TYPES.join(',')}
        multiple={multiple}
        onChange={handleChange}
        data-testid="image-input"
      />
      <Button variant="outlined" startIcon={<UploadIcon />} disabled={disabled} onClick={() => input.current?.click()}>
        {label}
      </Button>
      <Typography variant="caption" display="block" color="text.secondary" sx={{ mt: 0.5 }}>
        JPEG, PNG or WebP, up to 5 MB each.
      </Typography>
      {error && <Typography variant="body2" color="error" role="alert">{error}</Typography>}
    </Box>
  );
}

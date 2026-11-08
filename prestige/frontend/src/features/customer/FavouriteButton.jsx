import { useState } from 'react';
import { Button } from '@mui/material';
import FavoriteIcon from '@mui/icons-material/Favorite';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import { customerApi } from '../../api/customerApi';
import { errorMessage } from '../../api/axiosClient';
import { useNotify } from '../../context/NotifyContext';

/** Adds or removes an IDP from the customer's favourites (FR-CUS-4). */
export default function FavouriteButton({ projectId, initial = false, onChange }) {
  const [favourited, setFavourited] = useState(initial);
  const [busy, setBusy] = useState(false);
  const notify = useNotify();

  const toggle = async () => {
    setBusy(true);
    try {
      if (favourited) {
        await customerApi.removeFavourite(projectId);
        notify('Removed from favourites');
      } else {
        await customerApi.addFavourite(projectId);
        notify('Saved to favourites');
      }
      setFavourited(!favourited);
      onChange?.(!favourited);
    } catch (e) {
      notify(errorMessage(e), 'error');
    } finally {
      setBusy(false);
    }
  };

  return (
    <Button variant={favourited ? 'contained' : 'outlined'} color="secondary" onClick={toggle} disabled={busy}
      startIcon={favourited ? <FavoriteIcon /> : <FavoriteBorderIcon />} aria-pressed={favourited}>
      {favourited ? 'Saved to favourites' : 'Save to favourites'}
    </Button>
  );
}

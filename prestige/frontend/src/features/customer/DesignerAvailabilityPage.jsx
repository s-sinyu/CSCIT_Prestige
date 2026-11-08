import { useCallback, useEffect, useState } from 'react';
import { Button } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useNavigate, useParams } from 'react-router-dom';
import { customerApi } from '../../api/customerApi';
import { errorMessage } from '../../api/axiosClient';
import PageHeader from '../../components/PageHeader';
import MonthCalendar, { shiftMonth } from '../../components/MonthCalendar';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import { todaySgt } from '../../utils/format';

/** Read-only monthly availability of a designer, for logged-in customers (FR-AV-5). */
export default function DesignerAvailabilityPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [month, setMonth] = useState(todaySgt().slice(0, 7));
  const [data, setData] = useState(null);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setError('');
    try {
      setData(await customerApi.designerAvailability(id, month));
    } catch (e) {
      setError(e.response?.status === 404 ? 'This designer is not available.' : errorMessage(e));
    }
  }, [id, month]);

  useEffect(() => { load(); }, [load]);

  return (
    <>
      <PageHeader title="Designer availability"
        subtitle={data ? `${data.designerName}. This calendar is for information only; contact the designer to book.` : ''}
        action={<Button startIcon={<ArrowBackIcon />} onClick={() => navigate(-1)}>Back</Button>} />
      <ErrorAlert message={error} onRetry={load} />
      {!data && !error && <Loading />}
      {data && (
        <MonthCalendar month={data.month} days={data.days} today={data.today}
          onPrev={() => setMonth(shiftMonth(month, -1))} onNext={() => setMonth(shiftMonth(month, 1))} />
      )}
    </>
  );
}

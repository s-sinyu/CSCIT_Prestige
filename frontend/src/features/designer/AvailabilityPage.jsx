import { useCallback, useEffect, useState } from 'react';
import { designerApi } from '../../api/designerApi';
import { errorMessage } from '../../api/axiosClient';
import { useNotify } from '../../context/NotifyContext';
import PageHeader from '../../components/PageHeader';
import MonthCalendar, { shiftMonth } from '../../components/MonthCalendar';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import { todaySgt } from '../../utils/format';

/** The designer's own calendar; click today or a future date to toggle it (FR-AV-1 to 4). */
export default function AvailabilityPage() {
  const notify = useNotify();
  const [month, setMonth] = useState(todaySgt().slice(0, 7));
  const [data, setData] = useState(null);
  const [error, setError] = useState('');
  const [busyDate, setBusyDate] = useState(null);

  const load = useCallback(async () => {
    setError('');
    try {
      setData(await designerApi.availability(month));
    } catch (e) {
      setError(errorMessage(e));
    }
  }, [month]);

  useEffect(() => { load(); }, [load]);

  const toggle = async (day) => {
    const next = day.status === 'UNAVAILABLE' ? 'AVAILABLE' : 'UNAVAILABLE';
    setBusyDate(day.date);
    try {
      await designerApi.setAvailability(day.date, next);
      setData((d) => ({ ...d, days: d.days.map((x) => (x.date === day.date ? { ...x, status: next } : x)) }));
    } catch (e) {
      notify(errorMessage(e), 'error');
    } finally {
      setBusyDate(null);
    }
  };

  return (
    <>
      <PageHeader title="My availability"
        subtitle="Customers can see this calendar. Click a date to switch it between available and unavailable. Past dates cannot be changed." />
      <ErrorAlert message={error} onRetry={load} />
      {!data && !error && <Loading />}
      {data && (
        <MonthCalendar month={data.month} days={data.days} today={data.today} onToggle={toggle} busyDate={busyDate}
          onPrev={() => setMonth(shiftMonth(month, -1))} onNext={() => setMonth(shiftMonth(month, 1))} />
      )}
    </>
  );
}

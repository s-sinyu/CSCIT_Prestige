import { useCallback, useEffect, useState } from 'react';
import { Box, Button, IconButton, Paper, Stack, TextField, ToggleButton, ToggleButtonGroup, Typography } from '@mui/material';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import DownloadIcon from '@mui/icons-material/DownloadOutlined';
import { managementApi } from '../../api/managementApi';
import { errorMessage } from '../../api/axiosClient';
import { useNotify } from '../../context/NotifyContext';
import PageHeader from '../../components/PageHeader';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import ReportView from './ReportView';
import { formatDate, todaySgt } from '../../utils/format';

/** Daily / weekly / monthly platform report with previous/next navigation and CSV export (FR-PM-5 to 8). */
export default function ReportsPage() {
  const notify = useNotify();
  const [period, setPeriod] = useState('DAILY');
  const [date, setDate] = useState(todaySgt());
  const [report, setReport] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      setReport(await managementApi.report(period, date));
    } catch (e) {
      setError(errorMessage(e, 'Could not load the report'));
    } finally {
      setLoading(false);
    }
  }, [period, date]);

  useEffect(() => { load(); }, [load]);

  const exportCsv = async () => {
    try {
      const response = await managementApi.exportReport(period, date);
      const disposition = response.headers?.['content-disposition'] || '';
      const name = /filename="?([^";]+)"?/.exec(disposition)?.[1] || `platform-report-${period.toLowerCase()}-${date}.csv`;
      const url = URL.createObjectURL(response.data);
      const link = document.createElement('a');
      link.href = url;
      link.download = name;
      document.body.appendChild(link);
      link.click();
      link.remove();
      URL.revokeObjectURL(url);
    } catch (e) {
      notify(errorMessage(e, 'Export failed'), 'error');
    }
  };

  const range = report && (report.startDate === report.endDate
    ? formatDate(report.startDate) : `${formatDate(report.startDate)} - ${formatDate(report.endDate)}`);

  return (
    <>
      <PageHeader title="Platform reports" subtitle="Times are in Singapore time. Weeks run Monday to Sunday."
        action={<Button variant="outlined" startIcon={<DownloadIcon />} onClick={exportCsv} disabled={!report}>Export CSV</Button>} />
      <Paper variant="outlined" sx={{ p: 2, mb: 3 }}>
        <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} alignItems={{ md: 'center' }} justifyContent="space-between">
          <ToggleButtonGroup exclusive value={period} onChange={(_, v) => v && setPeriod(v)} aria-label="period type" size="small">
            <ToggleButton value="DAILY">Daily</ToggleButton>
            <ToggleButton value="WEEKLY">Weekly</ToggleButton>
            <ToggleButton value="MONTHLY">Monthly</ToggleButton>
          </ToggleButtonGroup>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <IconButton aria-label="previous period" disabled={!report} onClick={() => setDate(report.previousDate)}><ChevronLeftIcon /></IconButton>
            <Typography sx={{ minWidth: 200, textAlign: 'center' }} aria-live="polite">{range}</Typography>
            <IconButton aria-label="next period" disabled={!report} onClick={() => setDate(report.nextDate)}><ChevronRightIcon /></IconButton>
          </Box>
          <Stack direction="row" spacing={1}>
            <TextField type="date" size="small" label="Date in period" value={date} onChange={(e) => e.target.value && setDate(e.target.value)}
              slotProps={{ inputLabel: { shrink: true } }} />
            <Button onClick={() => setDate(todaySgt())}>Current</Button>
          </Stack>
        </Stack>
      </Paper>
      <ErrorAlert message={error} onRetry={load} />
      {loading && !report && <Loading label="Calculating report..." />}
      {report && <Box sx={{ opacity: loading ? 0.5 : 1 }}><ReportView report={report} /></Box>}
    </>
  );
}

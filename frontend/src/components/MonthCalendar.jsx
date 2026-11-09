import { Box, IconButton, Paper, Typography, Tooltip } from '@mui/material';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';

const WEEKDAYS = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

export function shiftMonth(month, delta) {
  const [y, m] = month.split('-').map(Number);
  const d = new Date(Date.UTC(y, m - 1 + delta, 1));
  return `${d.getUTCFullYear()}-${String(d.getUTCMonth() + 1).padStart(2, '0')}`;
}

function monthLabel(month) {
  const [y, m] = month.split('-').map(Number);
  return new Date(Date.UTC(y, m - 1, 1)).toLocaleDateString('en-SG', { timeZone: 'UTC', month: 'long', year: 'numeric' });
}

/**
 * Month grid of availability. days = [{ date: 'YYYY-MM-DD', status }]. When onToggle is given (designer view),
 * today and future dates are clickable; past dates are never editable (FR-AV-3).
 */
export default function MonthCalendar({ month, days, today, onPrev, onNext, onToggle, busyDate }) {
  const firstWeekday = days.length ? (new Date(`${days[0].date}T00:00:00Z`).getUTCDay() + 6) % 7 : 0;
  return (
    <Paper variant="outlined" sx={{ p: 2, maxWidth: 560 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
        <IconButton aria-label="previous month" onClick={onPrev}><ChevronLeftIcon /></IconButton>
        <Typography variant="h6" component="h2">{monthLabel(month)}</Typography>
        <IconButton aria-label="next month" onClick={onNext}><ChevronRightIcon /></IconButton>
      </Box>
      <Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 0.75 }}>
        {WEEKDAYS.map((d) => (
          <Typography key={d} variant="caption" align="center" color="text.secondary">{d}</Typography>
        ))}
        {Array.from({ length: firstWeekday }).map((_, i) => <Box key={`pad-${i}`} />)}
        {days.map((day) => {
          const past = today && day.date < today;
          const unavailable = day.status === 'UNAVAILABLE';
          const editable = onToggle && !past;
          const label = `${day.date} ${unavailable ? 'unavailable' : 'available'}${past ? ' (past)' : ''}`;
          return (
            <Tooltip key={day.date} title={unavailable ? 'Unavailable' : 'Available'}>
              <Box
                component={editable ? 'button' : 'div'}
                type={editable ? 'button' : undefined}
                aria-label={label}
                data-status={day.status}
                disabled={editable ? busyDate === day.date : undefined}
                onClick={editable ? () => onToggle(day) : undefined}
                sx={{
                  border: 1, borderColor: unavailable ? 'grey.400' : 'success.light', borderRadius: 1.5, py: 1,
                  textAlign: 'center', font: 'inherit',
                  bgcolor: unavailable ? 'grey.200' : 'rgba(46,125,50,0.08)',
                  color: past ? 'text.disabled' : 'text.primary',
                  opacity: past ? 0.6 : 1,
                  cursor: editable ? 'pointer' : 'default',
                  textDecoration: unavailable ? 'line-through' : 'none',
                }}
              >
                {Number(day.date.slice(8))}
              </Box>
            </Tooltip>
          );
        })}
      </Box>
      <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 2 }}>
        Green: available. Grey and struck through: unavailable. Dates without a setting count as available.
      </Typography>
    </Paper>
  );
}

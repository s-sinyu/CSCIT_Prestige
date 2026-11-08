const SGT = 'Asia/Singapore';

export const ROLE_LABELS = {
  CUSTOMER: 'Customer',
  INTERIOR_DESIGNER: 'Interior Designer',
  PLATFORM_MANAGEMENT: 'Platform Management',
  USER_ADMIN: 'User Admin',
};

export function formatMoney(value) {
  if (value === null || value === undefined || value === '') return 'Budget not stated';
  return new Intl.NumberFormat('en-SG', { style: 'currency', currency: 'SGD', maximumFractionDigits: 0 }).format(value);
}

export function formatDate(value) {
  if (!value) return '';
  // Plain dates (YYYY-MM-DD) are calendar dates; show them without time-zone shifting.
  if (/^\d{4}-\d{2}-\d{2}$/.test(value)) {
    const [y, m, d] = value.split('-').map(Number);
    return new Date(Date.UTC(y, m - 1, d)).toLocaleDateString('en-SG', { timeZone: 'UTC', day: 'numeric', month: 'short', year: 'numeric' });
  }
  return new Date(value).toLocaleDateString('en-SG', { timeZone: SGT, day: 'numeric', month: 'short', year: 'numeric' });
}

export function formatDateTime(value) {
  if (!value) return '';
  return new Date(value).toLocaleString('en-SG', {
    timeZone: SGT, day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit',
  });
}

/** Today's date in Singapore as YYYY-MM-DD. */
export function todaySgt() {
  return new Intl.DateTimeFormat('en-CA', { timeZone: SGT }).format(new Date());
}

export function titleCase(value) {
  if (!value) return '';
  return value.charAt(0) + value.slice(1).toLowerCase().replace(/_/g, ' ');
}

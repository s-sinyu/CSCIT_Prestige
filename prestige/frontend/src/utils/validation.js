/** Client-side rules mirroring the backend Bean Validation rules. Each returns an error message or ''. */
export const PASSWORD_MESSAGE = 'Password must be at least 8 characters and contain a letter and a digit';

export function validateEmail(value) {
  if (!value || !value.trim()) return 'Email is required';
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value.trim())) return 'Email format is invalid';
  return '';
}

export function validatePassword(value) {
  if (!value) return 'Password is required';
  if (value.length < 8 || !/[A-Za-z]/.test(value) || !/\d/.test(value)) return PASSWORD_MESSAGE;
  return '';
}

export function validateRequired(value, label, max) {
  if (value === null || value === undefined || String(value).trim() === '') return `${label} is required`;
  if (max && String(value).length > max) return `${label} must be at most ${max} characters`;
  return '';
}

export function validateMax(value, label, max) {
  if (value && String(value).length > max) return `${label} must be at most ${max} characters`;
  return '';
}

export function validatePhone(value) {
  if (!value) return '';
  return /^[+0-9 ()-]{6,30}$/.test(value) ? '' : 'Phone may contain digits, spaces, +, - and brackets (6 to 30 characters)';
}

export function validateUrl(value) {
  if (!value) return '';
  return /^https?:\/\/[^\s/$.?#].[^\s]*$/i.test(value) ? '' : 'Website must be a valid http(s) URL';
}

/** Removes empty messages; returns {} when the form is valid. */
export function compact(errors) {
  return Object.fromEntries(Object.entries(errors).filter(([, v]) => v));
}

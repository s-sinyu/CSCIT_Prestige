/** Backend origin, e.g. https://prestige-api.onrender.com (set VITE_API_BASE_URL at build time). */
export const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080').replace(/\/+$/, '');

/** Turns an API-relative file path such as /api/files/abc.jpg into an absolute URL. */
export function fileUrl(path) {
  if (!path) return null;
  return path.startsWith('http') ? path : `${API_BASE_URL}${path}`;
}

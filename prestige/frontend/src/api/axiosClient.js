import axios from 'axios';
import { API_BASE_URL } from './config';

export const TOKEN_KEY = 'prestige.token';
export const USER_KEY = 'prestige.user';

const axiosClient = axios.create({ baseURL: `${API_BASE_URL}/api`, timeout: 60000 });

let unauthorizedHandler = null;
/** AuthContext registers what to do when the API says the session is no longer valid. */
export function onUnauthorized(handler) {
  unauthorizedHandler = handler;
}

axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const isLoginCall = error.config?.url?.includes('/auth/login');
    if (error.response?.status === 401 && !isLoginCall && localStorage.getItem(TOKEN_KEY)) {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_KEY);
      if (unauthorizedHandler) unauthorizedHandler();
    }
    return Promise.reject(error);
  },
);

/** Human-readable message from an API error ({ message, fieldErrors }). */
export function errorMessage(error, fallback = 'Something went wrong. Please try again.') {
  if (!error) return fallback;
  if (error.code === 'ECONNABORTED') return 'The server is taking too long to respond (it may be waking up). Please try again.';
  if (!error.response) return 'Cannot reach the server. Please check your connection or try again shortly.';
  return error.response.data?.message || fallback;
}

/** Field errors keyed by field name, e.g. { email: 'Email format is invalid' }. */
export function fieldErrors(error) {
  return error?.response?.data?.fieldErrors || {};
}

export default axiosClient;

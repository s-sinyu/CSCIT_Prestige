import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { TOKEN_KEY, USER_KEY, onUnauthorized } from '../api/axiosClient';
import { authApi } from '../api/authApi';

const AuthContext = createContext(null);

/** Landing page for each role after login (PRD 8.3). */
export const HOME_BY_ROLE = {
  CUSTOMER: '/',
  INTERIOR_DESIGNER: '/designer/dashboard',
  PLATFORM_MANAGEMENT: '/management/reports',
  USER_ADMIN: '/admin/users',
};

function readStoredUser() {
  try {
    const raw = localStorage.getItem(USER_KEY);
    return raw && localStorage.getItem(TOKEN_KEY) ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readStoredUser);
  const navigate = useNavigate();

  const clear = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    setUser(null);
  }, []);

  useEffect(() => {
    onUnauthorized(() => {
      setUser(null);
      navigate('/login', { replace: true, state: { expired: true } });
    });
  }, [navigate]);

  const login = useCallback(async (email, password) => {
    const result = await authApi.login(email, password);
    localStorage.setItem(TOKEN_KEY, result.token);
    localStorage.setItem(USER_KEY, JSON.stringify(result.user));
    setUser(result.user);
    return result.user;
  }, []);

  /** Logout is client side: the token is discarded (FR-AUTH-6). */
  const logout = useCallback(() => {
    clear();
    navigate('/', { replace: true });
  }, [clear, navigate]);

  const updateUser = useCallback((next) => {
    localStorage.setItem(USER_KEY, JSON.stringify(next));
    setUser(next);
  }, []);

  const value = useMemo(() => ({
    user,
    role: user?.role ?? null,
    isAuthenticated: !!user,
    login,
    logout,
    updateUser,
  }), [user, login, logout, updateUser]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider');
  return ctx;
}

export default AuthContext;

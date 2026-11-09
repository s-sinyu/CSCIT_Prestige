import { render } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { ThemeProvider } from '@mui/material';
import theme from '../theme';
import AuthContext from '../context/AuthContext';

/** Renders UI inside the router, theme and a fake auth context ({ role } or null for visitors). */
export function renderWithProviders(ui, { role = null, route = '/', path = '*', auth = {} } = {}) {
  const user = role ? { id: 1, email: 'me@test.sg', fullName: 'Test User', role } : null;
  const value = {
    user, role, isAuthenticated: !!user, login: async () => user, logout: () => {}, updateUser: () => {}, ...auth,
  };
  return render(
    <ThemeProvider theme={theme}>
      <AuthContext.Provider value={value}>
        <MemoryRouter initialEntries={[route]}>
          <Routes>
            <Route path={path} element={ui} />
            <Route path="/login" element={<div>Login page</div>} />
            <Route path="/not-authorised" element={<div>Not authorised page</div>} />
          </Routes>
        </MemoryRouter>
      </AuthContext.Provider>
    </ThemeProvider>,
  );
}

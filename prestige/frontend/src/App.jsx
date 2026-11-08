import { BrowserRouter } from 'react-router-dom';
import { CssBaseline, ThemeProvider } from '@mui/material';
import theme from './theme';
import { AuthProvider } from './context/AuthContext';
import { NotifyProvider } from './context/NotifyContext';
import AppRoutes from './routes/AppRoutes';

export default function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <NotifyProvider>
        <BrowserRouter>
          <AuthProvider>
            <AppRoutes />
          </AuthProvider>
        </BrowserRouter>
      </NotifyProvider>
    </ThemeProvider>
  );
}

import { useState } from 'react';
import { AppBar, Box, Button, Container, Divider, Drawer, IconButton, List, ListItemButton, ListItemText,
  Toolbar, Typography, Menu, MenuItem } from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import AccountCircle from '@mui/icons-material/AccountCircleOutlined';
import { Link as RouterLink, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ROLE_LABELS } from '../utils/format';

/** Navigation shows only the links allowed for the current role (PRD 8.3). */
export const NAV_LINKS = {
  VISITOR: [{ to: '/', label: 'Browse projects' }],
  CUSTOMER: [
    { to: '/', label: 'Browse projects' },
    { to: '/customer/favourites', label: 'Favourites' },
    { to: '/reports/mine', label: 'My reports' },
  ],
  INTERIOR_DESIGNER: [
    { to: '/designer/dashboard', label: 'Dashboard' },
    { to: '/designer/projects', label: 'My projects' },
    { to: '/designer/enquiries', label: 'Enquiries' },
    { to: '/designer/availability', label: 'Availability' },
    { to: '/designer/company', label: 'Company' },
    { to: '/designer/profile', label: 'Designer profile' },
    { to: '/reports/mine', label: 'My reports' },
  ],
  PLATFORM_MANAGEMENT: [
    { to: '/management/reports', label: 'Reports' },
    { to: '/management/categories', label: 'Categories' },
    { to: '/', label: 'Browse projects' },
  ],
  USER_ADMIN: [
    { to: '/admin/users', label: 'Users' },
    { to: '/admin/issue-reports', label: 'Issue reports' },
    { to: '/', label: 'Browse projects' },
  ],
};

export default function AppLayout() {
  const { user, role, logout } = useAuth();
  const [drawer, setDrawer] = useState(false);
  const [anchor, setAnchor] = useState(null);
  const location = useLocation();
  const links = NAV_LINKS[role || 'VISITOR'];
  const isActive = (to) => (to === '/' ? location.pathname === '/' : location.pathname.startsWith(to));

  return (
    <Box sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <AppBar position="sticky" color="inherit" elevation={0} sx={{ borderBottom: 1, borderColor: 'divider' }}>
        <Toolbar>
          <IconButton edge="start" sx={{ mr: 1, display: { md: 'none' } }} aria-label="open menu" onClick={() => setDrawer(true)}>
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" component={RouterLink} to="/" sx={{ color: 'primary.main', textDecoration: 'none', fontWeight: 700, mr: 3 }}>
            Prestige
          </Typography>
          <Box component="nav" aria-label="main" sx={{ display: { xs: 'none', md: 'flex' }, gap: 0.5, flexGrow: 1 }}>
            {links.map((l) => (
              <Button key={l.to} component={RouterLink} to={l.to} color={isActive(l.to) ? 'primary' : 'inherit'}
                sx={{ fontWeight: isActive(l.to) ? 700 : 400 }}>
                {l.label}
              </Button>
            ))}
          </Box>
          <Box sx={{ flexGrow: { xs: 1, md: 0 } }} />
          {user ? (
            <>
              <Button color="inherit" startIcon={<AccountCircle />} onClick={(e) => setAnchor(e.currentTarget)} aria-label="account menu">
                <Box component="span" sx={{ display: { xs: 'none', sm: 'inline' } }}>{user.fullName}</Box>
              </Button>
              <Menu anchorEl={anchor} open={!!anchor} onClose={() => setAnchor(null)}>
                <MenuItem disabled>{ROLE_LABELS[role]}</MenuItem>
                <MenuItem component={RouterLink} to="/profile" onClick={() => setAnchor(null)}>My profile</MenuItem>
                <MenuItem onClick={() => { setAnchor(null); logout(); }}>Log out</MenuItem>
              </Menu>
            </>
          ) : (
            <>
              <Button component={RouterLink} to="/login" color="inherit">Log in</Button>
              <Button component={RouterLink} to="/register" variant="contained" sx={{ ml: 1 }}>Sign up</Button>
            </>
          )}
        </Toolbar>
      </AppBar>
      <Drawer open={drawer} onClose={() => setDrawer(false)}>
        <Box sx={{ width: 260 }} role="presentation" onClick={() => setDrawer(false)}>
          <Typography variant="h6" sx={{ p: 2, color: 'primary.main', fontWeight: 700 }}>Prestige</Typography>
          <Divider />
          <List>
            {links.map((l) => (
              <ListItemButton key={l.to} component={RouterLink} to={l.to} selected={isActive(l.to)}>
                <ListItemText primary={l.label} />
              </ListItemButton>
            ))}
          </List>
        </Box>
      </Drawer>
      <Container component="main" maxWidth="lg" sx={{ py: { xs: 3, md: 4 }, flexGrow: 1 }}>
        <Outlet />
      </Container>
      <Box component="footer" sx={{ py: 3, textAlign: 'center', color: 'text.secondary', borderTop: 1, borderColor: 'divider' }}>
        <Typography variant="body2">Prestige Interiors - CSIT314 group project demo</Typography>
      </Box>
    </Box>
  );
}

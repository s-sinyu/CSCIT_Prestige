import { Box, Card, CardContent, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from '@mui/material';
import Grid from '@mui/material/Grid2';
import { ROLE_LABELS } from '../../utils/format';

function Figure({ label, value }) {
  return (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Typography variant="body2" color="text.secondary">{label}</Typography>
        <Typography variant="h4" component="p">{value}</Typography>
      </CardContent>
    </Card>
  );
}

function Section({ title, children }) {
  return (
    <Box component="section" sx={{ mt: 4 }}>
      <Typography variant="h6" component="h2" gutterBottom>{title}</Typography>
      {children}
    </Box>
  );
}

/** Renders a platform report (FR-PM-6, FR-PM-7) as figures and tables. */
export default function ReportView({ report }) {
  const { idpsCreated, engagement, registrations, activity } = report;
  return (
    <Box>
      <Grid container spacing={2}>
        <Grid size={{ xs: 6, md: 3 }}><Figure label="IDPs created" value={idpsCreated.total} /></Grid>
        <Grid size={{ xs: 6, md: 3 }}><Figure label="Views" value={engagement.totalViews} /></Grid>
        <Grid size={{ xs: 6, md: 3 }}><Figure label="Favourites saved" value={engagement.totalFavouritesSaved} /></Grid>
        <Grid size={{ xs: 6, md: 3 }}><Figure label="New registrations" value={registrations.total} /></Grid>
      </Grid>

      <Section title="IDPs created by category">
        <TableContainer component={Paper} variant="outlined">
          <Table size="small" aria-label="IDPs created by category">
            <TableHead><TableRow><TableCell>Category</TableCell><TableCell align="right">Created</TableCell></TableRow></TableHead>
            <TableBody>
              {idpsCreated.byCategory.map((c) => (
                <TableRow key={c.categoryId}>
                  <TableCell>{c.categoryName}{c.active ? '' : ' (inactive)'}</TableCell>
                  <TableCell align="right">{c.count}</TableCell>
                </TableRow>
              ))}
              <TableRow><TableCell><strong>Total</strong></TableCell><TableCell align="right"><strong>{idpsCreated.total}</strong></TableCell></TableRow>
            </TableBody>
          </Table>
        </TableContainer>
      </Section>

      <Section title="Top 10 most viewed IDPs">
        {engagement.topViewed.length === 0 ? <Typography color="text.secondary">No views in this period.</Typography> : (
          <TableContainer component={Paper} variant="outlined">
            <Table size="small" aria-label="top viewed IDPs">
              <TableHead>
                <TableRow>
                  <TableCell>#</TableCell><TableCell>Title</TableCell>
                  <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>Designer / company</TableCell>
                  <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>Category</TableCell>
                  <TableCell align="right">Views</TableCell><TableCell align="right">Favourites saved</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {engagement.topViewed.map((p, i) => (
                  <TableRow key={p.projectId}>
                    <TableCell>{i + 1}</TableCell><TableCell>{p.title}</TableCell>
                    <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>{p.designerName}</TableCell>
                    <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>{p.categoryName}</TableCell>
                    <TableCell align="right">{p.views}</TableCell><TableCell align="right">{p.favouritesSaved}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Section>

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 6 }}>
          <Section title="New registrations">
            <TableContainer component={Paper} variant="outlined">
              <Table size="small" aria-label="registrations by role">
                <TableBody>
                  {Object.entries(registrations.byRole).map(([role, count]) => (
                    <TableRow key={role}><TableCell>{ROLE_LABELS[role] ?? role}</TableCell><TableCell align="right">{count}</TableCell></TableRow>
                  ))}
                  <TableRow><TableCell><strong>Total</strong></TableCell><TableCell align="right"><strong>{registrations.total}</strong></TableCell></TableRow>
                </TableBody>
              </Table>
            </TableContainer>
          </Section>
        </Grid>
        <Grid size={{ xs: 12, md: 6 }}>
          <Section title="Enquiries and issue reports">
            <TableContainer component={Paper} variant="outlined">
              <Table size="small" aria-label="enquiries and issue reports">
                <TableBody>
                  <TableRow><TableCell>Enquiries received</TableCell><TableCell align="right">{activity.enquiriesReceived}</TableCell></TableRow>
                  <TableRow><TableCell>Reports created, now open</TableCell><TableCell align="right">{activity.issueReportsCreatedOpen}</TableCell></TableRow>
                  <TableRow><TableCell>Reports created, now rectified</TableCell><TableCell align="right">{activity.issueReportsCreatedRectified}</TableCell></TableRow>
                  <TableRow><TableCell>All reports currently open</TableCell><TableCell align="right">{activity.issueReportsCurrentlyOpen}</TableCell></TableRow>
                </TableBody>
              </Table>
            </TableContainer>
          </Section>
        </Grid>
      </Grid>
    </Box>
  );
}

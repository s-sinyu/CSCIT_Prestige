import client from './axiosClient';

export const issueReportApi = {
  submit: (data) => client.post('/issue-reports', data).then((r) => r.data),
  mine: (page = 0) => client.get('/issue-reports/mine', { params: { page, size: 20 } }).then((r) => r.data),
  mineOne: (id) => client.get(`/issue-reports/mine/${id}`).then((r) => r.data),
};

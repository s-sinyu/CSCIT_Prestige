import client from './axiosClient';
import { cleanParams } from './publicApi';

export const adminApi = {
  users: (params) => client.get('/admin/users', { params: cleanParams(params) }).then((r) => r.data),
  user: (id) => client.get(`/admin/users/${id}`).then((r) => r.data),
  createManager: (data) => client.post('/admin/users', data).then((r) => r.data),
  updateUser: (id, data) => client.put(`/admin/users/${id}`, data).then((r) => r.data),
  setStatus: (id, status) => client.patch(`/admin/users/${id}/status`, { status }).then((r) => r.data),
  deleteUser: (id) => client.delete(`/admin/users/${id}`),
  issueReports: (params) => client.get('/admin/issue-reports', { params: cleanParams(params) }).then((r) => r.data),
  issueReport: (id) => client.get(`/admin/issue-reports/${id}`).then((r) => r.data),
  rectify: (id) => client.patch(`/admin/issue-reports/${id}/rectify`).then((r) => r.data),
  deleteRating: (id) => client.delete(`/admin/ratings/${id}`),
};

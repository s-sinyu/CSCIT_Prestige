import client from './axiosClient';

export const managementApi = {
  categories: (page = 0) => client.get('/management/categories', { params: { page, size: 20, sort: 'name' } }).then((r) => r.data),
  createCategory: (data) => client.post('/management/categories', data).then((r) => r.data),
  updateCategory: (id, data) => client.put(`/management/categories/${id}`, data).then((r) => r.data),
  deleteCategory: (id) => client.delete(`/management/categories/${id}`),
  setCategoryActive: (id, active) => client.patch(`/management/categories/${id}/active`, { active }).then((r) => r.data),
  report: (period, date) => client.get('/management/reports', { params: { period, date } }).then((r) => r.data),
  exportReport: (period, date) =>
    client.get('/management/reports/export', { params: { period, date }, responseType: 'blob' }),
};

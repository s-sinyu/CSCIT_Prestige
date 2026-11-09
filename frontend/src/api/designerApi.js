import client from './axiosClient';

export const designerApi = {
  dashboard: () => client.get('/designer/dashboard').then((r) => r.data),
  projects: (page = 0) => client.get('/designer/projects', { params: { page, size: 20 } }).then((r) => r.data),
  project: (id) => client.get(`/designer/projects/${id}`).then((r) => r.data),
  createProject: (data) => client.post('/designer/projects', data).then((r) => r.data),
  updateProject: (id, data) => client.put(`/designer/projects/${id}`, data).then((r) => r.data),
  deleteProject: (id) => client.delete(`/designer/projects/${id}`),
  uploadImage: (id, file) => {
    const form = new FormData();
    form.append('file', file);
    return client.post(`/designer/projects/${id}/images`, form).then((r) => r.data);
  },
  deleteImage: (id, imageId) => client.delete(`/designer/projects/${id}/images/${imageId}`),
  setCover: (id, imageId) => client.put(`/designer/projects/${id}/images/${imageId}/cover`).then((r) => r.data),
  company: () => client.get('/designer/company').then((r) => r.data),
  createCompany: (data) => client.post('/designer/company', data).then((r) => r.data),
  updateCompany: (data) => client.put('/designer/company', data).then((r) => r.data),
  deleteCompany: () => client.delete('/designer/company'),
  uploadLogo: (file) => {
    const form = new FormData();
    form.append('file', file);
    return client.post('/designer/company/logo', form).then((r) => r.data);
  },
  profile: () => client.get('/designer/profile').then((r) => r.data),
  updateProfile: (data) => client.put('/designer/profile', data).then((r) => r.data),
  enquiries: (params) => client.get('/designer/enquiries', { params }).then((r) => r.data),
  enquiry: (id) => client.get(`/designer/enquiries/${id}`).then((r) => r.data),
  setEnquiryStatus: (id, status) => client.patch(`/designer/enquiries/${id}/status`, { status }).then((r) => r.data),
  availability: (month) => client.get('/designer/availability', { params: { month } }).then((r) => r.data),
  setAvailability: (date, status) => client.put(`/designer/availability/${date}`, { status }).then((r) => r.data),
};

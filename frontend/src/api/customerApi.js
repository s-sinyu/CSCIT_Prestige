import client from './axiosClient';

export const customerApi = {
  favourites: (page = 0) => client.get('/customer/favourites', { params: { page, size: 9 } }).then((r) => r.data),
  addFavourite: (projectId) => client.post(`/customer/favourites/${projectId}`),
  removeFavourite: (projectId) => client.delete(`/customer/favourites/${projectId}`),
  sendEnquiry: (projectId, message) => client.post(`/projects/${projectId}/enquiries`, { message }).then((r) => r.data),
  designerAvailability: (designerId, month) =>
    client.get(`/customer/designers/${designerId}/availability`, { params: { month } }).then((r) => r.data),
  createRating: (data) => client.post('/customer/ratings', data).then((r) => r.data),
  updateRating: (id, data) => client.put(`/customer/ratings/${id}`, data).then((r) => r.data),
  deleteRating: (id) => client.delete(`/customer/ratings/${id}`),
};

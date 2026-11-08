import client from './axiosClient';

export const authApi = {
  login: (email, password) => client.post('/auth/login', { email, password }).then((r) => r.data),
  register: (data) => client.post('/auth/register', data).then((r) => r.data),
  me: () => client.get('/me').then((r) => r.data),
  updateMe: (data) => client.put('/me', data).then((r) => r.data),
  changePassword: (currentPassword, newPassword) =>
    client.put('/me/password', { currentPassword, newPassword }).then((r) => r.data),
};

import client from './axiosClient';

/** Removes empty values so they are not sent as filters. */
export function cleanParams(params) {
  return Object.fromEntries(Object.entries(params).filter(([, v]) => v !== '' && v !== null && v !== undefined));
}

export const publicApi = {
  categories: () => client.get('/categories').then((r) => r.data),
  searchProjects: (params) => client.get('/projects', { params: cleanParams(params) }).then((r) => r.data),
  project: (id) => client.get(`/projects/${id}`).then((r) => r.data),
  designerRatings: (designerId, params) =>
    client.get(`/designers/${designerId}/ratings`, { params: cleanParams(params) }).then((r) => r.data),
};

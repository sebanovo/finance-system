import { http, HttpResponse } from 'msw';
import { usersMock } from '../data/users.mock';

export const usersHandlers = [
  http.get('/api/users', () => {
    return HttpResponse.json(usersMock);
  }),
];
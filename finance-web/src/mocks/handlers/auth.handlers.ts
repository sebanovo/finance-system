import { http, HttpResponse } from 'msw';

export const authHandlers = [
  http.post('/api/auth/login', async ({ request }) => {
    const body = await request.json() as {
      email: string;
      password: string;
    };

    if (body.email === 'admin@test.com' && body.password === '123456') {
      return HttpResponse.json({
        token: 'fake-jwt-token',
        user: {
          id: 1,
          name: 'Admin User',
          email: body.email,
        },
      });
    }

    return HttpResponse.json(
      { message: 'Invalid credentials' },
      { status: 401 }
    );
  }),

  http.get('/api/auth/me', () => {
    return HttpResponse.json({
      id: 1,
      name: 'Admin User',
      email: 'admin@test.com',
    });
  }),
];
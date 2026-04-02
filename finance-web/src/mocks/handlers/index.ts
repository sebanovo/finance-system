import { productsHandlers } from './products.handlers';
import { authHandlers } from './auth.handlers';
import { usersHandlers } from './users.handlers';

export const handlers = [
  ...productsHandlers,
  ...authHandlers,
  ...usersHandlers,
];
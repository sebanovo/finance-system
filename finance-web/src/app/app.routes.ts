import { Routes } from '@angular/router';
import { AuthLayout } from './core/layout/layouts/auth-layout/auth-layout';
import { PublicLayout } from './core/layout/layouts/public-layout/public-layout';

export const routes: Routes = [
  // {
  //   path: 'products',
  //   loadChildren: () =>
  //     import('./products/products.routes').then((m) => m.productsRoutes),
  // },
  {
    path: '',
    component: PublicLayout,
    children: [
      {
        path: '',
        loadChildren: () =>
          import('./features/public/public.routes').then(
            (m) => m.PUBLIC_ROUTES
          ),
      },
    ],
  },

  {
    path: 'login',
    component: AuthLayout,
    children: [
      {
        path: '',
        loadChildren: () =>
          import('./features/auth/auth.routes').then((m) => m.AUTH_ROUTES),
      },
    ],
  },
];

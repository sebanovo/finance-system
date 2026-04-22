import { Routes } from '@angular/router';

export const PUBLIC_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/home-page/home-page').then((m) => m.HomePage),
  },
  {
    path: 'pricing',
    loadComponent: () =>
      import('./pages/pricing-page/pricing-page').then((m) => m.PricingPage),
  },
];

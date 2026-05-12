import { Routes } from '@angular/router';
import { authGuard } from './shared/api';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./pages/login-page/login-page.component').then(m => m.LoginPageComponent) },
  { path: 'onboarding', loadComponent: () => import('./pages/onboarding-page/onboarding-page.component').then(m => m.OnboardingPageComponent) },

  // admin tenant routes
  { path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/dashboard-page/dashboard-page.component').then(m => m.DashboardPageComponent),
    children: [
      { path: '', loadComponent: () => import('./pages/summary-page/summary-page.component').then(m => m.SummaryPageComponent)},
      { path: 'users', loadComponent: () => import('./pages/users-page/users-page.component').then(m => m.UsersPageComponent)}
    ]
  },

  // Ruta por defecto a login
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];

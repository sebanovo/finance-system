import { Routes } from '@angular/router';
import { authGuard, permissionGuard } from './shared/api';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./pages/login-page/login-page.component').then(m => m.LoginPageComponent) },
  { path: 'onboarding', loadComponent: () => import('./pages/onboarding-page/onboarding-page.component').then(m => m.OnboardingPageComponent) },

  // admin tenant routes
  { path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/dashboard-page/dashboard-page.component').then(m => m.DashboardPageComponent),
    children: [
      { path: 'summary', loadComponent: () => import('./pages/summary-page/summary-page.component').then(m => m.SummaryPageComponent)},
      { path: 'users', loadComponent: () => import('./pages/users-page/users-page.component').then(m => m.UsersPageComponent)},
      { path: 'roles', loadComponent: () => import('./pages/roles-page/roles-page.component').then(m => m.RolesPageComponent)},
      { path: 'accounts', canActivate: [permissionGuard('accounts.admin.read', 'accounts.create')], loadComponent: () => import('./pages/accounts-page/accounts-page.component').then(m => m.AccountsPageComponent)},
      { path: 'transactions', canActivate: [permissionGuard('transactions.admin.read')], loadComponent: () => import('./pages/transactions-page/transactions-page.component').then(m => m.TransactionsPageComponent)},
      { path: 'fx/rates', loadComponent: () => import('./pages/fx-rates-page/fx-rates-page.component').then(m => m.FxRatesPageComponent)},
      { path: 'fx/fees', loadComponent: () => import('./pages/fx-fees-page/fx-fees-page.component').then(m => m.FxFeesPageComponent)},
      { path: 'limits/rules', loadComponent: () => import('./pages/limits-rules-page/limits-rules-page.component').then(m => m.LimitsRulesPageComponent)},
      { path: 'accounting/periods', loadComponent: () => import('./pages/accounting-periods-page/accounting-periods-page.component').then(m => m.AccountingPeriodsPageComponent)},
      { path: 'accounting/journal-entries', loadComponent: () => import('./pages/accounting-journal-entries-page/accounting-journal-entries-page.component').then(m => m.AccountingJournalEntriesPageComponent)},
      { path: '', redirectTo: 'summary', pathMatch: 'full' }
    ]
  },

  // Ruta por defecto a login
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];

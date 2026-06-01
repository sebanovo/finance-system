import { Routes } from '@angular/router';
import { authGuard, permissionGuard } from './shared/api';
import { platformAuthGuard } from './shared/api/guards/platform-auth.guard';
import { LandingPageComponent } from './features/landing/ui/landing-page/landing-page.component';
import { PlatformLayoutComponent } from './shared/ui/layouts/platform-layout.component';

export const routes: Routes = [
  // ============================================================
  // PÚBLICAS
  // ============================================================
  
  // Landing page
  { path: '', component: LandingPageComponent },
  
  // Login de tenant
  { path: 'login', loadComponent: () => import('./pages/login-page/login-page.component').then(m => m.LoginPageComponent) },
  
  // Registro público de tenant
  { path: 'onboarding', loadComponent: () => import('./pages/onboarding-page/onboarding-page.component').then(m => m.OnboardingPageComponent) },
  
  // Login de SuperAdmin
  { path: 'platform/login', loadComponent: () => import('./pages/platform-login-page/platform-login-page.component').then(m => m.PlatformLoginPageComponent) },

  // ============================================================
  // TENANT (usuario normal)
  // ============================================================
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/dashboard-page/dashboard-page.component').then(m => m.DashboardPageComponent),
    children: [
      { path: 'summary', loadComponent: () => import('./pages/summary-page/summary-page.component').then(m => m.SummaryPageComponent) },
      { path: 'users', loadComponent: () => import('./pages/users-page/users-page.component').then(m => m.UsersPageComponent) },
      { path: 'roles', loadComponent: () => import('./pages/roles-page/roles-page.component').then(m => m.RolesPageComponent) },
      { 
        path: 'accounts', 
        canActivate: [permissionGuard('accounts.admin.read', 'accounts.create')], 
        loadComponent: () => import('./pages/accounts-page/accounts-page.component').then(m => m.AccountsPageComponent) 
      },
      { 
        path: 'transactions', 
        canActivate: [permissionGuard('transactions.admin.read')], 
        loadComponent: () => import('./pages/transactions-page/transactions-page.component').then(m => m.TransactionsPageComponent) 
      },
      { path: 'fx/rates', loadComponent: () => import('./pages/fx-rates-page/fx-rates-page.component').then(m => m.FxRatesPageComponent) },
      { path: 'fx/fees', loadComponent: () => import('./pages/fx-fees-page/fx-fees-page.component').then(m => m.FxFeesPageComponent) },
      { path: 'limits/rules', loadComponent: () => import('./pages/limits-rules-page/limits-rules-page.component').then(m => m.LimitsRulesPageComponent) },
      { path: 'accounting/periods', loadComponent: () => import('./pages/accounting-periods-page/accounting-periods-page.component').then(m => m.AccountingPeriodsPageComponent) },
      { path: 'accounting/journal-entries', loadComponent: () => import('./pages/accounting-journal-entries-page/accounting-journal-entries-page.component').then(m => m.AccountingJournalEntriesPageComponent) },
      { path: '', redirectTo: 'summary', pathMatch: 'full' }
    ]
  },

  // ============================================================
  // PLATFORM (SuperAdmin)
  // ============================================================
  {
    path: 'platform',
    component: PlatformLayoutComponent,  // ✅ Layout con sidebar y header
    canActivate: [platformAuthGuard],
    children: [
      // ✅ Las páginas se cargan como componentes (ya que el layout ya está cargado)
      { path: 'dashboard', loadComponent: () => import('./pages/platform-dashboard-page/platform-dashboard-page.component').then(m => m.PlatformDashboardPageComponent) },
      {
        path: 'profile',
        loadComponent: () => import('./pages/platform-profile-page/platform-profile-page.component').then(m => m.PlatformProfilePageComponent)
      },
      {
        path: 'security',
        loadComponent: () => import('./pages/platform-security-page/platform-security-page.component').then(m => m.PlatformSecurityPageComponent)
      },
      {
        path: 'backups',
        loadComponent: () => import('./pages/platform-backups-page/platform-backups-page.component').then(m => m.PlatformBackupsPageComponent)
      },
      {
        path: 'plans',
        loadComponent: () => import('./pages/platform-plans-page/platform-plans-page.component').then(m => m.PlatformPlansPageComponent)
      },
      {
        path: 'audit',
        loadComponent: () => import('./pages/platform-audit-page/platform-audit-page.component').then(m => m.PlatformAuditPageComponent)
      },
      { path: 'tenants', loadComponent: () => import('./pages/platform-tenants-page/platform-tenants-page.component').then(m => m.PlatformTenantsPageComponent) },
      { path: 'subscriptions', loadComponent: () => import('./pages/platform-subscriptions-page/platform-subscriptions-page.component').then(m => m.PlatformSubscriptionsPageComponent) },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      // app.routes.ts - agregar dentro del children de platform
    ]
  },

  // ============================================================
  // FALLBACK
  // ============================================================
  { path: '**', redirectTo: '' }
];

import { Routes } from '@angular/router';
import { authGuard, permissionGuard } from './shared/api';
import { platformAuthGuard } from './shared/api/guards/platform-auth.guard';
import { tenantRoleGuard } from './shared/api/guards/tenant-role.guard';
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
      { path: 'summary',
        canActivate: [tenantRoleGuard('OWNER_ADMIN', '/dashboard/me')],
        loadComponent: () => import('./pages/summary-page/summary-page.component').then(m => m.SummaryPageComponent)
      },

      { path: 'me',
        canActivate: [tenantRoleGuard('NON_OWNER', '/dashboard/summary')],
        loadComponent: () => import('./pages/customer-summary-page/customer-summary-page.component').then(m => m.CustomerSummaryPageComponent)
      },

      {
        path: 'users',
        canActivate: [permissionGuard('users.list')],
        loadComponent: () => import('./pages/users-page/users-page.component').then(m => m.UsersPageComponent)
      },
      {
        path: 'roles',
        canActivate: [permissionGuard('access.roles.read')],
        loadComponent: () => import('./pages/roles-page/roles-page.component').then(m => m.RolesPageComponent)
      },
      {
        path: 'permissions',
        canActivate: [permissionGuard('access.permissions.read')],
        loadComponent: () => import('./pages/permissions-page/permissions-page.component').then(m => m.PermissionsPageComponent)
      },
      {
        path: 'accounts',
        canActivate: [permissionGuard('accounts.list')],
        loadComponent: () => import('./pages/accounts-page/accounts-page.component').then(m => m.AccountsPageComponent)
      },
      {
        path: 'me/accounts',
        canActivate: [permissionGuard('me.accounts.list')],
        loadComponent: () => import('./pages/my-accounts-page/my-accounts-page.component').then(m => m.MyAccountsPageComponent)
      },
      {
        path: 'transactions',
        canActivate: [permissionGuard('transactions.read', 'transactions.admin.read')],
        loadComponent: () => import('./pages/transactions-page/transactions-page.component').then(m => m.TransactionsPageComponent)
      },
      {
        path: 'me/transactions',
        canActivate: [permissionGuard('me.transactions.read', 'me.transactions.detail')],
        loadComponent: () => import('./pages/my-transactions-page/my-transactions-page.component').then(m => m.MyTransactionsPageComponent)
      },
      {
        path: 'fx/rates',
        canActivate: [permissionGuard('fx.rates.read')],
        loadComponent: () => import('./pages/fx-rates-page/fx-rates-page.component').then(m => m.FxRatesPageComponent)
      },
      {
        path: 'fx/fees',
        canActivate: [permissionGuard('fx.fees.read')],
        loadComponent: () => import('./pages/fx-fees-page/fx-fees-page.component').then(m => m.FxFeesPageComponent)
      },
      {
        path: 'limits/rules',
        canActivate: [permissionGuard('limits.read')],
        loadComponent: () => import('./pages/limits-rules-page/limits-rules-page.component').then(m => m.LimitsRulesPageComponent)
      },
      { path: 'settings', loadComponent: () => import('./pages/settings-page/settings-page.component').then(m => m.SettingsPageComponent) },
      {
        path: 'accounting/periods',
        canActivate: [permissionGuard('accounting.periods.read')],
        loadComponent: () => import('./pages/accounting-periods-page/accounting-periods-page.component').then(m => m.AccountingPeriodsPageComponent)
      },
      {
        path: 'accounting/journal-entries',
        canActivate: [permissionGuard('accounting.journal.read')],
        loadComponent: () => import('./pages/accounting-journal-entries-page/accounting-journal-entries-page.component').then(m => m.AccountingJournalEntriesPageComponent)
      },
      {
        path: 'reporting',
        canActivate: [permissionGuard('reports.tenant.read')],
        loadComponent: () => import('./pages/reporting-page/reporting-page.component').then(m => m.ReportingPageComponent)
      },
      {
        path: 'notifications',
        loadComponent: () => import('./pages/notifications-page/notifications-page.component').then(m => m.NotificationsPageComponent)
      },
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
        path: 'settings',
        loadComponent: () => import('./pages/settings-page/settings-page.component').then(m => m.SettingsPageComponent)
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
      { path: 'reporting', loadComponent: () => import('./pages/platform-reporting-page/platform-reporting-page.component').then(m => m.PlatformReportingPageComponent) },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      // app.routes.ts - agregar dentro del children de platform
    ]
  },

  // ============================================================
  // FALLBACK
  // ============================================================
  { path: '**', redirectTo: '' }
];

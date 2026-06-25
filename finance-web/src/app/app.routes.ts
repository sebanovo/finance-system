import { Routes } from '@angular/router';
import { authGuard, permissionGuard } from './shared/api';
import { platformAuthGuard } from './shared/api/guards/platform-auth.guard';
import { tenantRoleGuard } from './shared/api/guards/tenant-role.guard';
import { LandingPageComponent } from './pages/landing-page/landing-page.component';
import { PlatformLayoutComponent } from './shared/ui/layouts/platform-layout.component';

export const routes: Routes = [
  // ============================================================
  // PÚBLICAS
  // ============================================================
  
  // Landing page
  { path: '', component: LandingPageComponent },
  
  // Login de tenant
  { path: 'login', loadComponent: () => import('./pages/login-page/login-page.component').then(m => m.LoginPageComponent) },

  // Recuperación de contraseña
  { path: 'forgot-password', loadComponent: () => import('./pages/forgot-password-page/forgot-password-page.component').then(m => m.ForgotPasswordPageComponent) },
  { path: 'reset-password', loadComponent: () => import('./pages/reset-password-page/reset-password-page.component').then(m => m.ResetPasswordPageComponent) },

  // Activación de cuenta (confirmación de correo)
  { path: 'activate', loadComponent: () => import('./pages/activate-account-page/activate-account-page.component').then(m => m.ActivateAccountPageComponent) },

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

      {
        path: 'me',
        canActivate: [tenantRoleGuard('NON_OWNER', '/dashboard/summary')],
        loadComponent: () => import('./pages/customer-services-shell/customer-services-shell.component').then(m => m.CustomerServicesShellComponent),
        children: [
          { path: '', redirectTo: 'summary', pathMatch: 'full' },
          {
            path: 'summary',
            loadComponent: () => import('./pages/customer-summary-page/customer-summary-page.component').then(m => m.CustomerSummaryPageComponent)
          },
          {
            path: 'service-enrollments',
            canActivate: [permissionGuard('me.service-enrollments.read')],
            loadComponent: () => import('./pages/my-service-enrollments-page/my-service-enrollments-page.component').then(m => m.MyServiceEnrollmentsPageComponent)
          },
          {
            path: 'service-payments',
            canActivate: [permissionGuard('me.service-payments.read', 'me.service-payments.detail')],
            loadComponent: () => import('./pages/my-service-payments-page/my-service-payments-page.component').then(m => m.MyServicePaymentsPageComponent)
          }
        ]
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
        path: 'loans',
        canActivate: [permissionGuard('loans.list', 'loans.view')],
        loadComponent: () => import('./pages/loans-page/loans-page.component').then(m => m.LoansPageComponent)
      },
      {
        path: 'me/loans',
        canActivate: [permissionGuard('me.loans.list', 'me.loans.view')],
        loadComponent: () => import('./pages/my-loans-page/my-loans-page.component').then(m => m.MyLoansPageComponent)
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
      { path: 'profile', loadComponent: () => import('./pages/profile-page/profile-page.component').then(m => m.ProfilePageComponent) },
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
        path: 'backups',
        canActivate: [permissionGuard('backups.list')],
        loadComponent: () => import('./pages/backups-page/backups-page.component').then(m => m.BackupsPageComponent)
      },
      {
        path: 'service-payments',
        canActivate: [permissionGuard('service-payments.read', 'service-payments.create')],
        loadComponent: () => import('./pages/service-payments-page/service-payments-page.component').then(m => m.ServicePaymentsPageComponent)
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
      { path: 'service-providers', loadComponent: () => import('./pages/platform-service-providers-page/platform-service-providers-page.component').then(m => m.PlatformServiceProvidersPageComponent) },
      { path: 'service-customers', loadComponent: () => import('./pages/platform-service-customers-page/platform-service-customers-page.component').then(m => m.PlatformServiceCustomersPageComponent) },
      { path: 'service-bills', loadComponent: () => import('./pages/platform-service-bills-page/platform-service-bills-page.component').then(m => m.PlatformServiceBillsPageComponent) },
      { path: 'service-bill-payments', loadComponent: () => import('./pages/platform-service-bill-payments-page/platform-service-bill-payments-page.component').then(m => m.PlatformServiceBillPaymentsPageComponent) },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      // app.routes.ts - agregar dentro del children de platform
    ]
  },

  // ============================================================
  // FALLBACK
  // ============================================================
  { path: '**', redirectTo: '' }
];

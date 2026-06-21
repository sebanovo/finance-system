# Directory Export: /home/walter/Documents/misDatos/repositorios/finance-system/finance-web

_Generated on 2026-05-31 20:21:19Z_

## Summary

- Source directory: `/home/walter/Documents/misDatos/repositorios/finance-system/finance-web
- Output file: `/home/walter/Documents/misDatos/repositorios/finance-system/frontend-done-v1.md`

## Files

### `app/app.config.ts`

```typescript
import { ApplicationConfig, importProvidersFrom, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authTokenInterceptor } from './shared/api';

import { 
  LucideAngularModule, Building2, User, ArrowRight, ArrowLeft, Eye, EyeOff,
  LayoutDashboard, Settings, Shield, Users, Key, Briefcase, CreditCard, 
  ArrowRightLeft, DollarSign, RefreshCcw, Percent, Lock, ShieldAlert, 
  BookOpen, Calendar, FileText, LogOut, ChevronDown, Menu, X, Bell,
  Plus, UserCircle2, Save, MoreHorizontal, Wallet, Pencil, CheckCircle, 
  Play, Ban, Snowflake, XCircle, ArrowDownToLine, ArrowUpFromLine, Send, 
  RotateCcw, Reply, ClipboardList  
} from 'lucide-angular';


import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authTokenInterceptor])),
    provideAnimationsAsync(),
    importProvidersFrom(LucideAngularModule.pick({ 
      Building2, User, ArrowRight, ArrowLeft, Eye, EyeOff,
      LayoutDashboard, Settings, Shield, Users, Key, Briefcase, CreditCard, 
      ArrowRightLeft, DollarSign, RefreshCcw, Percent, Lock, ShieldAlert, 
      BookOpen, Calendar, FileText, LogOut, ChevronDown, Menu, X, Bell,
      Plus, UserCircle2, Save, MoreHorizontal, Wallet, Pencil, CheckCircle, 
      Play, Ban, Snowflake, XCircle, ArrowDownToLine, ArrowUpFromLine, Send, 
      RotateCcw, Reply, ClipboardList 
    }))

  ]
};
```

### `app/app.css`

_Binary or non-text file omitted._

- MIME type: `inode/x-empty`
- Size: `0` bytes

### `app/app.html`

```html
<router-outlet />
<app-toast />

```

### `app/app.routes.ts`

```typescript
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
```

### `app/app.spec.ts`

```typescript
import { TestBed } from '@angular/core/testing';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render title', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('Hello, finance-web;
  });
});

```

### `app/app.ts`

```typescript
import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ToastComponent } from './shared/ui/toast/toast.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('finance-web;
}

```

### `app/entities/access/api/access.service.ts`

```typescript
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/api';
import { SystemPermissionResponse } from '../model/system-permission-response.model';
import { CreateTenantRoleRequest, TenantRoleResponse, UpdateTenantRoleRequest } from '../model/tenant-role.model';
import { AssignUserRolesRequest, UserRolesResponse } from '../model/user-roles.model';

@Injectable({
  providedIn: 'root'
})
export class AccessService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/api/access`;

  // --- Permissions ---
  getPermissions(): Observable<ApiResponse<SystemPermissionResponse[]>> {
    return this.http.get<ApiResponse<SystemPermissionResponse[]>>(`${this.API_URL}/permissions`);
  }

  // --- Roles ---
  getRoles(): Observable<ApiResponse<TenantRoleResponse[]>> {
    return this.http.get<ApiResponse<TenantRoleResponse[]>>(`${this.API_URL}/roles`);
  }

  getRoleById(id: string): Observable<ApiResponse<TenantRoleResponse>> {
    return this.http.get<ApiResponse<TenantRoleResponse>>(`${this.API_URL}/roles/${id}`);
  }

  createRole(request: CreateTenantRoleRequest): Observable<ApiResponse<TenantRoleResponse>> {
    return this.http.post<ApiResponse<TenantRoleResponse>>(`${this.API_URL}/roles`, request);
  }

  updateRole(id: string, request: UpdateTenantRoleRequest): Observable<ApiResponse<TenantRoleResponse>> {
    return this.http.put<ApiResponse<TenantRoleResponse>>(`${this.API_URL}/roles/${id}`, request);
  }

  activateRole(id: string): Observable<ApiResponse<TenantRoleResponse>> {
    return this.http.patch<ApiResponse<TenantRoleResponse>>(`${this.API_URL}/roles/${id}/activate`, {});
  }

  deactivateRole(id: string): Observable<ApiResponse<TenantRoleResponse>> {
    return this.http.patch<ApiResponse<TenantRoleResponse>>(`${this.API_URL}/roles/${id}/deactivate`, {});
  }

  // --- User Roles Assignment ---
  getUserRoles(userId: number | string): Observable<ApiResponse<UserRolesResponse>> {
    return this.http.get<ApiResponse<UserRolesResponse>>(`${this.API_URL}/users/${userId}/roles`);
  }

  assignUserRoles(userId: number | string, request: AssignUserRolesRequest): Observable<ApiResponse<UserRolesResponse>> {
    return this.http.put<ApiResponse<UserRolesResponse>>(`${this.API_URL}/users/${userId}/roles`, request);
  }
}

```

### `app/entities/access/index.ts`

```typescript
export * from './model/system-permission-response.model';
export * from './model/tenant-role.model';
export * from './model/user-roles.model';
export * from './api/access.service';

```

### `app/entities/access/model/system-permission-response.model.ts`

```typescript
export interface SystemPermissionResponse {
  code: string;
  module: string;
  description: string;
}

```

### `app/entities/access/model/tenant-role.model.ts`

```typescript
export interface TenantRoleResponse {
  id: string;
  name: string;
  description: string;
  active: boolean;
  createdAt: string;
  permissionCodes: string[];
}

export interface CreateTenantRoleRequest {
  name: string;
  description?: string;
  permissionCodes: string[];
}

export interface UpdateTenantRoleRequest {
  name: string;
  description?: string;
  permissionCodes: string[];
}

```

### `app/entities/access/model/user-roles.model.ts`

```typescript
import { TenantRoleResponse } from './tenant-role.model';

export interface UserRolesResponse {
  userId: string;
  roles: TenantRoleResponse[];
}

export interface AssignUserRolesRequest {
  roleIds: string[];
}

```

### `app/entities/accounting/api/accounting.service.ts`

```typescript
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/api';
import {
  AccountingPeriodResponse,
  CloseAccountingPeriodRequest,
  CreateAccountingPeriodRequest,
  JournalEntryResponse
} from '../model/accounting.model';

@Injectable({
  providedIn: 'root'
})
export class AccountingService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/api/accounting`;

  // --- Periods ---
  listPeriods(): Observable<ApiResponse<AccountingPeriodResponse[]>> {
    return this.http.get<ApiResponse<AccountingPeriodResponse[]>>(`${this.API_URL}/periods`);
  }

  createPeriod(request: CreateAccountingPeriodRequest): Observable<ApiResponse<AccountingPeriodResponse>> {
    return this.http.post<ApiResponse<AccountingPeriodResponse>>(`${this.API_URL}/periods`, request);
  }

  closePeriod(id: string, request: CloseAccountingPeriodRequest): Observable<ApiResponse<AccountingPeriodResponse>> {
    return this.http.patch<ApiResponse<AccountingPeriodResponse>>(`${this.API_URL}/periods/${id}/close`, request);
  }

  // --- Journal Entries ---
  listJournalEntries(): Observable<ApiResponse<JournalEntryResponse[]>> {
    return this.http.get<ApiResponse<JournalEntryResponse[]>>(`${this.API_URL}/journal-entries`);
  }

  getJournalEntryById(id: string): Observable<ApiResponse<JournalEntryResponse>> {
    return this.http.get<ApiResponse<JournalEntryResponse>>(`${this.API_URL}/journal-entries/${id}`);
  }
}

```

### `app/entities/accounting/index.ts`

```typescript
export * from './model/accounting.model';
export * from './api/accounting.service';

```

### `app/entities/accounting/model/accounting.model.ts`

```typescript
export interface AccountingPeriodResponse {
  id: string;
  periodCode: string;
  periodType: string;
  status: string;
  startDate: string;
  endDate: string;
  closedAt: string | null;
  description: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateAccountingPeriodRequest {
  periodCode: string;
  periodType: string;
  startDate: string;
  endDate: string;
  description?: string;
}

export interface CloseAccountingPeriodRequest {
  reason?: string;
}

export interface JournalLineResponse {
  id: string;
  lineNo: number;
  accountCode: string;
  accountName: string;
  lineType: string;
  amount: number;
  currency: string;
  description: string | null;
  createdAt: string;
}

export interface JournalEntryResponse {
  id: string;
  entryNumber: string;
  sourceTransactionId: string | null;
  entryType: string;
  status: string;
  description: string | null;
  reference: string | null;
  totalDebits: number;
  totalCredits: number;
  balanced: boolean;
  postedAt: string | null;
  createdAt: string;
  updatedAt: string;
  lines: JournalLineResponse[];
}

```

### `app/entities/accounts/api/accounts.service.ts`

```typescript
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/api';
import {
  AccountOwnerResponse,
  AccountBalanceResponse,
  CreateAccountRequest,
  UpdateAccountRequest
} from '../model/accounts.model';

@Injectable({
  providedIn: 'root'
})
export class AccountsService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/api/accounts`;

  createAccount(request: CreateAccountRequest): Observable<ApiResponse<AccountOwnerResponse>> {
    return this.http.post<ApiResponse<AccountOwnerResponse>>(this.API_URL, request);
  }

  listAccounts(): Observable<ApiResponse<AccountOwnerResponse[]>> {
    return this.http.get<ApiResponse<AccountOwnerResponse[]>>(this.API_URL);
  }

  getAccountById(id: string): Observable<ApiResponse<AccountOwnerResponse>> {
    return this.http.get<ApiResponse<AccountOwnerResponse>>(`${this.API_URL}/${id}`);
  }

  getAccountBalance(id: string): Observable<ApiResponse<AccountBalanceResponse>> {
    return this.http.get<ApiResponse<AccountBalanceResponse>>(`${this.API_URL}/${id}/balance`);
  }

  updateAccount(id: string, request: UpdateAccountRequest): Observable<ApiResponse<AccountOwnerResponse>> {
    return this.http.put<ApiResponse<AccountOwnerResponse>>(`${this.API_URL}/${id}`, request);
  }

  approveAccount(id: string): Observable<ApiResponse<AccountOwnerResponse>> {
    return this.http.patch<ApiResponse<AccountOwnerResponse>>(`${this.API_URL}/${id}/approve`, {});
  }

  activateAccount(id: string): Observable<ApiResponse<AccountOwnerResponse>> {
    return this.http.patch<ApiResponse<AccountOwnerResponse>>(`${this.API_URL}/${id}/activate`, {});
  }

  blockAccount(id: string, reason?: string): Observable<ApiResponse<AccountOwnerResponse>> {
    let params = new HttpParams();
    if (reason) params = params.set('reason', reason);
    return this.http.patch<ApiResponse<AccountOwnerResponse>>(`${this.API_URL}/${id}/block`, {}, { params });
  }

  freezeAccount(id: string, reason?: string): Observable<ApiResponse<AccountOwnerResponse>> {
    let params = new HttpParams();
    if (reason) params = params.set('reason', reason);
    return this.http.patch<ApiResponse<AccountOwnerResponse>>(`${this.API_URL}/${id}/freeze`, {}, { params });
  }

  closeAccount(id: string, reason?: string): Observable<ApiResponse<AccountOwnerResponse>> {
    let params = new HttpParams();
    if (reason) params = params.set('reason', reason);
    return this.http.patch<ApiResponse<AccountOwnerResponse>>(`${this.API_URL}/${id}/close`, {}, { params });
  }
}

```

### `app/entities/accounts/index.ts`

```typescript
export * from './model/accounts.model';
export * from './api/accounts.service';

```

### `app/entities/accounts/model/accounts.model.ts`

```typescript
export interface AccountOwnerResponse {
  id: string;
  userId: string;
  userEmail: string;
  userFirstName: string;
  userLastName: string;
  userFullName: string;
  accountNumber: string;
  accountName: string;
  accountNameLabel: string;
  customAlias: string;
  displayName: string;
  accountType: string;
  currency: string;
  availableBalance: number;
  heldBalance: number;
  totalBalance: number;
  status: string;
  statusReason: string | null;
  active: boolean;
  primary: boolean;
  openedAt: string;
  closedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface AccountBalanceResponse {
  accountId: string;
  accountNumber: string;
  accountName: string;
  accountNameLabel: string;
  customAlias: string;
  displayName: string;
  accountType: string;
  currency: string;
  availableBalance: number;
  heldBalance: number;
  totalBalance: number;
  status: string;
  active: boolean;
}

export interface CreateAccountRequest {
  userId: string;
  accountName: string;
  customAlias?: string;
  accountType: string;
  currency: string;
  primary?: boolean;
}

export interface UpdateAccountRequest {
  accountName?: string;
  customAlias?: string;
  primary?: boolean;
}

```

### `app/entities/auth/api/auth.service.ts`

```typescript
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../shared/api';
import { LoginRequest } from '../model/login-request.model';
import { AuthTokenResponse } from '../model/auth-token-response.model';
import { AuthenticatedTenantUserResponse } from '../model/authenticated-tenant-user-response.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/api/auth`;

  login(request: LoginRequest): Observable<ApiResponse<AuthTokenResponse>> {
    // Si la petición ya trae un tenantSlug (ingresado en el formulario o en storage pero no inyectado aún), lo enviamos explícitamente en el header
    const headersConfig: { [key: string]: string } = {};
    if (request.tenantSlug) {
      headersConfig['X-Tenant-Slug'] = request.tenantSlug;
    }

    const headers = new HttpHeaders(headersConfig);

    // No enviamos tenantSlug en el body porque la API probablemente no lo espera allí
    const body = {
      email: request.email,
      password: request.password
    };

    return this.http.post<ApiResponse<AuthTokenResponse>>(`${this.API_URL}/login`, body, { headers });
  }

  getMe(): Observable<ApiResponse<AuthenticatedTenantUserResponse>> {
    return this.http.get<ApiResponse<AuthenticatedTenantUserResponse>>(`${this.API_URL}/me`);
  }
}

```

### `app/entities/auth/index.ts`

```typescript
export * from './model/login-request.model';
export * from './model/auth-token-response.model';
export * from './api/auth.service';

```

### `app/entities/auth/model/authenticated-tenant-user-response.model.ts`

```typescript
export interface AuthenticatedTenantUserResponse {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  active: boolean;
  status: string;
  tenantSlug: string;
  roles: string[];
}

```

### `app/entities/auth/model/auth-token-response.model.ts`

```typescript
export interface AuthTokenResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

```

### `app/entities/auth/model/login-request.model.ts`

```typescript
export interface LoginRequest {
  email: string;
  password?: string;
  tenantSlug?: string; // Incluido para poder mandarlo en el header desde el servicio
}

```

### `app/entities/dashboard/api/dashboard.service.ts`

```typescript
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../shared/api';
import { TenantSummaryResponse } from '../model/tenant-summary-response.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/api/dashboard`;

  getTenantSummary(): Observable<ApiResponse<TenantSummaryResponse>> {
    // El interceptor ya se encarga de inyectar el token y el X-Tenant-Slug
    return this.http.get<ApiResponse<TenantSummaryResponse>>(`${this.API_URL}/tenant/summary`);
  }
}

```

### `app/entities/dashboard/index.ts`

```typescript
export * from './model/tenant-summary-response.model';
export * from './api/dashboard.service';

```

### `app/entities/dashboard/model/tenant-summary-response.model.ts`

```typescript
export interface TenantSummaryResponse {
  totalUsers: number;
  maxUsers: number;
  activePlan: string;
  trialDaysLeft: number;
}

```

### `app/entities/fx/api/fx.service.ts`

```typescript
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/api';
import {
  FxExchangeRateResponse,
  OperationFeeResponse,
  CreateFxExchangeRateRequest,
  UpdateFxExchangeRateRequest,
  CreateOperationFeeRequest,
  UpdateOperationFeeRequest
} from '../model/fx.model';

@Injectable({
  providedIn: 'root'
})
export class FxService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/api/fx`;

  // Rates
  listRates(): Observable<ApiResponse<FxExchangeRateResponse[]>> {
    return this.http.get<ApiResponse<FxExchangeRateResponse[]>>(`${this.API_URL}/rates`);
  }

  getRate(id: string): Observable<ApiResponse<FxExchangeRateResponse>> {
    return this.http.get<ApiResponse<FxExchangeRateResponse>>(`${this.API_URL}/rates/${id}`);
  }

  createRate(request: CreateFxExchangeRateRequest): Observable<ApiResponse<FxExchangeRateResponse>> {
    return this.http.post<ApiResponse<FxExchangeRateResponse>>(`${this.API_URL}/rates`, request);
  }

  updateRate(id: string, request: UpdateFxExchangeRateRequest): Observable<ApiResponse<FxExchangeRateResponse>> {
    return this.http.put<ApiResponse<FxExchangeRateResponse>>(`${this.API_URL}/rates/${id}`, request);
  }

  deleteRate(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/rates/${id}`);
  }

  // Fees
  listFees(): Observable<ApiResponse<OperationFeeResponse[]>> {
    return this.http.get<ApiResponse<OperationFeeResponse[]>>(`${this.API_URL}/fees`);
  }

  getFee(id: string): Observable<ApiResponse<OperationFeeResponse>> {
    return this.http.get<ApiResponse<OperationFeeResponse>>(`${this.API_URL}/fees/${id}`);
  }

  createFee(request: CreateOperationFeeRequest): Observable<ApiResponse<OperationFeeResponse>> {
    return this.http.post<ApiResponse<OperationFeeResponse>>(`${this.API_URL}/fees`, request);
  }

  updateFee(id: string, request: UpdateOperationFeeRequest): Observable<ApiResponse<OperationFeeResponse>> {
    return this.http.put<ApiResponse<OperationFeeResponse>>(`${this.API_URL}/fees/${id}`, request);
  }

  deleteFee(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/fees/${id}`);
  }
}

```

### `app/entities/fx/index.ts`

```typescript
export * from './model/fx.model';
export * from './api/fx.service';

```

### `app/entities/fx/model/fx.model.ts`

```typescript
export interface FxExchangeRateResponse {
  id: string;
  sourceCurrency: string;
  targetCurrency: string;
  rate: number;
  active: boolean;
  description: string;
  createdAt: string;
  updatedAt: string;
}

export interface OperationFeeResponse {
  id: string;
  operationCode: string;
  feeType: string;
  feeValue: number;
  calculationMode: string;
  active: boolean;
  description: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateFxExchangeRateRequest {
  sourceCurrency: string;
  targetCurrency: string;
  rate: number;
  active: boolean;
  description?: string;
}

export interface UpdateFxExchangeRateRequest {
  sourceCurrency: string;
  targetCurrency: string;
  rate: number;
  active: boolean;
  description?: string;
}

export interface CreateOperationFeeRequest {
  operationCode: string;
  feeType: string;
  feeValue: number;
  calculationMode: string;
  active: boolean;
  description?: string;
}

export interface UpdateOperationFeeRequest {
  operationCode: string;
  feeType: string;
  feeValue: number;
  calculationMode: string;
  active: boolean;
  description?: string;
}

```

### `app/entities/limits/api/limits.service.ts`

```typescript
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/api';
import {
  LimitRuleResponse,
  CreateLimitRuleRequest,
  UpdateLimitRuleRequest,
  LimitEvaluationRequest,
  LimitEvaluationResponse
} from '../model/limits.model';

@Injectable({
  providedIn: 'root'
})
export class LimitsService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/api/limits`;

  listRules(): Observable<ApiResponse<LimitRuleResponse[]>> {
    return this.http.get<ApiResponse<LimitRuleResponse[]>>(`${this.API_URL}/rules`);
  }

  getRule(id: string): Observable<ApiResponse<LimitRuleResponse>> {
    return this.http.get<ApiResponse<LimitRuleResponse>>(`${this.API_URL}/rules/${id}`);
  }

  createRule(request: CreateLimitRuleRequest): Observable<ApiResponse<LimitRuleResponse>> {
    return this.http.post<ApiResponse<LimitRuleResponse>>(`${this.API_URL}/rules`, request);
  }

  updateRule(id: string, request: UpdateLimitRuleRequest): Observable<ApiResponse<LimitRuleResponse>> {
    return this.http.patch<ApiResponse<LimitRuleResponse>>(`${this.API_URL}/rules/${id}`, request);
  }

  deleteRule(id: string): Observable<ApiResponse<LimitRuleResponse>> {
    return this.http.delete<ApiResponse<LimitRuleResponse>>(`${this.API_URL}/rules/${id}`);
  }

  evaluate(request: LimitEvaluationRequest): Observable<ApiResponse<LimitEvaluationResponse>> {
    return this.http.post<ApiResponse<LimitEvaluationResponse>>(`${this.API_URL}/evaluate`, request);
  }
}

```

### `app/entities/limits/index.ts`

```typescript
export * from './model/limits.model';
export * from './api/limits.service';

```

### `app/entities/limits/model/limits.model.ts`

```typescript
export interface LimitRuleResponse {
  id: string;
  code: string;
  name: string;
  description: string;
  limitType: string;
  scopeType: string;
  period: string;
  transactionType: string;
  accountType: string;
  currency: string;
  minAmount: number;
  maxAmount: number;
  maxCount: number;
  active: boolean;
  requireReviewExceed: boolean;
  scopeDescription: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateLimitRuleRequest {
  code: string;
  name: string;
  description?: string;
  limitType: string;
  scopeType: string;
  period: string;
  transactionType?: string;
  accountType?: string;
  currency?: string;
  minAmount?: number;
  maxAmount?: number;
  maxCount?: number;
  active: boolean;
  requireReviewExceed: boolean;
}

export interface UpdateLimitRuleRequest {
  name?: string;
  description?: string;
  minAmount?: number;
  maxAmount?: number;
  maxCount?: number;
  active?: boolean;
  requireReviewExceed?: boolean;
}

export interface LimitEvaluationRequest {
  accountId: string;
  transactionType: string;
  amount: number;
  currency: string;
}

export interface LimitEvaluationResponse {
  evaluationId: string;
  accountId: string;
  transactionType: string;
  amount: number;
  currency: string;
  allowed: boolean;
  reason: string;
  requiresReview: boolean;
  ruleChecks: any[];
}

```

### `app/entities/platform/api/platform.service.ts`

```typescript
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface PlatformPlan {
  id: string;
  code: string;
  name: string;
  description: string;
  maxUsers: number;
  maxRoles: number;
  planType: 'DEMO' | 'PAID';
  trialDays: number | null;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreatePlanRequest {
  code: string;
  name: string;
  planType: 'DEMO' | 'PAID';
  description: string;
  maxUsers: number;
  maxRoles: number;
  trialDays?: number | null;
}

export interface AuditEvent {
  id: string;
  actorSubject: string;
  eventType: string;
  resourceType: string;
  resourceId: string;
  eventDetails: string;
  createdAt: string;
}


export interface PlatformSuperadmin {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface PlatformTenant {
  id: string;
  name: string;
  slug: string;
  schemaName: string;
  status: string;
  planId: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateTenantRequest {
  name: string;
  slug: string;
  planCode: string;
}
export interface PlatformSubscription {
  id: string;
  tenantId: string;
  tenantName: string;
  tenantSlug: string;
  planId: string;
  planCode: string;
  planName: string;
  planType: string;
  maxUsers: number;
  maxRoles: number;
  status: string;
  trial: boolean;
  currentSubscription: boolean;
  startedAt: string;
  expiresAt: string;
  remainingDays: number;
  createdAt: string;
  updatedAt: string;
}

export interface AssignSubscriptionRequest {
  tenantId: string;
  planCode: string;
  overrideTrialDays?: number;
}

@Injectable({ providedIn: 'root' })
export class PlatformService {
  private http = inject(HttpClient);
  private baseUrl = environment.apiUrl;

  login(credentials: { email: string; password: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/platform/auth/login`, credentials);
  }

  getMe(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/platform/auth/me`);
  }

  logout(): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/platform/auth/logout`, {});
  }

  changePassword(data: { currentPassword: string; newPassword: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/platform/auth/change-password`, data);
  }

 
 getTenants(): Observable<{ success: boolean; data: PlatformTenant[]; message: string }> {
    return this.http.get<{ success: boolean; data: PlatformTenant[]; message: string }>(
      `${this.baseUrl}/api/platform/tenants`
    );
  }

  getTenantById(id: string): Observable<{ success: boolean; data: PlatformTenant; message: string }> {
    return this.http.get<{ success: boolean; data: PlatformTenant; message: string }>(
      `${this.baseUrl}/api/platform/tenants/${id}`
    );
  }

  createTenant(request: CreateTenantRequest): Observable<{ success: boolean; data: PlatformTenant; message: string }> {
    return this.http.post<{ success: boolean; data: PlatformTenant; message: string }>(
      `${this.baseUrl}/api/platform/tenants`,
      request
    );
  }

  activateTenant(id: string): Observable<{ success: boolean; data: PlatformTenant; message: string }> {
    return this.http.patch<{ success: boolean; data: PlatformTenant; message: string }>(
      `${this.baseUrl}/api/platform/tenants/${id}/activate`,
      {}
    );
  }

  deactivateTenant(id: string): Observable<{ success: boolean; data: PlatformTenant; message: string }> {
    return this.http.patch<{ success: boolean; data: PlatformTenant; message: string }>(
      `${this.baseUrl}/api/platform/tenants/${id}/deactivate`,
      {}
    );
  }


   // ============================================================
  // SUBSCRIPTIONS (nuevos)
  // ============================================================
  
  getSubscriptions(): Observable<{ success: boolean; data: PlatformSubscription[]; message: string }> {
    return this.http.get<{ success: boolean; data: PlatformSubscription[]; message: string }>(
      `${this.baseUrl}/api/platform/subscriptions`
    );
  }

  getSubscriptionById(id: string): Observable<{ success: boolean; data: PlatformSubscription; message: string }> {
    return this.http.get<{ success: boolean; data: PlatformSubscription; message: string }>(
      `${this.baseUrl}/api/platform/subscriptions/${id}`
    );
  }

  assignSubscription(request: AssignSubscriptionRequest): Observable<{ success: boolean; data: PlatformSubscription; message: string }> {
    return this.http.post<{ success: boolean; data: PlatformSubscription; message: string }>(
      `${this.baseUrl}/api/platform/subscriptions/assign`,
      request
    );
  }
 
   getCurrentSuperadmin(): Observable<{ success: boolean; data: PlatformSuperadmin; message: string }> {
    return this.http.get<{ success: boolean; data: PlatformSuperadmin; message: string }>(
      `${this.baseUrl}/api/platform/superadmins/me`
    );
  }

  // ============================================================
  // PLANS
  // ============================================================
  
  getPlans(): Observable<{ success: boolean; data: PlatformPlan[]; message: string }> {
    return this.http.get<{ success: boolean; data: PlatformPlan[]; message: string }>(
      `${this.baseUrl}/api/platform/plans`
    );
  }

  getPlanById(id: string): Observable<{ success: boolean; data: PlatformPlan; message: string }> {
    return this.http.get<{ success: boolean; data: PlatformPlan; message: string }>(
      `${this.baseUrl}/api/platform/plans/${id}`
    );
  }

  createPlan(request: CreatePlanRequest): Observable<{ success: boolean; data: PlatformPlan; message: string }> {
    return this.http.post<{ success: boolean; data: PlatformPlan; message: string }>(
      `${this.baseUrl}/api/platform/plans`,
      request
    );
  }

  activatePlan(id: string): Observable<{ success: boolean; data: PlatformPlan; message: string }> {
    return this.http.patch<{ success: boolean; data: PlatformPlan; message: string }>(
      `${this.baseUrl}/api/platform/plans/${id}/activate`,
      {}
    );
  }

  deactivatePlan(id: string): Observable<{ success: boolean; data: PlatformPlan; message: string }> {
    return this.http.patch<{ success: boolean; data: PlatformPlan; message: string }>(
      `${this.baseUrl}/api/platform/plans/${id}/deactivate`,
      {}
    );
  }

  // ============================================================
  // AUDIT
  // ============================================================
  
  getAuditEvents(limit: number = 50, offset: number = 0): Observable<{ success: boolean; data: AuditEvent[]; message: string }> {
    return this.http.get<{ success: boolean; data: AuditEvent[]; message: string }>(
      `${this.baseUrl}/api/platform/audit/events?limit=${limit}&offset=${offset}`
    );
  }
}
```

### `app/entities/tenant/api/tenant.service.ts`

```typescript
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../shared/api';
import { PublicSignupRequest } from '../model/public-signup-request.model';
import { PublicSignupResponse } from '../model/public-signup-response.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TenantService {
  private http = inject(HttpClient);
  // Reemplazar con la ruta de tu entorno real
  private readonly API_URL = `${environment.apiUrl}/api/public`; 

  publicSignup(request: PublicSignupRequest): Observable<ApiResponse<PublicSignupResponse>> {
    return this.http.post<ApiResponse<PublicSignupResponse>>(`${this.API_URL}/signup`, request);
  }
}

```

### `app/entities/tenant/index.ts`

```typescript
// export models
export * from './model/public-signup-request.model';
export * from './model/public-signup-response.model';

// export api/services
export * from './api/tenant.service';

```

### `app/entities/tenant/model/public-signup-request.model.ts`

```typescript
export interface PublicSignupRequest {
  companyName: string;
  tenantSlug: string;
  adminEmail: string;
  password?: string;
  firstName: string;
  lastName: string;
}

```

### `app/entities/tenant/model/public-signup-response.model.ts`

```typescript
export interface PublicSignupResponse {
  tenantId: string;
  tenantSlug: string;
  companyName: string;
  adminUserId: string;
  adminEmail: string;
  planCode: string;
  message?: string;
}

```

### `app/entities/transactions/api/transactions.service.ts`

```typescript
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/api';
import {
  TransactionResponse,
  QrTransactionIntentResponse,
  CreateDepositTransactionRequest,
  CreateFeeTransactionRequest,
  CreateHoldTransactionRequest,
  CreateQrTransactionIntentRequest,
  CreatePaymentTransactionRequest,
  CreateAdjustmentTransactionRequest,
  CreateWithdrawalTransactionRequest,
  CreateReleaseTransactionRequest,
  CreateTransferTransactionRequest,
  CreateReversalTransactionRequest,
  CreateRefundTransactionRequest
} from '../model/transactions.model';

@Injectable({
  providedIn: 'root'
})
export class TransactionsService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/api/transactions`;

  // Consultas
  listTransactions(): Observable<ApiResponse<TransactionResponse[]>> {
    return this.http.get<ApiResponse<TransactionResponse[]>>(this.API_URL);
  }

  getTransactionById(id: string): Observable<ApiResponse<TransactionResponse>> {
    return this.http.get<ApiResponse<TransactionResponse>>(`${this.API_URL}/${id}`);
  }

  // Creación de Transacciones
  createDeposit(request: CreateDepositTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/deposits`, request);
  }

  createFee(request: CreateFeeTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/fees`, request);
  }

  createHold(request: CreateHoldTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/holds`, request);
  }

  createQrIntent(request: CreateQrTransactionIntentRequest): Observable<ApiResponse<QrTransactionIntentResponse>> {
    return this.http.post<ApiResponse<QrTransactionIntentResponse>>(`${this.API_URL}/qr/intents`, request);
  }

  createPayment(request: CreatePaymentTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/payments`, request);
  }

  createAdjustment(request: CreateAdjustmentTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/adjustments`, request);
  }

  createWithdrawal(request: CreateWithdrawalTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/withdrawals`, request);
  }

  createRelease(request: CreateReleaseTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/releases`, request);
  }

  createTransfer(request: CreateTransferTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/transfers`, request);
  }

  reverseTransaction(id: string, request: CreateReversalTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/${id}/reversal`, request);
  }

  refundTransaction(id: string, request: CreateRefundTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/${id}/refunds`, request);
  }
}

```

### `app/entities/transactions/index.ts`

```typescript
export * from './model/transactions.model';
export * from './api/transactions.service';

```

### `app/entities/transactions/model/transactions.model.ts`

```typescript
export interface TransactionFxDetailResponse {
  sourceCurrency: string;
  targetCurrency: string;
  exchangeRate: number;
  originalAmount: number;
  convertedAmount: number;
}

export interface TransactionMovementResponse {
  accountId: string;
  accountNumber: string;
  accountDisplayName: string;
  movementType: string;
  amount: number;
  currency: string;
  balanceAfter: number;
}

export interface TransactionResponse {
  id: string;
  type: string;
  status: string;
  channel: string;
  amount: number;
  currency: string;
  sourceAccountId: string;
  sourceAccountNumber: string;
  sourceAccountDisplayName: string;
  targetAccountId: string;
  targetAccountNumber: string;
  targetAccountDisplayName: string;
  externalReference: string;
  idempotencyKey: string;
  description: string;
  failureReason: string;
  requestedByUserId: string;
  approvedByUserId: string;
  processedAt: string;
  createdAt: string;
  updatedAt: string;
  fxDetail?: TransactionFxDetailResponse;
  movements?: TransactionMovementResponse[];
}

export interface QrTransactionIntentResponse {
  id: string;
  targetAccountId: string;
  amount: number;
  currency: string;
  description: string;
  qrPayload: string;
  expiresAt: string;
}

export interface CreateDepositTransactionRequest {
  targetAccountId: string;
  amount: number;
  currency: string;
  description: string;
  channel: string;
  idempotencyKey: string;
  externalReference?: string;
}

export interface CreateFeeTransactionRequest {
  sourceAccountId: string;
  amount: number;
  currency: string;
  description: string;
  feeType: string;
  externalReference?: string;
}

export interface CreateHoldTransactionRequest {
  sourceAccountId: string;
  amount: number;
  currency: string;
  description: string;
  expiresAt?: string;
  externalReference?: string;
}

export interface CreateQrTransactionIntentRequest {
  targetAccountId: string;
  amount: number;
  currency: string;
  description: string;
  expiresInMinutes?: number;
}

export interface CreatePaymentTransactionRequest {
  sourceAccountId: string;
  targetAccountId: string;
  amount: number;
  currency: string;
  description: string;
  idempotencyKey: string;
  paymentReference?: string;
  externalReference?: string;
}

export interface CreateAdjustmentTransactionRequest {
  accountId: string;
  amount: number;
  currency: string;
  description: string;
  adjustmentType: string; // CREDIT or DEBIT
  externalReference?: string;
}

export interface CreateWithdrawalTransactionRequest {
  sourceAccountId: string;
  amount: number;
  currency: string;
  description: string;
  channel: string;
  idempotencyKey: string;
  externalReference?: string;
}

export interface CreateReleaseTransactionRequest {
  sourceAccountId: string;
  holdTransactionId: string;
  description: string;
  externalReference?: string;
}

export interface CreateTransferTransactionRequest {
  sourceAccountId: string;
  targetAccountId: string;
  amount: number;
  currency: string;
  description: string;
  idempotencyKey: string;
  externalReference?: string;
}

export interface CreateReversalTransactionRequest {
  description: string;
  reason: string;
  idempotencyKey: string;
}

export interface CreateRefundTransactionRequest {
  amount: number;
  description: string;
  reason: string;
  idempotencyKey: string;
}

```

### `app/entities/user/api/user.service.ts`

```typescript
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../shared/api';
import { TenantUserResponse } from '../model/tenant-user-response.model';
import { CreateTenantUserRequest } from '../model/create-tenant-user-request.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/api/users`;

  getUsers(): Observable<ApiResponse<TenantUserResponse[]>> {
    return this.http.get<ApiResponse<TenantUserResponse[]>>(this.API_URL);
  }

  getUserById(id: string): Observable<ApiResponse<TenantUserResponse>> {
    return this.http.get<ApiResponse<TenantUserResponse>>(`${this.API_URL}/${id}`);
  }

  createUser(request: CreateTenantUserRequest): Observable<ApiResponse<TenantUserResponse>> {
    return this.http.post<ApiResponse<TenantUserResponse>>(this.API_URL, request);
  }

  updateUser(id: string, request: any): Observable<ApiResponse<TenantUserResponse>> {
    return this.http.put<ApiResponse<TenantUserResponse>>(`${this.API_URL}/${id}`, request);
  }

  activateUser(id: string): Observable<ApiResponse<TenantUserResponse>> {
    return this.http.patch<ApiResponse<TenantUserResponse>>(`${this.API_URL}/${id}/activate`, {});
  }

  deactivateUser(id: string): Observable<ApiResponse<TenantUserResponse>> {
    return this.http.patch<ApiResponse<TenantUserResponse>>(`${this.API_URL}/${id}/deactivate`, {});
  }
}

```

### `app/entities/user/index.ts`

```typescript
export * from './model/tenant-user-response.model';
export * from './model/create-tenant-user-request.model';
export * from './model/update-tenant-user-request.model';
export * from './api/user.service';

```

### `app/entities/user/model/create-tenant-user-request.model.ts`

```typescript
export interface CreateTenantUserRequest {
  email: string;
  password?: string;
  firstName: string;
  lastName: string;
}

```

### `app/entities/user/model/tenant-user-response.model.ts`

```typescript
export interface TenantUserResponse {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  active: boolean;
}

```

### `app/entities/user/model/update-tenant-user-request.model.ts`

```typescript
export interface UpdateTenantUserRequest {
  email: string;
  firstName?: string;
  lastName?: string;
}

```

### `app/features/accounting/application/journal-entry-list.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AccountingService, JournalEntryResponse } from '../../../entities/accounting';

export interface JournalEntryListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: JournalEntryResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class JournalEntryListUseCase {
  private readonly accountingService = inject(AccountingService);

  private readonly state = signal<JournalEntryListState>({
    status: 'idle',
    data: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadEntries(): Promise<void> {
    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.accountingService.listJournalEntries());

      if (response.success && response.data) {
        this.state.set({ 
          status: 'success', 
          data: response.data, 
          error: null 
        });
      } else {
        this.state.set({ 
          status: 'error', 
          data: [], 
          error: response.message || 'No se pudieron cargar los asientos contables' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }
}

```

### `app/features/accounting/application/period-list.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AccountingService, AccountingPeriodResponse } from '../../../entities/accounting';

export interface PeriodListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: AccountingPeriodResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class PeriodListUseCase {
  private readonly accountingService = inject(AccountingService);

  private readonly state = signal<PeriodListState>({
    status: 'idle',
    data: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadPeriods(): Promise<void> {
    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.accountingService.listPeriods());

      if (response.success && response.data) {
        this.state.set({ 
          status: 'success', 
          data: response.data, 
          error: null 
        });
      } else {
        this.state.set({ 
          status: 'error', 
          data: [], 
          error: response.message || 'No se pudieron cargar los períodos' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }
}

```

### `app/features/accounting/index.ts`

```typescript
export * from './application/period-list.usecase';
export * from './application/journal-entry-list.usecase';

```

### `app/features/account-management/application/account-list.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AccountsService, AccountOwnerResponse, CreateAccountRequest, UpdateAccountRequest } from '../../../entities/accounts';

export interface AccountListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: AccountOwnerResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class AccountListUseCase {
  private readonly accountsService = inject(AccountsService);

  private readonly state = signal<AccountListState>({
    status: 'idle',
    data: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadAccounts(): Promise<void> {
    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.accountsService.listAccounts());

      if (response.success && response.data) {
        this.state.set({ 
          status: 'success', 
          data: response.data, 
          error: null 
        });
      } else {
        this.state.set({ 
          status: 'error', 
          data: [], 
          error: response.message || 'No se pudieron cargar las cuentas bancarias' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }

  // Extra methods for account lifecycle (activate, block, freeze) can be added here
  async createAccount(request: CreateAccountRequest): Promise<void> {
    try {
      const response = await firstValueFrom(this.accountsService.createAccount(request));
      if (response.success) {
        await this.loadAccounts(); // reload the list
      } else {
        throw new Error(response.message || 'Error al crear la cuenta');
      }
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }

  async updateAccount(id: string, request: UpdateAccountRequest): Promise<void> {
    try {
      const response = await firstValueFrom(this.accountsService.updateAccount(id, request));
      if (response.success) {
        await this.loadAccounts();
      } else {
        throw new Error(response.message || 'Error al actualizar la cuenta');
      }
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }

  async changeAccountState(
    id: string, 
    action: 'approve' | 'activate' | 'block' | 'freeze' | 'close', 
    reason?: string
  ): Promise<void> {
    try {
      let response;
      switch (action) {
        case 'approve': response = await firstValueFrom(this.accountsService.approveAccount(id)); break;
        case 'activate': response = await firstValueFrom(this.accountsService.activateAccount(id)); break;
        case 'block': response = await firstValueFrom(this.accountsService.blockAccount(id, reason)); break;
        case 'freeze': response = await firstValueFrom(this.accountsService.freezeAccount(id, reason)); break;
        case 'close': response = await firstValueFrom(this.accountsService.closeAccount(id, reason)); break;
      }
      
      if (response.success) {
        await this.loadAccounts(); // Recargar la lista después de cambiar el estado
      } else {
        throw new Error(response.message || 'Error al cambiar el estado de la cuenta');
      }
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }

  async getAccountBalance(id: string) {
    try {
      return await firstValueFrom(this.accountsService.getAccountBalance(id));
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }
}

```

### `app/features/account-management/index.ts`

```typescript
export * from './application/account-list.usecase';
export * from './ui/account-form/account-form.component';

```

### `app/features/account-management/ui/account-form/account-form.component.ts`

```typescript
import { Component, EventEmitter, Input, Output, inject, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LucideAngularModule, X, Save, Building2, UserCircle2 } from 'lucide-angular';
import { CreateAccountRequest, UpdateAccountRequest, AccountOwnerResponse } from '../../../../entities/accounts';
import { TenantUserResponse } from '../../../../entities/user';
import { UserListUseCase } from '../../../user-management';

@Component({
  selector: 'app-account-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <!-- Overlay -->
    <div 
      *ngIf="isOpen" 
      class="fixed inset-0 bg-background/80 backdrop-blur-sm z-40 transition-opacity"
      (click)="close()">
    </div>

    <!-- Slide-over -->
    <div 
      class="fixed inset-y-0 right-0 z-50 w-full max-w-md bg-card shadow-2xl border-l border-border transform transition-transform duration-300 ease-in-out flex flex-col"
      [class.translate-x-0]="isOpen"
      [class.translate-x-full]="!isOpen">
      
      <!-- Header -->
      <div class="flex items-center justify-between p-6 border-b border-border bg-muted/30">
        <div>
          <h2 class="text-xl font-bold text-foreground">
            {{ isEditing ? 'Editar Cuenta' : 'Nueva Cuenta' }}
          </h2>
          <p class="text-sm text-muted-foreground mt-1">
            {{ isEditing ? 'Modifica los detalles de la cuenta.' : 'Crea una nueva cuenta bancaria para un cliente.' }}
          </p>
        </div>
        <button 
          (click)="close()"
          class="p-2 rounded-full hover:bg-muted text-muted-foreground hover:text-foreground transition-colors">
          <lucide-icon name="x" [size]="20"></lucide-icon>
        </button>
      </div>

      <!-- Content -->
      <div class="flex-1 overflow-y-auto p-6">
        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-5">
          
          <!-- Usuario (Sólo creación) -->
          <div *ngIf="!isEditing" class="space-y-2">
            <label class="text-sm font-medium text-foreground">Cliente (Usuario)</label>
            <div class="relative">
              <lucide-icon name="user-circle-2" class="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground"></lucide-icon>
              <select 
                formControlName="userId"
                class="flex h-10 w-full items-center justify-between rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 pl-10">
                <option value="" disabled selected>Selecciona un cliente</option>
                <option *ngFor="let user of userListUseCase.data()" [value]="user.id">
                  {{ user.firstName }} {{ user.lastName }} ({{ user.email }})
                </option>
              </select>
            </div>
            <p *ngIf="form.get('userId')?.invalid && form.get('userId')?.touched" class="text-xs text-destructive">
              El cliente es obligatorio.
            </p>
          </div>

          <!-- Tipo de Cuenta (Sólo creación) -->
          <div *ngIf="!isEditing" class="space-y-2">
            <label class="text-sm font-medium text-foreground">Tipo de Cuenta</label>
            <div class="relative">
              <lucide-icon name="building-2" class="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground"></lucide-icon>
              <select 
                formControlName="accountType"
                class="flex h-10 w-full items-center justify-between rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 pl-10">
                <option value="SAVINGS">Ahorros (SAVINGS)</option>
                <option value="CHECKING">Corriente (CHECKING)</option>
              </select>
            </div>
          </div>

          <!-- Nombre de Cuenta -->
          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground">Nombre de la Cuenta</label>
            <select 
              formControlName="accountName"
              class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50">
              <option value="" disabled selected>Selecciona un nombre</option>
              <option value="MAIN_WALLET">Billetera Principal</option>
              <option value="SAVINGS_ACCOUNT">Cuenta de Ahorros</option>
              <option value="CHECKING_ACCOUNT">Cuenta Corriente</option>
              <option value="SECONDARY_ACCOUNT">Cuenta Secundaria</option>
              <option value="BUSINESS_ACCOUNT">Cuenta de Negocios</option>
              <option value="CREDIT_CARD_ACCOUNT">Tarjeta de Crédito</option>
              <option value="PREPAID_CARD_ACCOUNT">Tarjeta Prepaga</option>
              <option value="LOAN_ACCOUNT">Cuenta de Préstamo</option>
            </select>
            <p *ngIf="form.get('accountName')?.invalid && form.get('accountName')?.touched" class="text-xs text-destructive">
              El nombre es obligatorio.
            </p>
          </div>

          <!-- Moneda (Sólo creación) -->
          <div *ngIf="!isEditing" class="space-y-2">
            <label class="text-sm font-medium text-foreground">Moneda</label>
            <select 
              formControlName="currency"
              class="flex h-10 w-full items-center justify-between rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2">
              <option value="USD">Dólar Estadounidense (USD)</option>
              <option value="BOB">Boliviano (BOB)</option>
              <option value="EUR">Euro (EUR)</option>
            </select>
          </div>

          <!-- Alias Personalizado -->
          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground">Alias Personalizado (Opcional)</label>
            <input 
              type="text" 
              formControlName="customAlias"
              placeholder="Ej. Ahorros Viaje"
              class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50" />
          </div>

          <!-- Cuenta Principal -->
          <div class="flex items-center space-x-2 pt-2">
            <input 
              type="checkbox" 
              formControlName="primary"
              id="primaryAccount"
              class="peer h-4 w-4 shrink-0 rounded-sm border border-primary ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 data-[state=checked]:bg-primary data-[state=checked]:text-primary-foreground" />
            <label for="primaryAccount" class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70 text-foreground">
              Establecer como cuenta principal
            </label>
          </div>
        </form>
      </div>

      <!-- Footer -->
      <div class="p-6 border-t border-border bg-muted/10 flex justify-end gap-3">
        <button 
          type="button" 
          (click)="close()"
          class="inline-flex items-center justify-center rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 border border-input bg-background hover:bg-accent hover:text-accent-foreground h-10 px-4 py-2">
          Cancelar
        </button>
        <button 
          type="button" 
          (click)="onSubmit()"
          [disabled]="form.invalid || isSubmitting"
          class="inline-flex items-center justify-center rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 bg-primary text-primary-foreground hover:bg-primary/90 h-10 px-4 py-2 gap-2 shadow-sm">
          <lucide-icon *ngIf="!isSubmitting" name="save" [size]="16"></lucide-icon>
          <svg *ngIf="isSubmitting" class="animate-spin h-4 w-4 text-primary-foreground" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          {{ isEditing ? 'Actualizar' : 'Crear Cuenta' }}
        </button>
      </div>
    </div>
  `
})
export class AccountFormComponent implements OnInit, OnChanges {
  @Input() isOpen = false;
  @Input() account: AccountOwnerResponse | null = null;
  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<{ request: CreateAccountRequest | UpdateAccountRequest, isEditing: boolean }>();

  public readonly userListUseCase = inject(UserListUseCase);
  private fb = inject(FormBuilder);

  form!: FormGroup;
  isSubmitting = false;

  get isEditing(): boolean {
    return !!this.account;
  }

  ngOnInit() {
    this.initForm();
    if (this.userListUseCase.data().length === 0) {
      this.userListUseCase.loadUsers();
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['account'] || changes['isOpen']) {
      if (this.isOpen) {
        this.initForm();
      }
    }
  }

  initForm() {
    if (this.isEditing && this.account) {
      this.form = this.fb.group({
        accountName: [this.account.accountName, Validators.required],
        customAlias: [this.account.customAlias || ''],
        primary: [this.account.primary || false]
      });
    } else {
      this.form = this.fb.group({
        userId: ['', Validators.required],
        accountName: ['', Validators.required],
        accountType: ['SAVINGS', Validators.required],
        currency: ['USD', Validators.required],
        customAlias: [''],
        primary: [false]
      });
    }
  }

  close() {
    this.isOpen = false;
    this.closed.emit();
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.saved.emit({
      request: this.form.value,
      isEditing: this.isEditing
    });
  }

  setSubmitting(val: boolean) {
    this.isSubmitting = val;
    if (!val) {
      this.close();
    }
  }
}

```

### `app/features/auth/application/login.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { LoginRequest } from '../../../entities/auth/model/login-request.model';
import { AuthService } from '../../../entities/auth/api/auth.service';
import { AuthStorageService } from '../../../shared/lib/storage/auth-storage.service';

export interface LoginState {
  status: 'idle' | 'loading' | 'success' | 'error';
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class LoginUseCase {
  private readonly authService = inject(AuthService);
  private readonly authStorage = inject(AuthStorageService);

  private readonly state = signal<LoginState>({
    status: 'idle',
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly error = computed(() => this.state().error);

  async login(request: LoginRequest): Promise<void> {
    this.state.set({ status: 'loading', error: null });

    try {
      const response = await firstValueFrom(this.authService.login(request));

      if (response.success && response.data) {
        // Almacenar token, refresh y tenant slug en persistencia local centralizada
        this.authStorage.saveToken(response.data.accessToken);
        if (response.data.refreshToken) {
          this.authStorage.saveRefreshToken(response.data.refreshToken);
        }
        if (request.tenantSlug) {
          this.authStorage.saveTenantSlug(request.tenantSlug);
        }

        this.state.set({ status: 'success', error: null });
      } else {
        this.state.set({ status: 'error', error: response.message || 'Error desconocido al iniciar sesión' });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', error: errorMsg });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', error: null });
  }
}

```

### `app/features/auth/index.ts`

```typescript
export * from './application/login.usecase';
export * from './ui/login-form/login-form.component';

```

### `app/features/auth/ui/login-form/login-form.component.html`

```html
<div class="w-full max-w-md mx-auto bg-card rounded-xl shadow-lg border border-border p-8">
  <div class="text-center mb-8">
    <h2 class="text-2xl font-bold text-primary">Iniciar Sesión</h2>
    <p class="text-muted-foreground mt-2">Accede a tu panel de administración.</p>
  </div>

  <!-- Mensaje de Error -->
  <div *ngIf="status === 'error'" class="mb-6 p-4 bg-destructive/10 text-destructive border border-destructive/20 rounded-md">
    {{ error }}
  </div>

  <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="space-y-4">
    
    <div>
      <label for="tenantSlug" class="block text-sm font-medium text-foreground mb-1">Nombre de Organización (Slug)</label>
      <input 
        id="tenantSlug" 
        type="text" 
        formControlName="tenantSlug"
        class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
        [class.border-destructive]="isFieldInvalid('tenantSlug')"
        placeholder="ej: miempresa">
      <span *ngIf="isFieldInvalid('tenantSlug')" class="text-xs text-destructive mt-1">Ingresa el identificador único de tu empresa.</span>
    </div>

    <div>
      <label for="email" class="block text-sm font-medium text-foreground mb-1">Correo Electrónico</label>
      <input 
        id="email" 
        type="email" 
        formControlName="email"
        class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
        [class.border-destructive]="isFieldInvalid('email')"
        placeholder="admin@miempresa.com">
      <span *ngIf="isFieldInvalid('email')" class="text-xs text-destructive mt-1">Ingresa un correo válido.</span>
    </div>

    <div>
      <label for="password" class="block text-sm font-medium text-foreground mb-1">Contraseña</label>
      <div class="relative">
        <input 
          id="password" 
          [type]="showPassword ? 'text' : 'password'" 
          formControlName="password"
          class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors pr-10"
          [class.border-destructive]="isFieldInvalid('password')"
          placeholder="••••••••">
        <button 
          type="button" 
          (click)="togglePasswordVisibility()" 
          class="absolute inset-y-0 right-0 pr-3 flex items-center text-muted-foreground hover:text-foreground transition-colors"
          tabindex="-1"
        >
          <lucide-icon [name]="showPassword ? 'eye-off' : 'eye'" class="h-4 w-4"></lucide-icon>
        </button>
      </div>
      <span *ngIf="isFieldInvalid('password')" class="text-xs text-destructive mt-1">La contraseña es requerida.</span>
    </div>

    <div class="pt-4">
      <button 
        type="submit" 
        [disabled]="status === 'loading'"
        class="w-full bg-primary hover:bg-primary/90 text-primary-foreground font-medium py-2.5 px-4 rounded-md transition-colors disabled:opacity-50 flex justify-center items-center">
        
        <ng-container *ngIf="status !== 'loading'; else loadingSpinner">
          Ingresar
        </ng-container>
        
        <ng-template #loadingSpinner>
          <svg class="animate-spin h-5 w-5 mr-3" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          Autenticando...
        </ng-template>

      </button>
    </div>
    
    <!-- Link to Onboarding -->
    <div class="mt-6 text-center text-sm text-muted-foreground border-t border-border pt-4">
      ¿No tienes una empresa registrada? <br>
      <a routerLink="/onboarding" class="font-medium text-primary hover:underline hover:text-primary/90 transition-colors mt-1 inline-block">
        Regístrate y crea tu entorno
      </a>
    </div>
  </form>
</div>

```

### `app/features/auth/ui/login-form/login-form.component.ts`

```typescript
import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { LoginRequest } from '../../../../entities/auth/model/login-request.model';

@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, LucideAngularModule],
  templateUrl: './login-form.component.html'
})
export class LoginFormComponent {
  @Input() status: 'idle' | 'loading' | 'success' | 'error' = 'idle';
  @Input() error: string | null = null;
  
  @Output() loginSubmit = new EventEmitter<LoginRequest>();

  showPassword = false;

  private readonly fb = inject(FormBuilder);

  loginForm: FormGroup = this.fb.group({
    tenantSlug: ['', [Validators.required, Validators.pattern(/^[a-z0-9-]+$/)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  onSubmit(): void {
    if (this.loginForm.valid && this.status !== 'loading') {
      this.loginSubmit.emit(this.loginForm.value);
    } else {
      this.loginForm.markAllAsTouched();
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.loginForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }
}

```

### `app/features/dashboard/application/summary.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { DashboardService, TenantSummaryResponse } from '../../../entities/dashboard';

export interface SummaryState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: TenantSummaryResponse | null;
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class SummaryUseCase {
  private readonly dashboardService = inject(DashboardService);

  private readonly state = signal<SummaryState>({
    status: 'idle',
    data: null,
    error: null
  });

  // Selectores públicos reactivos
  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadSummary(): Promise<void> {
    this.state.set({ status: 'loading', data: null, error: null });

    try {
      const response = await firstValueFrom(this.dashboardService.getTenantSummary());

      if (response.success && response.data) {
        this.state.set({ status: 'success', data: response.data, error: null });
      } else {
        this.state.set({ 
          status: 'error', 
          data: null, 
          error: response.message || 'No se pudo cargar el resumen' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: null, error: errorMsg });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', data: null, error: null });
  }
}

```

### `app/features/dashboard/index.ts`

```typescript
export * from './application/summary.usecase';
export * from './ui/summary-cards/summary-cards.component';

```

### `app/features/dashboard/ui/summary-cards/summary-cards.component.html`

```html
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6" *ngIf="data">
  
  <!-- Tarjeta Plan Activo -->
  <div class="bg-card rounded-xl border border-border p-6 shadow-sm flex flex-col">
    <div class="flex items-center gap-3 text-muted-foreground mb-4">
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-primary"><path d="m12 14 4-4"/><path d="M3.34 19a10 10 0 1 1 17.32 0"/></svg>
      <h3 class="text-sm font-medium">Plan Actual</h3>
    </div>
    <div class="mt-auto">
      <p class="text-2xl font-bold text-foreground capitalize">{{ data.activePlan }}</p>
    </div>
  </div>

  <!-- Tarjeta Usuarios -->
  <div class="bg-card rounded-xl border border-border p-6 shadow-sm flex flex-col">
    <div class="flex items-center gap-3 text-muted-foreground mb-4">
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-primary"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
      <h3 class="text-sm font-medium">Usuarios Registrados</h3>
    </div>
    <div class="mt-auto flex items-end justify-between">
      <p class="text-2xl font-bold text-foreground">
        {{ data.totalUsers }} <span class="text-lg text-muted-foreground font-normal">/ {{ data.maxUsers }}</span>
      </p>
      <!-- Indicador de capacidad -->
      <div class="w-1/2 bg-muted rounded-full h-1.5 overflow-hidden flex">
        <div class="bg-primary h-full" [style.width.%]="(data.totalUsers / data.maxUsers) * 100"></div>
      </div>
    </div>
  </div>

  <!-- Tarjeta Días Restantes (Trial) -->
  <div class="bg-card rounded-xl border border-border p-6 shadow-sm flex flex-col relative overflow-hidden" 
       [ngClass]="{'border-secondary/50 bg-secondary/5': data.trialDaysLeft > 0}">
    
    <!-- Ribbon si está en trial -->
    <div *ngIf="data.trialDaysLeft > 0" class="absolute top-0 right-0">
      <span class="inline-flex items-center justify-center px-2 py-1 text-xs font-medium leading-none text-secondary-foreground bg-secondary rounded-bl-lg">
        TRIAL
      </span>
    </div>

    <div class="flex items-center gap-3 text-muted-foreground mb-4">
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-primary"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
      <h3 class="text-sm font-medium">Días Restantes</h3>
    </div>
    <div class="mt-auto">
      <p class="text-2xl font-bold text-foreground">
        {{ data.trialDaysLeft }} <span class="text-sm font-normal text-muted-foreground">días</span>
      </p>
    </div>
  </div>

</div>

```

### `app/features/dashboard/ui/summary-cards/summary-cards.component.ts`

```typescript
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TenantSummaryResponse } from '../../../../entities/dashboard';

@Component({
  selector: 'app-summary-cards',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './summary-cards.component.html'
})
export class SummaryCardsComponent {
  // Solo recibe datos (Dumb Component)
  @Input() data: TenantSummaryResponse | null = null;
}

```

### `app/features/fx-management/application/fx-fees-list.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { FxService, OperationFeeResponse } from '../../../entities/fx';

export interface FxFeesListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: OperationFeeResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class FxFeesListUseCase {
  private readonly fxService = inject(FxService);

  private readonly state = signal<FxFeesListState>({
    status: 'idle',
    data: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadFees(): Promise<void> {
    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.fxService.listFees());

      if (response.success && response.data) {
        this.state.set({ 
          status: 'success', 
          data: response.data, 
          error: null 
        });
      } else {
        this.state.set({ 
          status: 'error', 
          data: [], 
          error: response.message || 'No se pudieron cargar las comisiones operativas' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }

  async deleteFee(id: string): Promise<boolean> {
    try {
      await firstValueFrom(this.fxService.deleteFee(id));
      await this.loadFees();
      return true;
    } catch (err) {
      return false;
    }
  }

  async createFee(request: import('../../../entities/fx').CreateOperationFeeRequest): Promise<void> {
    this.state.set({ ...this.state(), status: 'loading', error: null });
    try {
      await firstValueFrom(this.fxService.createFee(request));
      await this.loadFees();
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al crear la comisión';
      this.state.set({ ...this.state(), status: 'error', error: errorMsg });
      throw err;
    }
  }

  async updateFee(id: string, request: import('../../../entities/fx').UpdateOperationFeeRequest): Promise<void> {
    this.state.set({ ...this.state(), status: 'loading', error: null });
    try {
      await firstValueFrom(this.fxService.updateFee(id, request));
      await this.loadFees();
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al actualizar la comisión';
      this.state.set({ ...this.state(), status: 'error', error: errorMsg });
      throw err;
    }
  }
}

```

### `app/features/fx-management/application/fx-rates-list.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { FxService, FxExchangeRateResponse } from '../../../entities/fx';

export interface FxRatesListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: FxExchangeRateResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class FxRatesListUseCase {
  private readonly fxService = inject(FxService);

  private readonly state = signal<FxRatesListState>({
    status: 'idle',
    data: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadRates(): Promise<void> {
    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.fxService.listRates());

      if (response.success && response.data) {
        this.state.set({ 
          status: 'success', 
          data: response.data, 
          error: null 
        });
      } else {
        this.state.set({ 
          status: 'error', 
          data: [], 
          error: response.message || 'No se pudieron cargar los tipos de cambio' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }

  async deleteRate(id: string): Promise<boolean> {
    try {
      await firstValueFrom(this.fxService.deleteRate(id));
      await this.loadRates();
      return true;
    } catch (err) {
      return false;
    }
  }

  async createRate(request: import('../../../entities/fx').CreateFxExchangeRateRequest): Promise<void> {
    this.state.set({ ...this.state(), status: 'loading', error: null });
    try {
      await firstValueFrom(this.fxService.createRate(request));
      await this.loadRates();
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al crear la tasa';
      this.state.set({ ...this.state(), status: 'error', error: errorMsg });
      throw err;
    }
  }

  async updateRate(id: string, request: import('../../../entities/fx').UpdateFxExchangeRateRequest): Promise<void> {
    this.state.set({ ...this.state(), status: 'loading', error: null });
    try {
      await firstValueFrom(this.fxService.updateRate(id, request));
      await this.loadRates();
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al actualizar la tasa';
      this.state.set({ ...this.state(), status: 'error', error: errorMsg });
      throw err;
    }
  }
}

```

### `app/features/fx-management/index.ts`

```typescript
// usecases
export * from './application/fx-rates-list.usecase';
export * from './application/fx-fees-list.usecase';

// ui
export * from './ui/fx-rate-form/fx-rate-form.component';
export * from './ui/fx-fee-form/fx-fee-form.component';

```

### `app/features/fx-management/ui/fx-fee-form/fx-fee-form.component.ts`

```typescript
import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { OperationFeeResponse, CreateOperationFeeRequest, UpdateOperationFeeRequest } from '../../../../entities/fx';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-fx-fee-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <div class="fixed inset-0 z-50 flex items-center justify-end">
      <div class="absolute inset-0 bg-background/80 backdrop-blur-sm animate-in fade-in" (click)="close()"></div>
      
      <div class="relative bg-card w-full max-w-md h-full shadow-2xl border-l border-border flex flex-col animate-in slide-in-from-right duration-300">
        <div class="flex items-center justify-between px-6 py-4 border-b border-border bg-card">
          <h3 class="text-lg font-semibold text-foreground">
            {{ isEditing ? 'Editar' : 'Nueva' }} Comisión
          </h3>
          <button type="button" (click)="close()" class="text-muted-foreground hover:text-foreground transition-colors p-1 rounded-md hover:bg-muted" [disabled]="loading">
            <lucide-icon name="x" [size]="20"></lucide-icon>
          </button>
        </div>

        <div class="flex-1 overflow-y-auto p-6">
          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-6">
            
            <div class="space-y-4">
              <!-- Operation Code -->
              <div>
                <label class="block text-sm font-medium mb-1 text-foreground">Código de Operación</label>
                <select 
                  formControlName="operationCode"
                  class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                  [attr.disabled]="isEditing ? true : null">
                  <option value="">Seleccione...</option>
                  <option value="DEPOSIT">Depósito (DEPOSIT)</option>
                  <option value="WITHDRAWAL">Retiro (WITHDRAWAL)</option>
                  <option value="TRANSFER">Transferencia (TRANSFER)</option>
                  <option value="PAYMENT">Pago (PAYMENT)</option>
                  <option value="CONVERSION">Conversión (CONVERSION)</option>
                </select>
                @if (form.get('operationCode')?.invalid && form.get('operationCode')?.touched) {
                  <span class="text-xs text-destructive mt-1 block">El código de operación es requerido.</span>
                }
              </div>

              <div class="grid grid-cols-2 gap-4">
                <!-- Fee Type -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Tipo de Comisión</label>
                  <select 
                    formControlName="feeType"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                    [attr.disabled]="isEditing ? true : null">
                    <option value="">Seleccione...</option>
                    <option value="NONE">Sin Comisión (NONE)</option>
                    <option value="FIXED">Fija (Monto exacto)</option>
                    <option value="PERCENTAGE">Porcentaje (%)</option>
                  </select>
                  @if (form.get('feeType')?.invalid && form.get('feeType')?.touched) {
                    <span class="text-xs text-destructive mt-1 block">Requerido.</span>
                  }
                </div>

                <!-- Fee Value -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Valor (Monto / %)</label>
                  <input 
                    type="number" 
                    step="0.01"
                    formControlName="feeValue"
                    placeholder="0.00"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow">
                  @if (form.get('feeValue')?.invalid && form.get('feeValue')?.touched) {
                    <span class="text-xs text-destructive mt-1 block">Mayor o igual a 0.</span>
                  }
                </div>
              </div>

              <!-- Calculation Mode -->
              <div>
                <label class="block text-sm font-medium mb-1 text-foreground">Modo de Cálculo</label>
                <select 
                  formControlName="calculationMode"
                  class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow">
                  <option value="">Seleccione...</option>
                  <option value="SEPARATE">Separado (SEPARATE - Se suma al monto)</option>
                  <option value="INCLUDED">Incluido (INCLUDED - Se deduce del monto)</option>
                </select>
                @if (form.get('calculationMode')?.invalid && form.get('calculationMode')?.touched) {
                  <span class="text-xs text-destructive mt-1 block">El modo es requerido.</span>
                }
              </div>

              <!-- Description -->
              <div>
                <label class="block text-sm font-medium mb-1 text-foreground">Descripción (Opcional)</label>
                <textarea 
                  formControlName="description"
                  rows="2"
                  placeholder="Información adicional..."
                  class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow resize-none"></textarea>
              </div>

              <!-- Active Toggle -->
              <div class="flex items-center gap-2 pt-2">
                <input 
                  type="checkbox" 
                  id="activeFee" 
                  formControlName="active"
                  class="h-4 w-4 rounded border-border text-primary focus:ring-primary/50 bg-background accent-primary">
                <label for="activeFee" class="text-sm font-medium text-foreground cursor-pointer">
                  Comisión Activa
                </label>
              </div>
            </div>

            @if (form.hasError('zeroFeeRequired')) {
              <div class="text-xs text-destructive mt-4 p-3 bg-destructive/10 rounded-md border border-destructive/20 flex gap-2">
                <lucide-icon name="x" [size]="16" class="shrink-0"></lucide-icon>
                <span>Si el tipo de comisión es 'Sin Comisión', el valor debe ser exactamente 0.</span>
              </div>
            }

            <div class="pt-4 flex justify-end gap-3 border-t border-border mt-6">
              <button 
                type="button" 
                (click)="close()"
                class="px-4 py-2 border border-border bg-background hover:bg-muted text-foreground rounded-md text-sm font-medium transition-colors"
                [disabled]="loading">
                Cancelar
              </button>
              
              <button 
                type="submit" 
                class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2"
                [disabled]="form.invalid || loading">
                @if (loading) {
                  <svg class="animate-spin h-4 w-4 text-primary-foreground" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Guardando...
                } @else {
                  <lucide-icon name="save" [size]="16"></lucide-icon>
                  {{ isEditing ? 'Guardar Cambios' : 'Crear Comisión' }}
                }
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `
})
export class FxFeeFormComponent implements OnInit {
  @Input() fee: OperationFeeResponse | null = null;
  @Input() loading = false;
  
  @Output() formSubmit = new EventEmitter<{ type: 'create' | 'update', data: any }>();
  @Output() cancel = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  
  form!: FormGroup;
  isEditing = false;

  ngOnInit() {
    this.isEditing = !!this.fee;
    
    this.form = this.fb.group({
      operationCode: [{ value: this.fee?.operationCode || '', disabled: this.isEditing }, [Validators.required]],
      feeType: [{ value: this.fee?.feeType || '', disabled: this.isEditing }, [Validators.required]],
      feeValue: [this.fee?.feeValue ?? null, [Validators.required, Validators.min(0)]],
      calculationMode: [this.fee?.calculationMode || '', [Validators.required]],
      description: [this.fee?.description || ''],
      active: [this.fee?.active ?? true]
    }, { validators: this.feeZeroValidator });
  }

  private feeZeroValidator(group: FormGroup) {
    const feeType = group.get('feeType')?.value;
    const feeValue = group.get('feeValue')?.value;
    
    if (feeType === 'NONE' && feeValue !== 0) {
      return { zeroFeeRequired: true };
    }
    return null;
  }

  onSubmit() {
    if (this.form.invalid) return;

    const formValues = this.form.getRawValue();

    if (this.isEditing) {
      const updateReq: UpdateOperationFeeRequest = {
        operationCode: formValues.operationCode,
        feeType: formValues.feeType,
        feeValue: formValues.feeValue,
        calculationMode: formValues.calculationMode,
        active: formValues.active,
        description: formValues.description
      };
      this.formSubmit.emit({ type: 'update', data: updateReq });
    } else {
      const createReq: CreateOperationFeeRequest = {
        operationCode: formValues.operationCode,
        feeType: formValues.feeType,
        feeValue: formValues.feeValue,
        calculationMode: formValues.calculationMode,
        active: formValues.active,
        description: formValues.description
      };
      this.formSubmit.emit({ type: 'create', data: createReq });
    }
  }

  close() {
    if (!this.loading) {
      this.cancel.emit();
    }
  }
}

```

### `app/features/fx-management/ui/fx-rate-form/fx-rate-form.component.ts`

```typescript
import { Component, EventEmitter, Input, Output, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FxExchangeRateResponse, CreateFxExchangeRateRequest, UpdateFxExchangeRateRequest } from '../../../../entities/fx';
import { LucideAngularModule, X, Save } from 'lucide-angular';

@Component({
  selector: 'app-fx-rate-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <div class="fixed inset-0 z-50 flex items-center justify-end">
      <!-- Backdrop -->
      <div class="absolute inset-0 bg-background/80 backdrop-blur-sm animate-in fade-in" (click)="close()"></div>
      
      <!-- Slide-over Panel -->
      <div class="relative bg-card w-full max-w-md h-full shadow-2xl border-l border-border flex flex-col animate-in slide-in-from-right duration-300">
        
        <div class="flex items-center justify-between px-6 py-4 border-b border-border bg-card">
          <h3 class="text-lg font-semibold text-foreground">
            {{ isEditing ? 'Editar' : 'Nueva' }} Tasa de Cambio
          </h3>
          <button type="button" (click)="close()" class="text-muted-foreground hover:text-foreground transition-colors p-1 rounded-md hover:bg-muted" [disabled]="loading">
            <lucide-icon name="x" [size]="20"></lucide-icon>
          </button>
        </div>

        <div class="flex-1 overflow-y-auto p-6">
          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-6">
            
            <div class="space-y-4">
              <!-- Source Currency -->
              <div>
                <label class="block text-sm font-medium mb-1 text-foreground">Moneda Origen</label>
                <select 
                  formControlName="sourceCurrency"
                  class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                  [attr.disabled]="isEditing ? true : null">
                  <option value="">Seleccione...</option>
                  <option value="USD">USD - Dólar Estadounidense</option>
                  <option value="BOB">BOB - Boliviano</option>
                  <option value="EUR">EUR - Euro</option>
                  <option value="USDT">USDT - Tether</option>
                </select>
                @if (form.get('sourceCurrency')?.invalid && form.get('sourceCurrency')?.touched) {
                  <span class="text-xs text-destructive mt-1 block">La moneda origen es requerida.</span>
                }
              </div>

              <!-- Target Currency -->
              <div>
                <label class="block text-sm font-medium mb-1 text-foreground">Moneda Destino</label>
                <select 
                  formControlName="targetCurrency"
                  class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                  [attr.disabled]="isEditing ? true : null">
                  <option value="">Seleccione...</option>
                  <option value="USD">USD - Dólar Estadounidense</option>
                  <option value="BOB">BOB - Boliviano</option>
                  <option value="EUR">EUR - Euro</option>
                  <option value="USDT">USDT - Tether</option>
                </select>
                @if (form.get('targetCurrency')?.invalid && form.get('targetCurrency')?.touched) {
                  <span class="text-xs text-destructive mt-1 block">La moneda destino es requerida.</span>
                }
              </div>

              <!-- Rate -->
              <div>
                <label class="block text-sm font-medium mb-1 text-foreground">Tasa (Rate)</label>
                <input 
                  type="number" 
                  step="0.000001"
                  formControlName="rate"
                  placeholder="0.000000"
                  class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow">
                @if (form.get('rate')?.invalid && form.get('rate')?.touched) {
                  <span class="text-xs text-destructive mt-1 block">La tasa es requerida y debe ser mayor a 0.</span>
                }
              </div>

              <!-- Description -->
              <div>
                <label class="block text-sm font-medium mb-1 text-foreground">Descripción (Opcional)</label>
                <textarea 
                  formControlName="description"
                  rows="3"
                  placeholder="Información adicional sobre la tasa..."
                  class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow resize-none"></textarea>
              </div>

              <!-- Active Toggle -->
              <div class="flex items-center gap-2 pt-2">
                <input 
                  type="checkbox" 
                  id="active" 
                  formControlName="active"
                  class="h-4 w-4 rounded border-border text-primary focus:ring-primary/50 bg-background accent-primary">
                <label for="active" class="text-sm font-medium text-foreground cursor-pointer">
                  Tasa Activa
                </label>
              </div>
            </div>

            @if (form.hasError('selfExchangeRate')) {
              <div class="text-xs text-destructive mt-4 p-3 bg-destructive/10 rounded-md border border-destructive/20 flex gap-2">
                <lucide-icon name="x" [size]="16" class="shrink-0"></lucide-icon>
                <span>Si la moneda origen y destino son la misma, la tasa debe ser exactamente 1.</span>
              </div>
            }

            <div class="pt-4 flex justify-end gap-3 border-t border-border mt-6">
              <button 
                type="button" 
                (click)="close()"
                class="px-4 py-2 border border-border bg-background hover:bg-muted text-foreground rounded-md text-sm font-medium transition-colors"
                [disabled]="loading">
                Cancelar
              </button>
              
              <button 
                type="submit" 
                class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2"
                [disabled]="form.invalid || loading">
                @if (loading) {
                  <svg class="animate-spin h-4 w-4 text-primary-foreground" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Guardando...
                } @else {
                  <lucide-icon name="save" [size]="16"></lucide-icon>
                  {{ isEditing ? 'Guardar Cambios' : 'Crear Tasa' }}
                }
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `
})
export class FxRateFormComponent implements OnInit {
  @Input() rate: FxExchangeRateResponse | null = null;
  @Input() loading = false;
  
  @Output() formSubmit = new EventEmitter<{ type: 'create' | 'update', data: any }>();
  @Output() cancel = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  
  form!: FormGroup;
  isEditing = false;

  ngOnInit() {
    this.isEditing = !!this.rate;
    
    this.form = this.fb.group({
      sourceCurrency: [{ value: this.rate?.sourceCurrency || '', disabled: this.isEditing }, [Validators.required]],
      targetCurrency: [{ value: this.rate?.targetCurrency || '', disabled: this.isEditing }, [Validators.required]],
      rate: [this.rate?.rate || null, [Validators.required, Validators.min(0.000001)]],
      description: [this.rate?.description || ''],
      active: [this.rate?.active ?? true]
    }, { validators: this.selfExchangeRateValidator });
  }

  private selfExchangeRateValidator(group: FormGroup) {
    const source = group.get('sourceCurrency')?.value;
    const target = group.get('targetCurrency')?.value;
    const rate = group.get('rate')?.value;
    
    if (source && target && source === target && rate !== 1) {
      return { selfExchangeRate: true };
    }
    return null;
  }

  onSubmit() {
    if (this.form.invalid) return;

    const formValues = this.form.getRawValue();

    if (this.isEditing) {
      const updateReq: UpdateFxExchangeRateRequest = {
        sourceCurrency: formValues.sourceCurrency,
        targetCurrency: formValues.targetCurrency,
        rate: formValues.rate,
        active: formValues.active,
        description: formValues.description
      };
      this.formSubmit.emit({ type: 'update', data: updateReq });
    } else {
      const createReq: CreateFxExchangeRateRequest = {
        sourceCurrency: formValues.sourceCurrency,
        targetCurrency: formValues.targetCurrency,
        rate: formValues.rate,
        active: formValues.active,
        description: formValues.description
      };
      this.formSubmit.emit({ type: 'create', data: createReq });
    }
  }

  close() {
    if (!this.loading) {
      this.cancel.emit();
    }
  }
}

```

### `app/features/landing/ui/landing-page/landing-page.component.ts`

```typescript
// features/landing/ui/landing-page/landing-page.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule],
  template: `
    <div class="min-h-screen bg-linear-to-br from-slate-950 via-slate-900 to-[#1a3320] flex flex-col">

      <!-- Fondo decorativo con partículas sutiles -->
      <div class="absolute inset-0 overflow-hidden pointer-events-none">
        <div class="absolute top-1/4 left-1/4 w-96 h-96 bg-[#2E7D32]/5 rounded-full blur-3xl"></div>
        <div class="absolute bottom-1/4 right-1/4 w-80 h-80 bg-[#4CAF50]/5 rounded-full blur-3xl"></div>
      </div>

      <!-- Header logo -->
      <header class="relative z-10 flex items-center justify-between px-8 py-6">
        <div class="flex items-center gap-3">
          <div class="h-10 w-10 rounded-lg bg-[#2E7D32] flex items-center justify-center font-bold text-white text-xl shadow-lg">
            P
          </div>
          <span class="font-bold text-2xl tracking-tight text-white">PROSPERA</span>
        </div>
        <div class="text-sm text-slate-400">
          Plataforma SaaS Financiero
        </div>
      </header>

      <!-- Contenido central -->
      <main class="relative z-10 flex-1 flex flex-col items-center justify-center px-4 pb-12">

        <!-- Título principal -->
        <div class="text-center mb-14">
          <div class="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-[#2E7D32]/20 border border-[#2E7D32]/30 text-[#4CAF50] text-sm font-medium mb-6">
            <lucide-icon name="shield" class="h-3.5 w-3.5"></lucide-icon>
            Acceso seguro y encriptado
          </div>
          <h1 class="text-4xl md:text-5xl font-extrabold text-white tracking-tight leading-tight">
            Bienvenido a tu<br>
            <span class="text-transparent bg-clip-text bg-linear-to-r from-[#4CAF50] to-[#81C784]">Panel de Control</span>
          </h1>
          <p class="mt-4 text-slate-400 text-lg max-w-md mx-auto">
            Selecciona el tipo de cuenta con la que deseas acceder al sistema.
          </p>
        </div>

        <!-- Tarjetas de acceso -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 w-full max-w-2xl">

          <!-- Acceso Tenant (Admin) -->
          <a routerLink="/login" class="group relative block rounded-2xl border border-white/10 bg-white/5 backdrop-blur-sm p-8 hover:bg-white/10 hover:border-[#4CAF50]/40 transition-all duration-300 hover:-translate-y-1 hover:shadow-2xl hover:shadow-[#2E7D32]/20 cursor-pointer">
            <!-- Icono -->
            <div class="h-14 w-14 rounded-xl bg-[#2E7D32]/20 border border-[#2E7D32]/30 flex items-center justify-center mb-6 group-hover:bg-[#2E7D32]/30 transition-colors">
              <lucide-icon name="building-2" class="h-7 w-7 text-[#4CAF50]"></lucide-icon>
            </div>
            <!-- Contenido -->
            <h2 class="text-xl font-bold text-white mb-2">Acceso Empresarial</h2>
            <p class="text-slate-400 text-sm leading-relaxed mb-6">
              Para administradores y usuarios de una organización. Gestiona tus cuentas, transacciones y equipo.
            </p>
            <!-- Características -->
            <ul class="space-y-2 mb-8">
              <li class="flex items-center gap-2 text-xs text-slate-400">
                <lucide-icon name="check-circle" class="h-3.5 w-3.5 text-[#4CAF50] shrink-0"></lucide-icon>
                Gestión de usuarios y roles
              </li>
              <li class="flex items-center gap-2 text-xs text-slate-400">
                <lucide-icon name="check-circle" class="h-3.5 w-3.5 text-[#4CAF50] shrink-0"></lucide-icon>
                Panel de transacciones y cuentas
              </li>
              <li class="flex items-center gap-2 text-xs text-slate-400">
                <lucide-icon name="check-circle" class="h-3.5 w-3.5 text-[#4CAF50] shrink-0"></lucide-icon>
                Contabilidad y tipos de cambio
              </li>
            </ul>
            <!-- CTA -->
            <div class="flex items-center gap-2 text-sm font-semibold text-[#4CAF50] group-hover:gap-3 transition-all">
              Iniciar sesión
              <lucide-icon name="arrow-right" class="h-4 w-4"></lucide-icon>
            </div>
          </a>

          <!-- Acceso SuperAdmin (Platform) -->
          <a routerLink="/platform/login" class="group relative block rounded-2xl border border-white/10 bg-white/5 backdrop-blur-sm p-8 hover:bg-white/10 hover:border-purple-500/40 transition-all duration-300 hover:-translate-y-1 hover:shadow-2xl hover:shadow-purple-900/20 cursor-pointer">
            <!-- Icono -->
            <div class="h-14 w-14 rounded-xl bg-purple-900/20 border border-purple-500/20 flex items-center justify-center mb-6 group-hover:bg-purple-900/30 transition-colors">
              <lucide-icon name="layout-dashboard" class="h-7 w-7 text-purple-400"></lucide-icon>
            </div>
            <!-- Badge -->
            <div class="absolute top-6 right-6">
              <span class="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-purple-900/40 text-purple-300 border border-purple-500/30">
                SuperAdmin
              </span>
            </div>
            <!-- Contenido -->
            <h2 class="text-xl font-bold text-white mb-2">Administración Global</h2>
            <p class="text-slate-400 text-sm leading-relaxed mb-6">
              Acceso exclusivo para administradores de la plataforma. Gestiona tenants, planes y suscripciones.
            </p>
            <!-- Características -->
            <ul class="space-y-2 mb-8">
              <li class="flex items-center gap-2 text-xs text-slate-400">
                <lucide-icon name="check-circle" class="h-3.5 w-3.5 text-purple-400 shrink-0"></lucide-icon>
                Panel de métricas global
              </li>
              <li class="flex items-center gap-2 text-xs text-slate-400">
                <lucide-icon name="check-circle" class="h-3.5 w-3.5 text-purple-400 shrink-0"></lucide-icon>
                Gestión de planes y suscripciones
              </li>
              <li class="flex items-center gap-2 text-xs text-slate-400">
                <lucide-icon name="check-circle" class="h-3.5 w-3.5 text-purple-400 shrink-0"></lucide-icon>
                Control de organizaciones (tenants)
              </li>
            </ul>
            <!-- CTA -->
            <div class="flex items-center gap-2 text-sm font-semibold text-purple-400 group-hover:gap-3 transition-all">
              Acceder como SuperAdmin
              <lucide-icon name="arrow-right" class="h-4 w-4"></lucide-icon>
            </div>
          </a>

        </div>

        <!-- Link a registro público -->
        <p class="mt-10 text-sm text-slate-500">
          ¿Eres nuevo? 
          <a routerLink="/onboarding" class="text-[#4CAF50] font-medium hover:underline hover:text-[#81C784] transition-colors">
            Regístrate y crea tu organización gratis
          </a>
        </p>

      </main>

      <!-- Footer -->
      <footer class="relative z-10 text-center pb-6 text-xs text-slate-600">
        © 2025 PROSPERA — Plataforma Financiera SaaS
      </footer>

    </div>
  `
})
export class LandingPageComponent {}
// import { Component } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { RouterModule } from '@angular/router';
// import { LucideAngularModule } from 'lucide-angular';

// @Component({
//   selector: 'app-landing-page',
//   standalone: true,
//   imports: [CommonModule, RouterModule, LucideAngularModule],
//   template: `
//     <div class="min-h-screen bg-white">
//       <!-- Navbar -->
//       <nav class="bg-white border-b border-[#C8E6C9] sticky top-0 z-50">
//         <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
//           <div class="flex justify-between items-center h-16">
//             <div class="flex items-center">
//               <div class="h-8 w-8 rounded-md bg-[#2E7D32] flex items-center justify-center">
//                 <span class="text-white font-bold text-lg">F</span>
//               </div>
//               <span class="ml-2 text-xl font-bold text-[#2E7D32]">Finance System</span>
//             </div>
//             <div class="flex items-center gap-4">
//               <a routerLink="/login" class="text-[#2E7D32] hover:text-[#4CAF50] px-3 py-2 text-sm font-medium">
//                 Iniciar Sesión
//               </a>
//               <a routerLink="/onboarding" class="bg-[#2E7D32] text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-[#4CAF50] transition-colors">
//                 Crear Cuenta
//               </a>
//             </div>
//           </div>
//         </div>
//       </nav>

//       <!-- Hero -->
//       <div class="relative bg-[#F1F8E9] overflow-hidden">
//         <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24">
//           <div class="text-center">
//             <h1 class="text-4xl md:text-6xl font-bold text-[#2E7D32] tracking-tight">
//               Gestión financiera
//               <span class="text-[#4CAF50]">a nivel corporativo</span>
//             </h1>
//             <p class="mt-6 text-lg text-[#666666] max-w-2xl mx-auto">
//               Centraliza la administración de finanzas, usuarios y roles en una sola plataforma.
//               Diseñado con arquitectura robusta para escalar junto a los objetivos de tu empresa.
//             </p>
//             <div class="mt-10 flex flex-col sm:flex-row justify-center gap-4">
//               <a routerLink="/onboarding" class="bg-[#2E7D32] text-white px-6 py-3 rounded-lg font-medium hover:bg-[#4CAF50] transition-colors">
//                 Comenzar prueba gratis
//               </a>
//               <a routerLink="/login" class="border border-[#C8E6C9] text-[#2E7D32] px-6 py-3 rounded-lg font-medium hover:bg-[#F1F8E9] transition-colors">
//                 Iniciar sesión
//               </a>
//             </div>
//           </div>
//         </div>
//       </div>

//       <!-- Features -->
//       <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
//         <div class="text-center mb-12">
//           <h2 class="text-3xl font-bold text-[#2E7D32]">Características</h2>
//           <p class="mt-4 text-[#666666]">Todo lo que necesitas para administrar tu negocio</p>
//         </div>
//         <div class="grid md:grid-cols-3 gap-8">
//           <div class="bg-white p-6 rounded-xl border border-[#C8E6C9] shadow-sm">
//             <div class="h-12 w-12 rounded-lg bg-[#E8F5E9] flex items-center justify-center mb-4">
//               <lucide-icon name="users" class="h-6 w-6 text-[#2E7D32]"></lucide-icon>
//             </div>
//             <h3 class="text-lg font-semibold text-[#2E7D32]">Gestión de Usuarios</h3>
//             <p class="mt-2 text-[#666666] text-sm">Administra usuarios con control granular de permisos y roles.</p>
//           </div>
//           <div class="bg-white p-6 rounded-xl border border-[#C8E6C9] shadow-sm">
//             <div class="h-12 w-12 rounded-lg bg-[#E8F5E9] flex items-center justify-center mb-4">
//               <lucide-icon name="shield" class="h-6 w-6 text-[#2E7D32]"></lucide-icon>
//             </div>
//             <h3 class="text-lg font-semibold text-[#2E7D32]">Control de Acceso</h3>
//             <p class="mt-2 text-[#666666] text-sm">Define roles personalizados y permisos específicos.</p>
//           </div>
//           <div class="bg-white p-6 rounded-xl border border-[#C8E6C9] shadow-sm">
//             <div class="h-12 w-12 rounded-lg bg-[#E8F5E9] flex items-center justify-center mb-4">
//               <lucide-icon name="building" class="h-6 w-6 text-[#2E7D32]"></lucide-icon>
//             </div>
//             <h3 class="text-lg font-semibold text-[#2E7D32]">Multitenencia</h3>
//             <p class="mt-2 text-[#666666] text-sm">Soporta múltiples empresas con aislamiento de datos total.</p>
//           </div>
//         </div>
//       </div>

//       <!-- Footer -->
//       <footer class="bg-[#F1F8E9] border-t border-[#C8E6C9] py-8">
//         <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
//           <p class="text-[#666666] text-sm">© 2024 Finance System. Todos los derechos reservados.</p>
//           <div class="mt-4">
//             <a routerLink="/platform/login" class="text-xs text-[#4CAF50] hover:text-[#2E7D32]">Acceso SuperAdmin</a>
//           </div>
//         </div>
//       </footer>
//     </div>
//   `
// })
// export class LandingPageComponent {}
```

### `app/features/limits-management/application/limit-rules-list.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { LimitsService, LimitRuleResponse, CreateLimitRuleRequest, UpdateLimitRuleRequest } from '../../../entities/limits';

export interface LimitRulesListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: LimitRuleResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class LimitRulesListUseCase {
  private readonly limitsService = inject(LimitsService);

  private readonly state = signal<LimitRulesListState>({
    status: 'idle',
    data: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadRules(): Promise<void> {
    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.limitsService.listRules());

      if (response.success && response.data) {
        this.state.set({ 
          status: 'success', 
          data: response.data, 
          error: null 
        });
      } else {
        this.state.set({ 
          status: 'error', 
          data: [], 
          error: response.message || 'No se pudieron cargar las reglas de límite' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }

  async createRule(request: CreateLimitRuleRequest): Promise<void> {
    try {
      await firstValueFrom(this.limitsService.createRule(request));
      await this.loadRules();
    } catch (err: any) {
      throw err;
    }
  }

  async updateRule(id: string, request: UpdateLimitRuleRequest): Promise<void> {
    try {
      await firstValueFrom(this.limitsService.updateRule(id, request));
      await this.loadRules();
    } catch (err: any) {
      throw err;
    }
  }

  async deleteRule(id: string): Promise<boolean> {
    try {
      await firstValueFrom(this.limitsService.deleteRule(id));
      await this.loadRules();
      return true;
    } catch (err) {
      return false;
    }
  }
}

```

### `app/features/limits-management/index.ts`

```typescript
export * from './application/limit-rules-list.usecase';
export * from './ui/limit-rule-form/limit-rule-form.component';

```

### `app/features/limits-management/ui/limit-rule-form/limit-rule-form.component.ts`

```typescript
import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LimitRuleResponse, CreateLimitRuleRequest, UpdateLimitRuleRequest } from '../../../../entities/limits';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-limit-rule-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <div class="fixed inset-0 z-50 flex items-center justify-end">
      <!-- Backdrop -->
      <div class="absolute inset-0 bg-background/80 backdrop-blur-sm animate-in fade-in" (click)="close()"></div>
      
      <!-- Slide-over Panel -->
      <div class="relative bg-card w-full max-w-2xl h-full shadow-2xl border-l border-border flex flex-col animate-in slide-in-from-right duration-300">
        
        <div class="flex items-center justify-between px-6 py-4 border-b border-border bg-card">
          <h3 class="text-lg font-semibold text-foreground">
            {{ isEditing ? 'Editar' : 'Nueva' }} Regla de Límite
          </h3>
          <button type="button" (click)="close()" class="text-muted-foreground hover:text-foreground transition-colors p-1 rounded-md hover:bg-muted" [disabled]="loading">
            <lucide-icon name="x" [size]="20"></lucide-icon>
          </button>
        </div>

        <div class="flex-1 overflow-y-auto p-6">
          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-6">
            
            <div class="space-y-6">
              
              <div class="grid grid-cols-2 gap-4">
                <!-- Nombre -->
                <div class="col-span-2 sm:col-span-1">
                  <label class="block text-sm font-medium mb-1 text-foreground">Nombre de Regla</label>
                  <input 
                    type="text" 
                    formControlName="name"
                    placeholder="ej. Límite Diario Transferencias"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow">
                  @if (form.get('name')?.invalid && form.get('name')?.touched) {
                    <span class="text-xs text-destructive mt-1 block">El nombre es requerido.</span>
                  }
                </div>

                <!-- Código -->
                <div class="col-span-2 sm:col-span-1">
                  <label class="block text-sm font-medium mb-1 text-foreground">Código Único</label>
                  <input 
                    type="text" 
                    formControlName="code"
                    placeholder="ej. LMT-D-TRF"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow uppercase disabled:opacity-50"
                    [attr.disabled]="isEditing ? true : null">
                  @if (form.get('code')?.invalid && form.get('code')?.touched) {
                    <span class="text-xs text-destructive mt-1 block">El código es requerido.</span>
                  }
                </div>
              </div>

              <!-- Descripción -->
              <div>
                <label class="block text-sm font-medium mb-1 text-foreground">Descripción (Opcional)</label>
                <textarea 
                  formControlName="description"
                  rows="2"
                  placeholder="Detalles de la regla..."
                  class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow resize-none"></textarea>
              </div>

              <div class="h-px w-full bg-border"></div>

              <div class="grid grid-cols-2 gap-4">
                <!-- Tipo de Regla -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Tipo de Regla (Limit Type)</label>
                  <select 
                    formControlName="limitType"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                    [attr.disabled]="isEditing ? true : null">
                    <option value="">Seleccione...</option>
                    <option value="PER_TRANSACTION_AMOUNT">Monto por Transacción</option>
                    <option value="DAILY_AMOUNT">Monto Diario Acumulado</option>
                    <option value="MONTHLY_AMOUNT">Monto Mensual Acumulado</option>
                    <option value="DAILY_COUNT">Cantidad Diaria de Tx</option>
                    <option value="MONTHLY_COUNT">Cantidad Mensual de Tx</option>
                    <option value="MIN_AMOUNT">Monto Mínimo</option>
                    <option value="MAX_BALANCE">Balance Máximo</option>
                  </select>
                  @if (form.get('limitType')?.invalid && form.get('limitType')?.touched) {
                    <span class="text-xs text-destructive mt-1 block">Requerido.</span>
                  }
                </div>

                <!-- Alcance -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Alcance (Scope)</label>
                  <select 
                    formControlName="scopeType"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                    [attr.disabled]="isEditing ? true : null">
                    <option value="">Seleccione...</option>
                    <option value="TENANT">Global (Tenant)</option>
                    <option value="ACCOUNT_TYPE">Por Tipo de Cuenta</option>
                    <option value="TRANSACTION_TYPE">Por Tipo de Transacción</option>
                    <option value="USER">Por Usuario</option>
                    <option value="ACCOUNT">Por Cuenta Específica</option>
                  </select>
                  @if (form.get('scopeType')?.invalid && form.get('scopeType')?.touched) {
                    <span class="text-xs text-destructive mt-1 block">Requerido.</span>
                  }
                </div>

                <!-- Periodo -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Periodo de Evaluación</label>
                  <select 
                    formControlName="period"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                    [attr.disabled]="isEditing ? true : null">
                    <option value="">Seleccione...</option>
                    <option value="TRANSACTION">Por Transacción</option>
                    <option value="DAILY">Diario</option>
                    <option value="MONTHLY">Mensual</option>
                  </select>
                  @if (form.get('period')?.invalid && form.get('period')?.touched) {
                    <span class="text-xs text-destructive mt-1 block">Requerido.</span>
                  }
                </div>

                <!-- Moneda (Opcional) -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Moneda (Opcional)</label>
                  <select 
                    formControlName="currency"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                    [attr.disabled]="isEditing ? true : null">
                    <option value="">Cualquiera</option>
                    <option value="USD">USD - Dólar</option>
                    <option value="BOB">BOB - Boliviano</option>
                    <option value="EUR">EUR - Euro</option>
                    <option value="USDT">USDT - Tether</option>
                  </select>
                </div>

                <!-- Tipo Transacción (Opcional) -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Tipo de Transacción (Opcional)</label>
                  <select 
                    formControlName="transactionType"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                    [attr.disabled]="isEditing ? true : null">
                    <option value="">Cualquiera</option>
                    <option value="DEPOSIT">Depósito</option>
                    <option value="WITHDRAWAL">Retiro</option>
                    <option value="TRANSFER">Transferencia</option>
                    <option value="PAYMENT">Pago</option>
                    <option value="CONVERSION">Conversión</option>
                  </select>
                </div>

                <!-- Tipo de Cuenta (Opcional) -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Tipo de Cuenta (Opcional)</label>
                  <select 
                    formControlName="accountType"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                    [attr.disabled]="isEditing ? true : null">
                    <option value="">Cualquiera</option>
                    <option value="SAVINGS">Ahorros</option>
                    <option value="CHECKING">Corriente</option>
                    <option value="INVESTMENT">Inversión</option>
                  </select>
                </div>
              </div>

              <div class="h-px w-full bg-border"></div>

              <div class="grid grid-cols-3 gap-4">
                <!-- Monto Mínimo -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Monto Mín.</label>
                  <input 
                    type="number" 
                    step="0.01"
                    formControlName="minAmount"
                    placeholder="0.00"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow">
                </div>

                <!-- Monto Máximo -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Monto Máx.</label>
                  <input 
                    type="number" 
                    step="0.01"
                    formControlName="maxAmount"
                    placeholder="0.00"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow">
                </div>

                <!-- Conteo Máximo -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Conteo Máx. (Tx)</label>
                  <input 
                    type="number" 
                    formControlName="maxCount"
                    placeholder="0"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow">
                </div>
              </div>

              @if (form.hasError('invalidRange')) {
                <div class="text-xs text-destructive p-3 bg-destructive/10 rounded-md border border-destructive/20 flex gap-2">
                  <lucide-icon name="x" [size]="16" class="shrink-0"></lucide-icon>
                  <span>El monto máximo debe ser mayor o igual al monto mínimo.</span>
                </div>
              }

              <div class="flex flex-col gap-3 pt-2">
                <!-- Active Toggle -->
                <div class="flex items-center gap-2">
                  <input 
                    type="checkbox" 
                    id="activeRule" 
                    formControlName="active"
                    class="h-4 w-4 rounded border-border text-primary focus:ring-primary/50 bg-background accent-primary">
                  <label for="activeRule" class="text-sm font-medium text-foreground cursor-pointer">
                    Regla Activa
                  </label>
                </div>
                
                <!-- Require Review Toggle -->
                <div class="flex items-center gap-2">
                  <input 
                    type="checkbox" 
                    id="requireReview" 
                    formControlName="requireReviewExceed"
                    class="h-4 w-4 rounded border-border text-primary focus:ring-primary/50 bg-background accent-primary">
                  <label for="requireReview" class="text-sm font-medium text-foreground cursor-pointer">
                    Requerir Revisión Manual al Exceder
                  </label>
                </div>
              </div>
              
            </div>

            <div class="pt-4 flex justify-end gap-3 border-t border-border mt-6">
              <button 
                type="button" 
                (click)="close()"
                class="px-4 py-2 border border-border bg-background hover:bg-muted text-foreground rounded-md text-sm font-medium transition-colors"
                [disabled]="loading">
                Cancelar
              </button>
              
              <button 
                type="submit" 
                class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2"
                [disabled]="form.invalid || loading">
                @if (loading) {
                  <svg class="animate-spin h-4 w-4 text-primary-foreground" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Guardando...
                } @else {
                  <lucide-icon name="save" [size]="16"></lucide-icon>
                  {{ isEditing ? 'Guardar Cambios' : 'Crear Regla' }}
                }
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `
})
export class LimitRuleFormComponent implements OnInit {
  @Input() rule: LimitRuleResponse | null = null;
  @Input() loading = false;
  
  @Output() formSubmit = new EventEmitter<{ type: 'create' | 'update', data: any }>();
  @Output() cancel = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  
  form!: FormGroup;
  isEditing = false;

  ngOnInit() {
    this.isEditing = !!this.rule;
    
    this.form = this.fb.group({
      code: [{ value: this.rule?.code || '', disabled: this.isEditing }, [Validators.required]],
      name: [this.rule?.name || '', [Validators.required]],
      description: [this.rule?.description || ''],
      limitType: [{ value: this.rule?.limitType || '', disabled: this.isEditing }, [Validators.required]],
      scopeType: [{ value: this.rule?.scopeType || '', disabled: this.isEditing }, [Validators.required]],
      period: [{ value: this.rule?.period || '', disabled: this.isEditing }, [Validators.required]],
      transactionType: [{ value: this.rule?.transactionType || '', disabled: this.isEditing }],
      accountType: [{ value: this.rule?.accountType || '', disabled: this.isEditing }],
      currency: [{ value: this.rule?.currency || '', disabled: this.isEditing }],
      minAmount: [this.rule?.minAmount ?? null, [Validators.min(0)]],
      maxAmount: [this.rule?.maxAmount ?? null, [Validators.min(0)]],
      maxCount: [this.rule?.maxCount ?? null, [Validators.min(1)]],
      active: [this.rule?.active ?? true],
      requireReviewExceed: [this.rule?.requireReviewExceed ?? false]
    }, { validators: this.rangeValidator });
  }

  private rangeValidator(group: FormGroup) {
    const min = group.get('minAmount')?.value;
    const max = group.get('maxAmount')?.value;
    if (min !== null && max !== null && min > max) {
      return { invalidRange: true };
    }
    return null;
  }

  onSubmit() {
    if (this.form.invalid) return;

    const formValues = this.form.getRawValue();

    if (this.isEditing) {
      const updateReq: UpdateLimitRuleRequest = {
        name: formValues.name,
        description: formValues.description,
        minAmount: formValues.minAmount,
        maxAmount: formValues.maxAmount,
        maxCount: formValues.maxCount,
        active: formValues.active,
        requireReviewExceed: formValues.requireReviewExceed
      };
      this.formSubmit.emit({ type: 'update', data: updateReq });
    } else {
      const createReq: CreateLimitRuleRequest = {
        code: formValues.code.toUpperCase(),
        name: formValues.name,
        description: formValues.description,
        limitType: formValues.limitType,
        scopeType: formValues.scopeType,
        period: formValues.period,
        transactionType: formValues.transactionType || undefined,
        accountType: formValues.accountType || undefined,
        currency: formValues.currency || undefined,
        minAmount: formValues.minAmount,
        maxAmount: formValues.maxAmount,
        maxCount: formValues.maxCount,
        active: formValues.active,
        requireReviewExceed: formValues.requireReviewExceed
      };
      this.formSubmit.emit({ type: 'create', data: createReq });
    }
  }

  close() {
    if (!this.loading) {
      this.cancel.emit();
    }
  }
}

```

### `app/features/onboarding/application/signup.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { PublicSignupRequest } from '../../../entities/tenant/model/public-signup-request.model';
import { TenantService } from '../../../entities/tenant/api/tenant.service';

export interface SignupState {
  loading: boolean;
  error: string | null;
  success: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class SignupUseCase {
  private readonly tenantService = inject(TenantService);

  // Estado privado
  private readonly state = signal<SignupState>({
    loading: false,
    error: null,
    success: false
  });

  // Selectores públicos (Readonly)
  readonly loading = computed(() => this.state().loading);
  readonly error = computed(() => this.state().error);
  readonly success = computed(() => this.state().success);

  signup(request: PublicSignupRequest): void {
    // Actualizar estado a loading
    this.state.set({ loading: true, error: null, success: false });

    this.tenantService.publicSignup(request).subscribe({
      next: (response) => {
        if (response.success) {
          this.state.set({ loading: false, error: null, success: true });
        } else {
          this.state.set({ loading: false, error: response.message || 'Error desconocido', success: false });
        }
      },
      error: (err) => {
        const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
        this.state.set({ loading: false, error: errorMsg, success: false });
      }
    });
  }

  resetState(): void {
    this.state.set({ loading: false, error: null, success: false });
  }
}

```

### `app/features/onboarding/index.ts`

```typescript
export * from './application/signup.usecase';
export * from './ui/signup-form/signup-form.component';

```

### `app/features/onboarding/ui/signup-form/signup-form.component.html`

```html
<div class="w-full max-w-lg mx-auto bg-card rounded-xl shadow-lg border border-border p-8 relative">
  
  <a routerLink="/login" class="absolute top-4 left-4 text-muted-foreground hover:text-primary transition-colors flex items-center gap-1 text-sm font-medium">
    <lucide-icon name="arrow-left" class="h-4 w-4"></lucide-icon>
    Volver
  </a>

  <div class="text-center mb-8 mt-4">
    <h2 class="text-2xl font-bold text-primary flex items-center justify-center gap-2">
      <lucide-icon name="building-2" class="h-6 w-6"></lucide-icon>
      Crea tu cuenta empresarial
    </h2>
    <p class="text-muted-foreground mt-2">Empieza a gestionar tus finanzas hoy mismo.</p>
  </div>

  <!-- Mensaje de Éxito -->
  <div *ngIf="success" @fadeSlideInOut class="mb-6 p-4 bg-accent text-accent-foreground rounded-md border border-primary/20 flex flex-col items-center gap-3 text-center">
    <lucide-icon name="arrow-right" class="h-10 w-10 text-primary animate-bounce"></lucide-icon>
    <div>
      <h3 class="font-bold text-lg">¡Cuenta creada exitosamente!</h3>
      <p class="text-sm mt-1">Revisa tu correo electrónico para continuar.</p>
    </div>
    <a routerLink="/login" class="mt-4 bg-primary text-primary-foreground px-4 py-2 rounded-md font-medium text-sm hover:bg-primary/90 transition-colors">
      Ir al Login
    </a>
  </div>

  <!-- Mensaje de Error -->
  <div *ngIf="error" @fadeSlideInOut class="mb-6 p-4 bg-destructive/10 text-destructive border border-destructive/20 rounded-md">
    {{ error }}
  </div>

  <!-- Formulario -->
  <form *ngIf="!success" [formGroup]="signupForm" (ngSubmit)="onSubmit()" class="space-y-4">
    
    <!-- Datos de Empresa -->
    <div class="space-y-4 pb-4 border-b border-border">
      <h3 class="text-sm font-semibold text-foreground uppercase tracking-wider flex items-center gap-2">
        <lucide-icon name="building-2" class="h-4 w-4 text-muted-foreground"></lucide-icon>
        Datos de la Empresa
      </h3>
      
      <div>
        <label for="companyName" class="block text-sm font-medium text-foreground mb-1">Nombre de la Empresa</label>
        <input 
          id="companyName" 
          type="text" 
          formControlName="companyName"
          class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
          [class.border-destructive]="isFieldInvalid('companyName')"
          placeholder="Ej: FinanCruz Ltda">
        <span *ngIf="isFieldInvalid('companyName')" class="text-xs text-destructive mt-1">El nombre es requerido (mínimo 3 caracteres).</span>
      </div>

      <div>
        <label for="tenantSlug" class="block text-sm font-medium text-foreground mb-1">Identificador único (Slug)</label>
        <div class="flex items-center">
          <span class="px-3 py-2 bg-muted text-muted-foreground border border-r-0 border-input rounded-l-md text-sm">app.com/</span>
          <input 
            id="tenantSlug" 
            type="text" 
            formControlName="tenantSlug"
            class="w-full px-3 py-2 border border-input rounded-r-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
            [class.border-destructive]="isFieldInvalid('tenantSlug')"
            placeholder="financruz">
        </div>
        <span *ngIf="isFieldInvalid('tenantSlug')" class="text-xs text-destructive mt-1">Solo letras minúsculas y números (ej: miempresa).</span>
      </div>
    </div>

    <!-- Datos del Administrador -->
    <div class="space-y-4 pt-2">
      <h3 class="text-sm font-semibold text-foreground uppercase tracking-wider flex items-center gap-2">
        <lucide-icon name="user" class="h-4 w-4 text-muted-foreground"></lucide-icon>
        Administrador Principal
      </h3>
      
      <div class="grid grid-cols-2 gap-4">
        <div>
          <label for="firstName" class="block text-sm font-medium text-foreground mb-1">Nombre</label>
          <input 
            id="firstName" 
            type="text" 
            formControlName="firstName"
            class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
            [class.border-destructive]="isFieldInvalid('firstName')"
            placeholder="Carlos">
        </div>
        <div>
          <label for="lastName" class="block text-sm font-medium text-foreground mb-1">Apellido</label>
          <input 
            id="lastName" 
            type="text" 
            formControlName="lastName"
            class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
            [class.border-destructive]="isFieldInvalid('lastName')"
            placeholder="Rojas">
        </div>
      </div>

      <div>
        <label for="adminEmail" class="block text-sm font-medium text-foreground mb-1">Correo Electrónico</label>
        <input 
          id="adminEmail" 
          type="email" 
          formControlName="adminEmail"
          class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
          [class.border-destructive]="isFieldInvalid('adminEmail')"
          placeholder="admin@financruz.com">
        <span *ngIf="isFieldInvalid('adminEmail')" class="text-xs text-destructive mt-1">Ingresa un correo válido.</span>
      </div>

      <div>
        <label for="password" class="block text-sm font-medium text-foreground mb-1 flex items-center gap-1">
          Contraseña
        </label>
        <input 
          id="password" 
          type="password" 
          formControlName="password"
          class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
          [class.border-destructive]="isFieldInvalid('password')"
          placeholder="••••••••">
        <span *ngIf="isFieldInvalid('password')" class="text-xs text-destructive mt-1">La contraseña debe tener al menos 8 caracteres.</span>
      </div>
    </div>

    <div class="pt-4">
      <button 
        type="submit" 
        [disabled]="loading"
        class="w-full bg-primary hover:bg-primary/90 text-primary-foreground font-medium py-2.5 px-4 rounded-md transition-colors disabled:opacity-50 flex justify-center items-center">
        
        <ng-container *ngIf="!loading; else loadingSpinner">
          Crear Cuenta
        </ng-container>
        
        <ng-template #loadingSpinner>
          <svg class="animate-spin h-5 w-5 mr-3" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          Procesando...
        </ng-template>

      </button>
    </div>
  </form>
</div>

```

### `app/features/onboarding/ui/signup-form/signup-form.component.ts`

```typescript
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { trigger, transition, style, animate } from '@angular/animations';
import { PublicSignupRequest } from '../../../../entities/tenant/model/public-signup-request.model';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-signup-form',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    RouterModule, 
    LucideAngularModule,
  ],
  templateUrl: './signup-form.component.html',
  animations: [
    trigger('fadeSlideInOut', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-10px)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ]),
      transition(':leave', [
        animate('300ms ease-in', style({ opacity: 0, transform: 'translateY(-10px)' }))
      ])
    ])
  ]
})
export class SignupFormComponent {
  @Input() loading = false;
  @Input() error: string | null = null;
  @Input() success = false;
  
  @Output() formSubmit = new EventEmitter<PublicSignupRequest>();

  signupForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.signupForm = this.fb.group({
      companyName: ['', [Validators.required, Validators.minLength(3)]],
      tenantSlug: ['', [Validators.required, Validators.pattern(/^[a-z0-9-]+$/)]],
      adminEmail: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.signupForm.valid && !this.loading) {
      this.formSubmit.emit(this.signupForm.value);
    } else {
      this.signupForm.markAllAsTouched();
    }
  }

  // Helpers para la UI
  isFieldInvalid(fieldName: string): boolean {
    const field = this.signupForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }
}

```

### `app/features/platform/application/platform-audit-list.usecase.ts`

```typescript
// features/platform/application/platform-audit-list.usecase.ts
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { PlatformService, AuditEvent } from '../../../entities/platform/api/platform.service';

export interface PlatformAuditListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  events: AuditEvent[];  // ✅ Asegurar que sea AuditEvent[]
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class PlatformAuditListUseCase {
  private platformService = inject(PlatformService);

  private state = signal<PlatformAuditListState>({
    status: 'idle',
    events: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly events = computed(() => this.state().events);  // ✅ Esto devuelve AuditEvent[]
  readonly error = computed(() => this.state().error);

  async loadAuditEvents(limit: number = 50): Promise<void> {
    this.state.set({ status: 'loading', events: [], error: null });

    try {
      const response = await firstValueFrom(this.platformService.getAuditEvents(limit));
      if (response.success && response.data) {
        this.state.set({ status: 'success', events: response.data, error: null });
      } else {
        this.state.set({ status: 'error', events: [], error: response.message });
      }
    } catch (err: any) {
      this.state.set({ status: 'error', events: [], error: err.message || 'Error al cargar auditoría' });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', events: [], error: null });
  }
}
```

### `app/features/platform/application/platform-login.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { PlatformService } from '../../../entities/platform/api/platform.service';
import { PlatformStorageService } from '../lib/platform-storage.service';

export interface PlatformLoginState {
  status: 'idle' | 'loading' | 'success' | 'error';
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class PlatformLoginUseCase {
  private readonly platformService = inject(PlatformService);
  private readonly platformStorage = inject(PlatformStorageService);

  private readonly state = signal<PlatformLoginState>({ status: 'idle', error: null });

  readonly status = computed(() => this.state().status);
  readonly error = computed(() => this.state().error);

  async login(credentials: { email: string; password: string }): Promise<void> {
    this.state.set({ status: 'loading', error: null });

    try {
      const response = await firstValueFrom(this.platformService.login(credentials));

      if (response.success && response.data) {
        this.platformStorage.saveToken(response.data.accessToken);
        if (response.data.refreshToken) {
          this.platformStorage.saveRefreshToken(response.data.refreshToken);
        }
        this.state.set({ status: 'success', error: null });
      } else {
        this.state.set({ status: 'error', error: response.message || 'Error al iniciar sesión' });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', error: errorMsg });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', error: null });
  }
}
```

### `app/features/platform/application/platform-logout.usecase.ts`

_Binary or non-text file omitted._

- MIME type: `inode/x-empty`
- Size: `0` bytes

### `app/features/platform/application/platform-me.usecase.ts`

_Binary or non-text file omitted._

- MIME type: `inode/x-empty`
- Size: `0` bytes

### `app/features/platform/application/platform-plan-activate.usecase.ts`

```typescript
// features/platform/application/platform-plan-activate.usecase.ts
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { PlatformService } from '../../../entities/platform/api/platform.service';

export interface PlatformPlanActivateState {
  status: 'idle' | 'loading' | 'success' | 'error';
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class PlatformPlanActivateUseCase {
  private platformService = inject(PlatformService);

  private state = signal<PlatformPlanActivateState>({
    status: 'idle',
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly error = computed(() => this.state().error);

  async activatePlan(id: string): Promise<boolean> {
    this.state.set({ status: 'loading', error: null });

    try {
      const response = await firstValueFrom(this.platformService.activatePlan(id));
      if (response.success) {
        this.state.set({ status: 'success', error: null });
        return true;
      } else {
        this.state.set({ status: 'error', error: response.message });
        return false;
      }
    } catch (err: any) {
      this.state.set({ status: 'error', error: err.message || 'Error al activar plan' });
      return false;
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', error: null });
  }
}
```

### `app/features/platform/application/platform-plan-create.usecase.ts`

```typescript
// features/platform/application/platform-plan-create.usecase.ts
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { PlatformService, CreatePlanRequest, PlatformPlan } from '../../../entities/platform/api/platform.service';

export interface PlatformPlanCreateState {
  status: 'idle' | 'loading' | 'success' | 'error';
  createdPlan: PlatformPlan | null;
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class PlatformPlanCreateUseCase {
  private platformService = inject(PlatformService);

  private state = signal<PlatformPlanCreateState>({
    status: 'idle',
    createdPlan: null,
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly createdPlan = computed(() => this.state().createdPlan);
  readonly error = computed(() => this.state().error);

  async createPlan(request: CreatePlanRequest): Promise<boolean> {
    this.state.set({ status: 'loading', createdPlan: null, error: null });

    try {
      const response = await firstValueFrom(this.platformService.createPlan(request));
      if (response.success) {
        this.state.set({ status: 'success', createdPlan: response.data, error: null });
        return true;
      } else {
        this.state.set({ status: 'error', createdPlan: null, error: response.message });
        return false;
      }
    } catch (err: any) {
      this.state.set({ status: 'error', createdPlan: null, error: err.message || 'Error al crear plan' });
      return false;
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', createdPlan: null, error: null });
  }
}
```

### `app/features/platform/application/platform-plan-deactivate.usecase.ts`

```typescript
// features/platform/application/platform-plan-deactivate.usecase.ts
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { PlatformService } from '../../../entities/platform/api/platform.service';

export interface PlatformPlanDeactivateState {
  status: 'idle' | 'loading' | 'success' | 'error';
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class PlatformPlanDeactivateUseCase {
  private platformService = inject(PlatformService);

  private state = signal<PlatformPlanDeactivateState>({
    status: 'idle',
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly error = computed(() => this.state().error);

  async deactivatePlan(id: string): Promise<boolean> {
    this.state.set({ status: 'loading', error: null });

    try {
      const response = await firstValueFrom(this.platformService.deactivatePlan(id));
      if (response.success) {
        this.state.set({ status: 'success', error: null });
        return true;
      } else {
        this.state.set({ status: 'error', error: response.message });
        return false;
      }
    } catch (err: any) {
      this.state.set({ status: 'error', error: err.message || 'Error al desactivar plan' });
      return false;
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', error: null });
  }
}
```

### `app/features/platform/application/platform-plan-list.usecase.ts`

```typescript
// features/platform/application/platform-plan-list.usecase.ts
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { PlatformService, PlatformPlan } from '../../../entities/platform/api/platform.service';

export interface PlatformPlanListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  plans: PlatformPlan[];
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class PlatformPlanListUseCase {
  private platformService = inject(PlatformService);

  private state = signal<PlatformPlanListState>({
    status: 'idle',
    plans: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly plans = computed(() => this.state().plans);
  readonly error = computed(() => this.state().error);

  async loadPlans(): Promise<void> {
    this.state.set({ status: 'loading', plans: [], error: null });

    try {
      const response = await firstValueFrom(this.platformService.getPlans());
      if (response.success) {
        this.state.set({ status: 'success', plans: response.data, error: null });
      } else {
        this.state.set({ status: 'error', plans: [], error: response.message });
      }
    } catch (err: any) {
      this.state.set({ status: 'error', plans: [], error: err.message || 'Error al cargar planes' });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', plans: [], error: null });
  }
}
```

### `app/features/platform/application/platform-subscription-assign.usecase.ts`

```typescript
// features/platform/application/platform-subscription-assign.usecase.ts
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { PlatformService, AssignSubscriptionRequest, PlatformSubscription } from '../../../entities/platform/api/platform.service';

export interface PlatformSubscriptionAssignState {
  status: 'idle' | 'loading' | 'success' | 'error';
  subscription: PlatformSubscription | null;
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class PlatformSubscriptionAssignUseCase {
  private platformService = inject(PlatformService);

  private state = signal<PlatformSubscriptionAssignState>({
    status: 'idle',
    subscription: null,
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly subscription = computed(() => this.state().subscription);
  readonly error = computed(() => this.state().error);

  async assignSubscription(request: AssignSubscriptionRequest): Promise<boolean> {
    this.state.set({ status: 'loading', subscription: null, error: null });

    try {
      const response = await firstValueFrom(this.platformService.assignSubscription(request));
      if (response.success) {
        this.state.set({ status: 'success', subscription: response.data, error: null });
        return true;
      } else {
        this.state.set({ status: 'error', subscription: null, error: response.message });
        return false;
      }
    } catch (err: any) {
      this.state.set({ status: 'error', subscription: null, error: err.message || 'Error al asignar suscripción' });
      return false;
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', subscription: null, error: null });
  }
}
```

### `app/features/platform/application/platform-subscription-get-by-id.usecase.ts`

```typescript
// features/platform/application/platform-subscription-get-by-id.usecase.ts
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { PlatformService, PlatformSubscription } from '../../../entities/platform/api/platform.service';

export interface PlatformSubscriptionDetailState {
  status: 'idle' | 'loading' | 'success' | 'error';
  subscription: PlatformSubscription | null;
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class PlatformSubscriptionGetByIdUseCase {
  private platformService = inject(PlatformService);

  private state = signal<PlatformSubscriptionDetailState>({
    status: 'idle',
    subscription: null,
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly subscription = computed(() => this.state().subscription);
  readonly error = computed(() => this.state().error);

  async loadSubscription(id: string): Promise<void> {
    this.state.set({ status: 'loading', subscription: null, error: null });

    try {
      const response = await firstValueFrom(this.platformService.getSubscriptionById(id));
      if (response.success) {
        this.state.set({ status: 'success', subscription: response.data, error: null });
      } else {
        this.state.set({ status: 'error', subscription: null, error: response.message });
      }
    } catch (err: any) {
      this.state.set({ status: 'error', subscription: null, error: err.message || 'Error al cargar suscripción' });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', subscription: null, error: null });
  }
}
```

### `app/features/platform/application/platform-subscription-list.usecase.ts`

```typescript
// features/platform/application/platform-subscription-list.usecase.ts
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { PlatformService, PlatformSubscription } from '../../../entities/platform/api/platform.service';

export interface PlatformSubscriptionListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  subscriptions: PlatformSubscription[];
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class PlatformSubscriptionListUseCase {
  private platformService = inject(PlatformService);

  private state = signal<PlatformSubscriptionListState>({
    status: 'idle',
    subscriptions: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly subscriptions = computed(() => this.state().subscriptions);
  readonly error = computed(() => this.state().error);

  async loadSubscriptions(): Promise<void> {
    this.state.set({ status: 'loading', subscriptions: [], error: null });

    try {
      const response = await firstValueFrom(this.platformService.getSubscriptions());
      if (response.success) {
        this.state.set({ status: 'success', subscriptions: response.data, error: null });
      } else {
        this.state.set({ status: 'error', subscriptions: [], error: response.message });
      }
    } catch (err: any) {
      this.state.set({ status: 'error', subscriptions: [], error: err.message || 'Error al cargar suscripciones' });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', subscriptions: [], error: null });
  }
}
```

### `app/features/platform/application/platform-superadmin-me.usecase.ts`

```typescript
// features/platform/application/platform-superadmin-me.usecase.ts
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { PlatformService, PlatformSuperadmin } from '../../../entities/platform/api/platform.service';

export interface PlatformSuperadminState {
  status: 'idle' | 'loading' | 'success' | 'error';
  superadmin: PlatformSuperadmin | null;
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class PlatformSuperadminMeUseCase {
  private platformService = inject(PlatformService);

  private state = signal<PlatformSuperadminState>({
    status: 'idle',
    superadmin: null,
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly superadmin = computed(() => this.state().superadmin);
  readonly error = computed(() => this.state().error);

  async loadCurrentSuperadmin(): Promise<void> {
    this.state.set({ status: 'loading', superadmin: null, error: null });

    try {
      const response = await firstValueFrom(this.platformService.getCurrentSuperadmin());
      if (response.success && response.data) {
        this.state.set({ status: 'success', superadmin: response.data, error: null });
      } else {
        this.state.set({ status: 'error', superadmin: null, error: response.message });
      }
    } catch (err: any) {
      this.state.set({ status: 'error', superadmin: null, error: err.message || 'Error al cargar perfil' });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', superadmin: null, error: null });
  }
}
```

### `app/features/platform/application/platform-tenant-activate.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { PlatformService } from '../../../entities/platform/api/platform.service';

export interface PlatformTenantActivateState {
  status: 'idle' | 'loading' | 'success' | 'error';
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class PlatformTenantActivateUseCase {
  private platformService = inject(PlatformService);

  private state = signal<PlatformTenantActivateState>({
    status: 'idle',
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly error = computed(() => this.state().error);

  async activateTenant(id: string): Promise<boolean> {
    this.state.set({ status: 'loading', error: null });

    try {
      const response = await firstValueFrom(this.platformService.activateTenant(id));
      if (response.success) {
        this.state.set({ status: 'success', error: null });
        return true;
      } else {
        this.state.set({ status: 'error', error: response.message });
        return false;
      }
    } catch (err: any) {
      this.state.set({ status: 'error', error: err.message || 'Error al activar tenant' });
      return false;
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', error: null });
  }
}
```

### `app/features/platform/application/platform-tenant-create.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { PlatformService, CreateTenantRequest } from '../../../entities/platform/api/platform.service';

export interface PlatformTenantCreateState {
  status: 'idle' | 'loading' | 'success' | 'error';
  createdTenant: any | null;
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class PlatformTenantCreateUseCase {
  private platformService = inject(PlatformService);

  private state = signal<PlatformTenantCreateState>({
    status: 'idle',
    createdTenant: null,
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly createdTenant = computed(() => this.state().createdTenant);
  readonly error = computed(() => this.state().error);

  async createTenant(request: CreateTenantRequest): Promise<boolean> {
    this.state.set({ status: 'loading', createdTenant: null, error: null });

    try {
      const response = await firstValueFrom(this.platformService.createTenant(request));
      if (response.success) {
        this.state.set({ status: 'success', createdTenant: response.data, error: null });
        return true;
      } else {
        this.state.set({ status: 'error', createdTenant: null, error: response.message });
        return false;
      }
    } catch (err: any) {
      this.state.set({ status: 'error', createdTenant: null, error: err.message || 'Error al crear tenant' });
      return false;
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', createdTenant: null, error: null });
  }
}
```

### `app/features/platform/application/platform-tenant-deactivate.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { PlatformService } from '../../../entities/platform/api/platform.service';

export interface PlatformTenantDeactivateState {
  status: 'idle' | 'loading' | 'success' | 'error';
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class PlatformTenantDeactivateUseCase {
  private platformService = inject(PlatformService);

  private state = signal<PlatformTenantDeactivateState>({
    status: 'idle',
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly error = computed(() => this.state().error);

  async deactivateTenant(id: string): Promise<boolean> {
    this.state.set({ status: 'loading', error: null });

    try {
      const response = await firstValueFrom(this.platformService.deactivateTenant(id));
      if (response.success) {
        this.state.set({ status: 'success', error: null });
        return true;
      } else {
        this.state.set({ status: 'error', error: response.message });
        return false;
      }
    } catch (err: any) {
      this.state.set({ status: 'error', error: err.message || 'Error al desactivar tenant' });
      return false;
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', error: null });
  }
}
```

### `app/features/platform/application/platform-tenant-list.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { PlatformService, PlatformTenant } from '../../../entities/platform/api/platform.service';

export interface PlatformTenantListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  tenants: PlatformTenant[];
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class PlatformTenantListUseCase {
  private platformService = inject(PlatformService);

  private state = signal<PlatformTenantListState>({
    status: 'idle',
    tenants: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly tenants = computed(() => this.state().tenants);
  readonly error = computed(() => this.state().error);

  async loadTenants(): Promise<void> {
    this.state.set({ status: 'loading', tenants: [], error: null });

    try {
      const response = await firstValueFrom(this.platformService.getTenants());
      if (response.success) {
        this.state.set({ status: 'success', tenants: response.data, error: null });
      } else {
        this.state.set({ status: 'error', tenants: [], error: response.message });
      }
    } catch (err: any) {
      this.state.set({ status: 'error', tenants: [], error: err.message || 'Error al cargar tenants' });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', tenants: [], error: null });
  }
}
```

### `app/features/platform/lib/platform.facade.ts`

```typescript
// features/platform/lib/platform.facade.ts
import { Injectable, inject, computed } from '@angular/core';
import { PlatformSuperadminMeUseCase } from '../application/platform-superadmin-me.usecase';

@Injectable({ providedIn: 'root' })
export class PlatformFacade {
  private superadminMeUseCase = inject(PlatformSuperadminMeUseCase);

  readonly currentSuperadmin = computed(() => this.superadminMeUseCase.superadmin());
  readonly isLoading = computed(() => this.superadminMeUseCase.status() === 'loading');

  loadCurrentSuperadmin(): void {
    this.superadminMeUseCase.loadCurrentSuperadmin();
  }
}
```

### `app/features/platform/lib/platform-storage.service.ts`

```typescript
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class PlatformStorageService {
  private readonly TOKEN_KEY = 'platform_access_token';
  private readonly REFRESH_TOKEN_KEY = 'platform_refresh_token';
  private readonly USER_KEY = 'platform_user';

  saveToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  saveRefreshToken(token: string): void {
    localStorage.setItem(this.REFRESH_TOKEN_KEY, token);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  saveUser(user: any): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  getUser(): any | null {
    const user = localStorage.getItem(this.USER_KEY);
    return user ? JSON.parse(user) : null;
  }

  clear(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
  }
}
```

### `app/features/platform/ui/platform-audit-table/platform-audit-table.component.ts`

```typescript
// features/platform/ui/platform-audit-table/platform-audit-table.component.ts
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule } from 'lucide-angular';
import { AuditEvent } from '../../../../entities/platform/api/platform.service';

@Component({
  selector: 'app-platform-audit-table',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <div class="overflow-x-auto">
      @if (isLoading) {
        <div class="space-y-3">
          @for (i of [1,2,3,4,5]; track i) {
            <div class="bg-white rounded-xl border border-[#C8E6C9] p-4 animate-pulse">
              <div class="flex gap-4">
                <div class="h-5 w-24 bg-gray-200 rounded"></div>
                <div class="h-5 w-32 bg-gray-200 rounded"></div>
              </div>
            </div>
          }
        </div>
      } @else {
        <table class="min-w-full divide-y divide-[#C8E6C9]">
          <thead class="bg-[#F1F8E9]">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Fecha</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Actor</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Evento</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Recurso</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-[#C8E6C9]">
            @for (event of events; track event.id) {
              <tr class="hover:bg-[#F1F8E9] transition-colors">
                <td class="px-6 py-4 whitespace-nowrap text-sm text-[#666666]">
                  {{ event.createdAt | date:'medium' }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-medium text-[#2E7D32]">{{ event.actorSubject }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="px-2 py-1 text-xs font-semibold rounded-full bg-[#E8F5E9] text-[#2E7D32]">
                    {{ event.eventType }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-[#666666]">
                  {{ event.resourceType }}: {{ event.resourceId | slice:0:8 }}...
                </td>
              </tr>
            } @empty {
              <tr>
                <td colspan="4" class="px-6 py-10 text-center text-[#666666]">
                  No hay eventos de auditoría
                </td>
              </tr>
            }
          </tbody>
        </table>
      }
    </div>
  `
})
export class PlatformAuditTableComponent {
  @Input() events: AuditEvent[] = [];
  @Input() isLoading = false;
}
```

### `app/features/platform/ui/platform-login-form/platform-login-form.component.ts`

```typescript
import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';

export interface PlatformLoginRequest {
  email: string;
  password: string;
}

@Component({
  selector: 'app-platform-login-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, LucideAngularModule],
  template: `
    <div class="min-h-screen bg-white flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div class="sm:mx-auto sm:w-full sm:max-w-md">
        <div class="text-center">
          <div class="mx-auto h-12 w-12 rounded-xl bg-[#1B5E20] flex items-center justify-center">
            <span class="text-white font-bold text-xl">P</span>
          </div>
          <h2 class="mt-6 text-3xl font-extrabold text-[#1B5E20]">Plataforma</h2>
          <p class="mt-2 text-sm text-[#666666]">Acceso exclusivo para administradores globales</p>
        </div>

        <div class="mt-8 bg-white py-8 px-4 shadow-xl rounded-lg border border-[#C8E6C9] sm:px-10">
          <!-- Mensaje de Error -->
          <div *ngIf="status === 'error'" class="mb-6 p-4 bg-red-50 text-red-700 border border-red-200 rounded-md">
            {{ error }}
          </div>

          <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="space-y-4">
            
            <div>
              <div class="flex justify-between items-center mb-1">
                <label for="email" class="block text-sm font-medium text-[#2E7D32]">Correo Electrónico</label>
                <!-- ✅ Botón para auto-llenar credenciales -->
                <button 
                  type="button"
                  (click)="autoFillCredentials()"
                  class="text-xs text-[#4CAF50] hover:text-[#2E7D32] transition-colors flex items-center gap-1"
                  title="Auto-llenar credenciales de superadmin">
                  <lucide-icon name="wand-2" class="h-3 w-3"></lucide-icon>
                  Auto-llenar
                </button>
              </div>
              <input 
                id="email" 
                type="email" 
                formControlName="email"
                class="w-full px-3 py-2 border border-[#C8E6C9] rounded-md focus:outline-none focus:ring-2 focus:ring-[#4CAF50] focus:border-transparent transition-colors"
                [class.border-red-500]="isFieldInvalid('email')"
                placeholder="superadmin@finance.local">
              <span *ngIf="isFieldInvalid('email')" class="text-xs text-red-600 mt-1">Ingresa un correo válido.</span>
            </div>

            <div>
              <label for="password" class="block text-sm font-medium text-[#2E7D32] mb-1">Contraseña</label>
              <div class="relative">
                <input 
                  id="password" 
                  [type]="showPassword ? 'text' : 'password'" 
                  formControlName="password"
                  class="w-full px-3 py-2 border border-[#C8E6C9] rounded-md focus:outline-none focus:ring-2 focus:ring-[#4CAF50] focus:border-transparent transition-colors pr-10"
                  [class.border-red-500]="isFieldInvalid('password')"
                  placeholder="••••••••">
                <button 
                  type="button" 
                  (click)="togglePasswordVisibility()" 
                  class="absolute inset-y-0 right-0 pr-3 flex items-center text-[#999999] hover:text-[#2E7D32] transition-colors"
                  tabindex="-1"
                >
                  <lucide-icon [name]="showPassword ? 'eye-off' : 'eye'" class="h-4 w-4"></lucide-icon>
                </button>
              </div>
              <span *ngIf="isFieldInvalid('password')" class="text-xs text-red-600 mt-1">La contraseña es requerida (mínimo 8 caracteres).</span>
            </div>

            <div class="pt-4">
              <button 
                type="submit" 
                [disabled]="status === 'loading'"
                class="w-full bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-medium py-2.5 px-4 rounded-md transition-colors disabled:opacity-50 flex justify-center items-center">
                
                <ng-container *ngIf="status !== 'loading'; else loadingSpinner">
                  Acceder como SuperAdmin
                </ng-container>
                
                <ng-template #loadingSpinner>
                  <svg class="animate-spin h-5 w-5 mr-3" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Autenticando...
                </ng-template>

              </button>
            </div>
            
            <!-- Link to return to landing -->
            <div class="mt-6 text-center text-sm text-[#666666] border-t border-[#C8E6C9] pt-4">
              <a routerLink="/" class="font-medium text-[#4CAF50] hover:text-[#2E7D32] transition-colors">
                ← Volver al inicio
              </a>
            </div>
          </form>
        </div>
      </div>
    </div>
  `
})
export class PlatformLoginFormComponent {
  @Input() status: 'idle' | 'loading' | 'success' | 'error' = 'idle';
  @Input() error: string | null = null;
  @Output() loginSubmit = new EventEmitter<PlatformLoginRequest>();

  showPassword = false;
  private readonly fb = inject(FormBuilder);

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  // ✅ Método para auto-llenar credenciales de superadmin
  autoFillCredentials(): void {
    this.loginForm.patchValue({
      email: 'superadmin@finance.local',
      password: 'SuperAdmin123!'
    });
    // Marcar campos como tocados para que no muestren errores de validación
    this.loginForm.get('email')?.markAsTouched();
    this.loginForm.get('password')?.markAsTouched();
  }

  onSubmit(): void {
    if (this.loginForm.valid && this.status !== 'loading') {
      this.loginSubmit.emit(this.loginForm.value as PlatformLoginRequest);
    } else {
      this.loginForm.markAllAsTouched();
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.loginForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }
}
```

### `app/features/platform/ui/platform-plan-form/platform-plan-form.component.ts`

```typescript
// features/platform/ui/platform-plan-form/platform-plan-form.component.ts
import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-platform-plan-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <div class="bg-white rounded-xl border border-[#C8E6C9] p-6">
      <h2 class="text-xl font-bold text-[#2E7D32] mb-4">Crear nuevo plan</h2>
      
      <div *ngIf="error" class="mb-4 p-3 bg-red-50 text-red-700 rounded-lg text-sm">
        {{ error }}
      </div>
      
      <form [formGroup]="planForm" (ngSubmit)="onSubmit()" class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-[#2E7D32] mb-1">Código</label>
            <input type="text" formControlName="code" placeholder="Ej: PREMIUM"
                   class="w-full px-3 py-2 border border-[#C8E6C9] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#4CAF50]">
            <span *ngIf="isFieldInvalid('code')" class="text-xs text-red-600 mt-1">Código requerido</span>
          </div>
          <div>
            <label class="block text-sm font-medium text-[#2E7D32] mb-1">Nombre</label>
            <input type="text" formControlName="name" placeholder="Ej: Premium"
                   class="w-full px-3 py-2 border border-[#C8E6C9] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#4CAF50]">
            <span *ngIf="isFieldInvalid('name')" class="text-xs text-red-600 mt-1">Nombre requerido</span>
          </div>
        </div>
        
        <div>
          <label class="block text-sm font-medium text-[#2E7D32] mb-1">Descripción</label>
          <textarea formControlName="description" rows="2" placeholder="Descripción del plan"
                    class="w-full px-3 py-2 border border-[#C8E6C9] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#4CAF50]"></textarea>
        </div>
        
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-[#2E7D32] mb-1">Tipo de plan</label>
            <select formControlName="planType"
                    class="w-full px-3 py-2 border border-[#C8E6C9] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#4CAF50]">
              <option value="PAID">PAID - Pago</option>
              <option value="DEMO">DEMO - Demostración</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-[#2E7D32] mb-1">Días de prueba (solo DEMO)</label>
            <input type="number" formControlName="trialDays" placeholder="Ej: 10"
                   class="w-full px-3 py-2 border border-[#C8E6C9] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#4CAF50]">
          </div>
        </div>
        
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-[#2E7D32] mb-1">Máx. usuarios</label>
            <input type="number" formControlName="maxUsers" placeholder="Ej: 50"
                   class="w-full px-3 py-2 border border-[#C8E6C9] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#4CAF50]">
            <span *ngIf="isFieldInvalid('maxUsers')" class="text-xs text-red-600 mt-1">Valor requerido</span>
          </div>
          <div>
            <label class="block text-sm font-medium text-[#2E7D32] mb-1">Máx. roles</label>
            <input type="number" formControlName="maxRoles" placeholder="Ej: 20"
                   class="w-full px-3 py-2 border border-[#C8E6C9] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#4CAF50]">
            <span *ngIf="isFieldInvalid('maxRoles')" class="text-xs text-red-600 mt-1">Valor requerido</span>
          </div>
        </div>
        
        <div class="flex justify-end gap-3 pt-4">
          <button type="button" (click)="cancel.emit()" 
                  class="px-4 py-2 border border-[#C8E6C9] text-[#666666] rounded-lg hover:bg-[#F1F8E9] transition-colors">
            Cancelar
          </button>
          <button type="submit" [disabled]="isLoading || planForm.invalid" 
                  class="px-4 py-2 bg-[#2E7D32] text-white rounded-lg hover:bg-[#4CAF50] transition-colors disabled:opacity-50">
            {{ isLoading ? 'Creando...' : 'Crear plan' }}
          </button>
        </div>
      </form>
    </div>
  `
})
export class PlatformPlanFormComponent {
  @Input() isLoading = false;
  @Input() error: string | null = null;
  @Output() submitForm = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<void>();

  private fb = inject(FormBuilder);

  planForm = this.fb.group({
    code: ['', Validators.required],
    name: ['', Validators.required],
    description: [''],
    planType: ['PAID', Validators.required],
    maxUsers: [10, [Validators.required, Validators.min(1)]],
    maxRoles: [5, [Validators.required, Validators.min(1)]],
    trialDays: [null]
  });

  onSubmit(): void {
    if (this.planForm.valid) {
      this.submitForm.emit(this.planForm.value);
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.planForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }
}
```

### `app/features/platform/ui/platform-plan-table/platform-plan-table.component.ts`

```typescript
// features/platform/ui/platform-plan-table/platform-plan-table.component.ts
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LucideAngularModule, Eye, PlayCircle, PauseCircle } from 'lucide-angular';
import { PlatformPlan } from '../../../../entities/platform/api/platform.service';

@Component({
  selector: 'app-platform-plan-table',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule],
  template: `
    <div class="overflow-x-auto">
      @if (isLoading) {
        <div class="space-y-3">
          @for (i of [1,2,3,4,5]; track i) {
            <div class="bg-white rounded-xl border border-[#C8E6C9] p-4 animate-pulse">
              <div class="flex justify-between">
                <div class="space-y-2">
                  <div class="h-5 w-32 bg-gray-200 rounded"></div>
                  <div class="h-4 w-24 bg-gray-200 rounded"></div>
                </div>
                <div class="h-6 w-16 bg-gray-200 rounded-full"></div>
              </div>
            </div>
          }
        </div>
      } @else {
        <table class="min-w-full divide-y divide-[#C8E6C9]">
          <thead class="bg-[#F1F8E9]">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Código</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Nombre</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Tipo</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Usuarios</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Roles</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Estado</th>
              <th class="px-6 py-3 text-right text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Acciones</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-[#C8E6C9]">
            @for (plan of plans; track plan.id) {
              <tr class="hover:bg-[#F1F8E9] transition-colors">
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="font-mono text-sm text-[#2E7D32]">{{ plan.code }}</span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-medium text-[#2E7D32]">{{ plan.name }}</div>
                  <div class="text-xs text-[#666666]">{{ plan.description }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span [class]="plan.planType === 'DEMO' ? 'bg-[#E8F5E9] text-[#2E7D32]' : 'bg-[#E3F2FD] text-[#1565C0]'" 
                        class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full">
                    {{ plan.planType }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-[#666666]">
                  {{ plan.maxUsers }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-[#666666]">
                  {{ plan.maxRoles }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span [class]="plan.active ? 'bg-[#E8F5E9] text-[#2E7D32]' : 'bg-[#FFEBEE] text-[#C62828]'" 
                        class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full">
                    {{ plan.active ? 'ACTIVO' : 'INACTIVO' }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <div class="flex justify-end gap-2">
                    <button (click)="viewDetails.emit(plan.id)" 
                            class="text-[#4CAF50] hover:text-[#2E7D32] transition-colors"
                            title="Ver detalles">
                      <lucide-icon name="eye" class="h-5 w-5"></lucide-icon>
                    </button>
                    @if (plan.active) {
                      <button (click)="deactivate.emit(plan.id)" 
                              class="text-[#FF9800] hover:text-[#F57C00] transition-colors"
                              title="Desactivar">
                        <lucide-icon name="pause-circle" class="h-5 w-5"></lucide-icon>
                      </button>
                    } @else {
                      <button (click)="activate.emit(plan.id)" 
                              class="text-[#4CAF50] hover:text-[#2E7D32] transition-colors"
                              title="Activar">
                        <lucide-icon name="play-circle" class="h-5 w-5"></lucide-icon>
                      </button>
                    }
                  </div>
                </td>
              </tr>
            } @empty {
              <tr>
                <td colspan="7" class="px-6 py-10 text-center text-[#666666]">
                  No hay planes registrados
                </td>
              </tr>
            }
          </tbody>
        </table>
      }
    </div>
  `
})
export class PlatformPlanTableComponent {
  @Input() plans: PlatformPlan[] = [];
  @Input() isLoading = false;
  @Output() viewDetails = new EventEmitter<string>();
  @Output() activate = new EventEmitter<string>();
  @Output() deactivate = new EventEmitter<string>();
}
```

### `app/features/platform/ui/platform-subscription-assign-form/platform-subscription-assign-form.component.ts`

```typescript
// features/platform/ui/platform-subscription-assign-form/platform-subscription-assign-form.component.ts
import { Component, EventEmitter, Input, Output, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LucideAngularModule } from 'lucide-angular';
import { PlatformTenant } from '../../../../entities/platform/api/platform.service';
import { PlatformTenantListUseCase } from '../../application/platform-tenant-list.usecase';

@Component({
  selector: 'app-platform-subscription-assign-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <div class="bg-white rounded-xl border border-[#C8E6C9] p-6">
      <h2 class="text-xl font-bold text-[#2E7D32] mb-4">Asignar/Modificar Suscripción</h2>
      
      <div *ngIf="error" class="mb-4 p-3 bg-red-50 text-red-700 rounded-lg text-sm">
        {{ error }}
      </div>
      
      <form [formGroup]="assignForm" (ngSubmit)="onSubmit()" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-[#2E7D32] mb-1">Tenant</label>
          <select formControlName="tenantId"
                  class="w-full px-3 py-2 border border-[#C8E6C9] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#4CAF50]">
            <option value="">Selecciona un tenant...</option>
            @for (tenant of tenants; track tenant.id) {
              <option [value]="tenant.id">{{ tenant.name }} ({{ tenant.slug }})</option>
            }
          </select>
          <span *ngIf="isFieldInvalid('tenantId')" class="text-xs text-red-600 mt-1">Selecciona un tenant</span>
        </div>
        
        <div>
          <label class="block text-sm font-medium text-[#2E7D32] mb-1">Plan</label>
          <select formControlName="planCode"
                  class="w-full px-3 py-2 border border-[#C8E6C9] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#4CAF50]">
            <option value="DEMO">DEMO - Prueba gratuita (10 días, 5 usuarios, 3 roles)</option>
            <option value="BASIC">BASIC - Básico</option>
            <option value="PRO">PRO - Profesional</option>
            <option value="ENTERPRISE">ENTERPRISE - Empresarial</option>
          </select>
        </div>
        
        <div>
          <label class="block text-sm font-medium text-[#2E7D32] mb-1">Días de prueba (opcional)</label>
          <input type="number" formControlName="overrideTrialDays" 
                 placeholder="Dejar vacío para usar valor por defecto"
                 class="w-full px-3 py-2 border border-[#C8E6C9] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#4CAF50]">
          <p class="text-xs text-[#666666] mt-1">Solo aplica para planes DEMO</p>
        </div>
        
        <div class="flex justify-end gap-3 pt-4">
          <button type="button" (click)="cancel.emit()" 
                  class="px-4 py-2 border border-[#C8E6C9] text-[#666666] rounded-lg hover:bg-[#F1F8E9] transition-colors">
            Cancelar
          </button>
          <button type="submit" [disabled]="isLoading || assignForm.invalid" 
                  class="px-4 py-2 bg-[#2E7D32] text-white rounded-lg hover:bg-[#4CAF50] transition-colors disabled:opacity-50">
            {{ isLoading ? 'Asignando...' : 'Asignar suscripción' }}
          </button>
        </div>
      </form>
    </div>
  `
})
export class PlatformSubscriptionAssignFormComponent implements OnInit {
  @Input() isLoading = false;
  @Input() error: string | null = null;
  @Output() submitForm = new EventEmitter<{ tenantId: string; planCode: string; overrideTrialDays?: number }>();
  @Output() cancel = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  private tenantListUseCase = inject(PlatformTenantListUseCase);

  tenants: PlatformTenant[] = [];

  assignForm = this.fb.group({
    tenantId: ['', Validators.required],
    planCode: ['DEMO', Validators.required],
    overrideTrialDays: [null]
  });

  ngOnInit(): void {
    this.loadTenants();
  }

  async loadTenants(): Promise<void> {
    await this.tenantListUseCase.loadTenants();
    this.tenants = this.tenantListUseCase.tenants();
  }

  onSubmit(): void {
    if (this.assignForm.valid) {
      const value = this.assignForm.value;
      this.submitForm.emit({
        tenantId: value.tenantId!,
        planCode: value.planCode!,
        overrideTrialDays: value.overrideTrialDays || undefined
      });
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.assignForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }
}
```

### `app/features/platform/ui/platform-subscription-table/platform-subscription-table.component.ts`

```typescript
// features/platform/ui/platform-subscription-table/platform-subscription-table.component.ts
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LucideAngularModule, Eye, Calendar, Clock } from 'lucide-angular';
import { PlatformSubscription } from '../../../../entities/platform/api/platform.service';

@Component({
  selector: 'app-platform-subscription-table',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule],
  template: `
    <div class="overflow-x-auto">
      @if (isLoading) {
        <div class="space-y-3">
          @for (i of [1,2,3,4,5]; track i) {
            <div class="bg-white rounded-xl border border-[#C8E6C9] p-4 animate-pulse">
              <div class="flex justify-between">
                <div class="space-y-2">
                  <div class="h-5 w-32 bg-gray-200 rounded"></div>
                  <div class="h-4 w-24 bg-gray-200 rounded"></div>
                </div>
                <div class="h-6 w-16 bg-gray-200 rounded-full"></div>
              </div>
            </div>
          }
        </div>
      } @else {
        <table class="min-w-full divide-y divide-[#C8E6C9]">
          <thead class="bg-[#F1F8E9]">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Tenant</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Plan</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Estado</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Expira</th>
              <th class="px-6 py-3 text-right text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Acciones</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-[#C8E6C9]">
            @for (sub of subscriptions; track sub.id) {
              <tr class="hover:bg-[#F1F8E9] transition-colors">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-medium text-[#2E7D32]">{{ sub.tenantName }}</div>
                  <div class="text-xs text-[#666666]">Slug: {{ sub.tenantSlug }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div>
                    <span class="font-medium text-[#2E7D32]">{{ sub.planName }}</span>
                    <div class="text-xs text-[#666666]">{{ sub.planCode }} - {{ sub.planType }}</div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  @if (sub.trial) {
                    <span class="inline-flex items-center gap-1 px-2 py-1 text-xs font-semibold rounded-full bg-[#E8F5E9] text-[#2E7D32]">
                      <lucide-icon name="clock" class="h-3 w-3"></lucide-icon>
                      Prueba
                    </span>
                  } @else {
                    <span [class]="sub.status === 'ACTIVE' ? 'bg-[#E8F5E9] text-[#2E7D32]' : 'bg-[#FFEBEE] text-[#C62828]'" 
                          class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full">
                      {{ sub.status }}
                    </span>
                  }
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-[#666666]">
                    {{ sub.expiresAt | date:'shortDate' }}
                  </div>
                  <div class="text-xs text-[#FF9800]" *ngIf="sub.remainingDays <= 7 && sub.remainingDays > 0">
                    ⚠️ {{ sub.remainingDays }} días restantes
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <button (click)="viewDetails.emit(sub.id)" 
                          class="text-[#4CAF50] hover:text-[#2E7D32] transition-colors"
                          title="Ver detalles">
                    <lucide-icon name="eye" class="h-5 w-5"></lucide-icon>
                  </button>
                </td>
              </tr>
            } @empty {
              <tr>
                <td colspan="5" class="px-6 py-10 text-center text-[#666666]">
                  No hay suscripciones registradas
                </td>
              </tr>
            }
          </tbody>
        </table>
      }
    </div>
  `
})
export class PlatformSubscriptionTableComponent {
  @Input() subscriptions: PlatformSubscription[] = [];
  @Input() isLoading = false;
  @Output() viewDetails = new EventEmitter<string>();
}
```

### `app/features/platform/ui/platform-tenant-form/platform-tenant-form.component.ts`

```typescript
import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-platform-tenant-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <div class="bg-white rounded-xl border border-[#C8E6C9] p-6">
      <h2 class="text-xl font-bold text-[#2E7D32] mb-4">Crear nuevo tenant</h2>
      
      <div *ngIf="error" class="mb-4 p-3 bg-red-50 text-red-700 rounded-lg text-sm">
        {{ error }}
      </div>
      
      <form [formGroup]="tenantForm" (ngSubmit)="onSubmit()" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-[#2E7D32] mb-1">Nombre de la empresa</label>
          <input type="text" formControlName="name" placeholder="Ej: Mi Empresa SRL"
                 class="w-full px-3 py-2 border border-[#C8E6C9] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#4CAF50]">
          <span *ngIf="isFieldInvalid('name')" class="text-xs text-red-600 mt-1">El nombre es obligatorio</span>
        </div>
        
        <div>
          <label class="block text-sm font-medium text-[#2E7D32] mb-1">Slug (identificador único)</label>
          <input type="text" formControlName="slug" placeholder="Ej: miempresa"
                 class="w-full px-3 py-2 border border-[#C8E6C9] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#4CAF50]">
          <span *ngIf="isFieldInvalid('slug')" class="text-xs text-red-600 mt-1">
            Solo letras minúsculas, números y guiones. Mínimo 3 caracteres.
          </span>
        </div>
        
        <div>
          <label class="block text-sm font-medium text-[#2E7D32] mb-1">Plan</label>
          <select formControlName="planCode"
                  class="w-full px-3 py-2 border border-[#C8E6C9] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#4CAF50]">
            <option value="DEMO">DEMO - Prueba gratuita (10 días, 2 usuarios, 2 roles)</option>
            <option value="BASIC">BASIC - Básico</option>
            <option value="PRO">PRO - Profesional</option>
            <option value="ENTERPRISE">ENTERPRISE - Empresarial</option>
          </select>
        </div>
        
        <div class="flex justify-end gap-3 pt-4">
          <button type="button" (click)="cancel.emit()" 
                  class="px-4 py-2 border border-[#C8E6C9] text-[#666666] rounded-lg hover:bg-[#F1F8E9] transition-colors">
            Cancelar
          </button>
          <button type="submit" [disabled]="isLoading" 
                  class="px-4 py-2 bg-[#2E7D32] text-white rounded-lg hover:bg-[#4CAF50] transition-colors disabled:opacity-50">
            {{ isLoading ? 'Creando...' : 'Crear tenant' }}
          </button>
        </div>
      </form>
    </div>
  `
})
export class PlatformTenantFormComponent {
  @Input() isLoading = false;
  @Input() error: string | null = null;
  @Output() submitForm = new EventEmitter<{ name: string; slug: string; planCode: string }>();
  @Output() cancel = new EventEmitter<void>();

  private fb = inject(FormBuilder);

  tenantForm = this.fb.group({
    name: ['', [Validators.required]],
    slug: ['', [Validators.required, Validators.minLength(3), Validators.pattern(/^[a-z0-9-]+$/)]],
    planCode: ['DEMO', Validators.required]
  });

  onSubmit(): void {
    if (this.tenantForm.valid) {
      this.submitForm.emit(this.tenantForm.value as { name: string; slug: string; planCode: string });
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.tenantForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }
}
```

### `app/features/platform/ui/platform-tenant-table/platform-tenant-table.component.ts`

```typescript
// features/platform/ui/platform-tenant-table/platform-tenant-table.component.ts
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LucideAngularModule, Eye, PauseCircle, PlayCircle } from 'lucide-angular';
import { PlatformTenant } from '../../../../entities/platform/api/platform.service';
import {IconsModule} from '../../../../shared/ui/icons/icons.module';

@Component({
  selector: 'app-platform-tenant-table',
  standalone: true,
  imports: [CommonModule, RouterModule, IconsModule],
  // imports: [CommonModule, RouterModule, LucideAngularModule],
  template: `
    <div class="overflow-x-auto">
      @if (isLoading) {
        <!-- Estado de carga (skeleton) -->
        <div class="space-y-3">
          @for (i of [1,2,3,4,5]; track i) {
            <div class="bg-white rounded-xl border border-[#C8E6C9] p-4 animate-pulse">
              <div class="flex justify-between">
                <div class="space-y-2">
                  <div class="h-5 w-32 bg-gray-200 rounded"></div>
                  <div class="h-4 w-24 bg-gray-200 rounded"></div>
                </div>
                <div class="h-6 w-16 bg-gray-200 rounded-full"></div>
              </div>
            </div>
          }
        </div>
      } @else {
        <table class="min-w-full divide-y divide-[#C8E6C9]">
          <thead class="bg-[#F1F8E9]">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Nombre</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Slug</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Estado</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Creado</th>
              <th class="px-6 py-3 text-right text-xs font-medium text-[#2E7D32] uppercase tracking-wider">Acciones</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-[#C8E6C9]">
            @for (tenant of tenants; track tenant.id) {
              <tr class="hover:bg-[#F1F8E9] transition-colors">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-medium text-[#2E7D32]">{{ tenant.name }}</div>
                  <div class="text-xs text-[#666666]">Schema: {{ tenant.schemaName }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-[#E8F5E9] text-[#2E7D32]">
                    {{ tenant.slug }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span [class]="tenant.active ? 'bg-[#E8F5E9] text-[#2E7D32]' : 'bg-[#FFEBEE] text-[#C62828]'" 
                        class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full">
                    {{ tenant.status }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-[#666666]">
                  {{ tenant.createdAt | date:'shortDate' }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <div class="flex justify-end gap-2">
                    <!-- ✅ Botón Ver detalles -->
                    <button (click)="viewDetails.emit(tenant.id)" 
                            class="text-[#4CAF50] hover:text-[#2E7D32] transition-colors"
                            title="Ver detalles">
                      <lucide-icon name="eye" class="h-5 w-5"></lucide-icon>
                    </button>
                    
                    <!-- ✅ Botón Desactivar (solo si está activo) -->
                    @if (tenant.active) {
                      <button (click)="deactivate.emit(tenant.id)" 
                              class="text-[#FF9800] hover:text-[#F57C00] transition-colors"
                              title="Desactivar">
                        <lucide-icon name="pause-circle" class="h-5 w-5"></lucide-icon>
                      </button>
                    }
                    
                    <!-- ✅ Botón Activar (solo si está inactivo) -->
                    @if (!tenant.active) {
                      <button (click)="activate.emit(tenant.id)" 
                              class="text-[#4CAF50] hover:text-[#2E7D32] transition-colors"
                              title="Activar">
                        <lucide-icon name="play-circle" class="h-5 w-5"></lucide-icon>
                      </button>
                    }
                  </div>
                </td>
              </tr>
            } @empty {
              <tr>
                <td colspan="5" class="px-6 py-10 text-center text-[#666666]">
                  No hay tenants registrados
                </td>
              </tr>
            }
          </tbody>
        </table>
      }
    </div>
  `
})
export class PlatformTenantTableComponent {
  @Input() tenants: PlatformTenant[] = [];
  @Input() isLoading = false;
  @Output() viewDetails = new EventEmitter<string>();
  @Output() activate = new EventEmitter<string>();
  @Output() deactivate = new EventEmitter<string>();
}
```

### `app/features/role-management/application/permission-list.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AccessService, SystemPermissionResponse } from '../../../entities/access';

export interface PermissionListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: SystemPermissionResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class PermissionListUseCase {
  private readonly accessService = inject(AccessService);

  private readonly state = signal<PermissionListState>({
    status: 'idle',
    data: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadPermissions(): Promise<void> {
    // Si ya los tenemos cargados, no volvemos a llamar (cache simple), los permisos rara vez cambian.
    if (this.state().data.length > 0 && this.state().status === 'success') {
      return;
    }

    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.accessService.getPermissions());

      if (response.success && response.data) {
        this.state.set({ 
          status: 'success', 
          data: response.data, 
          error: null 
        });
      } else {
        this.state.set({ 
          status: 'error', 
          data: [], 
          error: response.message || 'No se pudieron cargar los permisos del sistema' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }
}

```

### `app/features/role-management/application/role-form.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AccessService, CreateTenantRoleRequest, UpdateTenantRoleRequest } from '../../../entities/access';

export interface RoleFormState {
  status: 'idle' | 'loading' | 'success' | 'error';
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class RoleFormUseCase {
  private readonly accessService = inject(AccessService);

  private readonly state = signal<RoleFormState>({
    status: 'idle',
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly error = computed(() => this.state().error);

  async createRole(request: CreateTenantRoleRequest): Promise<void> {
    this.state.set({ status: 'loading', error: null });

    try {
      const response = await firstValueFrom(this.accessService.createRole(request));

      if (response.success && response.data) {
        this.state.set({ status: 'success', error: null });
      } else {
        this.state.set({ 
          status: 'error', 
          error: response.message || 'Error al crear el rol' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', error: errorMsg });
    }
  }

  async updateRole(id: string, request: UpdateTenantRoleRequest): Promise<void> {
    this.state.set({ status: 'loading', error: null });

    try {
      const response = await firstValueFrom(this.accessService.updateRole(id, request));

      if (response.success && response.data) {
        this.state.set({ status: 'success', error: null });
      } else {
        this.state.set({ 
          status: 'error', 
          error: response.message || 'Error al actualizar el rol' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', error: errorMsg });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', error: null });
  }
}

```

### `app/features/role-management/application/role-list.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AccessService, TenantRoleResponse } from '../../../entities/access';

export interface RoleListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: TenantRoleResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class RoleListUseCase {
  private readonly accessService = inject(AccessService);

  private readonly state = signal<RoleListState>({
    status: 'idle',
    data: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadRoles(): Promise<void> {
    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.accessService.getRoles());

      if (response.success && response.data) {
        this.state.set({ 
          status: 'success', 
          data: response.data, 
          error: null 
        });
      } else {
        this.state.set({ 
          status: 'error', 
          data: [], 
          error: response.message || 'No se pudieron cargar los roles' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }

  async reloadRoles(): Promise<void> {
    const currentData = this.state().data;
    this.state.set({ status: 'loading', data: currentData, error: null });

    try {
      const response = await firstValueFrom(this.accessService.getRoles());

      if (response.success && response.data) {
        this.state.set({ 
          status: 'success', 
          data: response.data, 
          error: null 
        });
      } else {
        this.state.set({ 
          status: 'error', 
          data: currentData,
          error: response.message || 'No se pudieron recargar los roles' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: currentData, error: errorMsg });
    }
  }

  // Métodos extra para activar/desactivar (toggle) en la lista si lo necesitamos
  async toggleRoleStatus(id: string, currentActiveStatus: boolean): Promise<boolean> {
    try {
      if (currentActiveStatus) {
        await firstValueFrom(this.accessService.deactivateRole(id));
      } else {
        await firstValueFrom(this.accessService.activateRole(id));
      }
      return true; // éxito
    } catch (error) {
      console.error('Error toggling role status', error);
      return false; // fallo
    }
  }
}

```

### `app/features/role-management/index.ts`

```typescript
export * from './application/permission-list.usecase';
export * from './application/role-list.usecase';
export * from './application/role-form.usecase';
export * from './ui/role-table/role-table.component';
export * from './ui/role-form/role-form.component';

```

### `app/features/role-management/ui/role-form/role-form.component.html`

```html
<form [formGroup]="roleForm" (ngSubmit)="onSubmit()" class="flex flex-col h-full">
  
  <div class="flex-1 overflow-y-auto p-6 space-y-6">
    <!-- Información General -->
    <section class="space-y-4">
      <h4 class="text-sm font-semibold text-foreground border-b border-border pb-2 uppercase tracking-wider">Información General</h4>
      
      <div>
        <label for="name" class="block text-sm font-medium text-foreground mb-1">Nombre del Rol</label>
        <input 
          id="name" 
          type="text" 
          formControlName="name"
          class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
          [class.border-destructive]="isFieldInvalid('name')"
          placeholder="Ej: Administrador Financiero">
        @if (isFieldInvalid('name')) {
          <span class="text-xs text-destructive mt-1 block animate-in slide-in-from-top-1">El nombre es requerido (máx 100 caracteres).</span>
        }
      </div>

      <div>
        <label for="description" class="block text-sm font-medium text-foreground mb-1">Descripción</label>
        <textarea 
          id="description" 
          rows="2"
          formControlName="description"
          class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors resize-none"
          placeholder="Ej: Tiene acceso total a los módulos de contabilidad..."></textarea>
      </div>
    </section>

    <!-- Permisos Agrupados -->
    <section class="space-y-4">
      <h4 class="text-sm font-semibold text-foreground border-b border-border pb-2 uppercase tracking-wider">Asignación de Permisos</h4>
      
      @if (groupedPermissions.length === 0) {
        <div class="p-4 border border-dashed border-border rounded-lg text-center text-muted-foreground text-sm">
          No hay permisos disponibles para cargar.
        </div>
      }

      <div class="space-y-5">
        @for (group of groupedPermissions; track group.module) {
          <div class="bg-muted/10 border border-border rounded-lg overflow-hidden">
            <!-- Header del Módulo -->
            <div class="bg-muted/30 px-4 py-3 flex items-center justify-between border-b border-border">
              <span class="font-medium text-foreground capitalize">{{ group.module }}</span>
              <label class="flex items-center gap-2 cursor-pointer">
                <span class="text-xs font-medium text-muted-foreground">Seleccionar todo</span>
                <!-- Switch nativo estilizado -->
                <div class="relative">
                  <input type="checkbox" class="peer sr-only" [checked]="isModuleFullySelected(group.permissions)" (change)="toggleModule(group.permissions, $event)">
                  <div class="block w-10 h-5.5 rounded-full bg-input peer-checked:bg-primary transition-colors duration-300"></div>
                  <div class="absolute left-1 top-1 bg-white w-3.5 h-3.5 rounded-full transition-transform duration-300 peer-checked:translate-x-4.5"></div>
                </div>
              </label>
            </div>
            
            <!-- Lista de Permisos -->
            <div class="p-4 grid grid-cols-1 md:grid-cols-2 gap-4">
              @for (perm of group.permissions; track perm.code) {
                <label class="flex items-start gap-3 cursor-pointer group">
                  <div class="relative flex items-center justify-center mt-0.5">
                    <input type="checkbox" class="peer sr-only" [checked]="isPermissionSelected(perm.code)" (change)="togglePermission(perm.code, $event)">
                    <div class="block w-9 h-5 rounded-full bg-input group-hover:bg-input/80 peer-checked:bg-secondary transition-colors duration-300"></div>
                    <div class="absolute left-1 top-1 bg-white w-3 h-3 rounded-full transition-transform duration-300 peer-checked:translate-x-4"></div>
                  </div>
                  <div>
                    <p class="text-sm font-medium text-foreground">{{ perm.code }}</p>
                    <p class="text-xs text-muted-foreground">{{ perm.description }}</p>
                  </div>
                </label>
              }
            </div>
          </div>
        }
      </div>
    </section>
  </div>

  <!-- Footer / Actions -->
  <div class="p-6 border-t border-border bg-card/50 flex items-center justify-end gap-3 mt-auto">
    <button 
      type="button" 
      (click)="onCancel()"
      [disabled]="status === 'loading'"
      class="px-4 py-2 text-sm font-medium text-foreground bg-background border border-input rounded-md hover:bg-muted transition-colors duration-200 disabled:opacity-50">
      Cancelar
    </button>
    <button 
      type="submit" 
      [disabled]="status === 'loading'"
      class="px-4 py-2 text-sm font-medium text-primary-foreground bg-primary rounded-md hover:bg-primary/90 transition-colors duration-200 disabled:opacity-50 flex items-center min-w-[120px] justify-center">
      
      @if (status === 'loading') {
        <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        Guardando...
      } @else {
        {{ roleToEdit ? 'Actualizar Rol' : 'Crear Rol' }}
      }
    </button>
  </div>
</form>

```

### `app/features/role-management/ui/role-form/role-form.component.ts`

```typescript
import { Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CreateTenantRoleRequest, SystemPermissionResponse, TenantRoleResponse, UpdateTenantRoleRequest } from '../../../../entities/access';

interface PermissionGroup {
  module: string;
  permissions: SystemPermissionResponse[];
}

@Component({
  selector: 'app-role-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './role-form.component.html'
})
export class RoleFormComponent implements OnInit, OnChanges {
  @Input() status: 'idle' | 'loading' | 'success' | 'error' = 'idle';
  @Input() roleToEdit: TenantRoleResponse | null = null;
  @Input() permissions: SystemPermissionResponse[] = [];
  
  @Output() submitCreate = new EventEmitter<CreateTenantRoleRequest>();
  @Output() submitUpdate = new EventEmitter<{ id: string, request: UpdateTenantRoleRequest }>();
  @Output() cancel = new EventEmitter<void>();

  private readonly fb = inject(FormBuilder);

  roleForm!: FormGroup;
  groupedPermissions: PermissionGroup[] = [];

  ngOnInit(): void {
    this.initForm();
    this.groupPermissions();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['permissions'] && !changes['permissions'].firstChange) {
      this.groupPermissions();
    }
    
    // Si roleToEdit cambia después de inicializar el form
    if (changes['roleToEdit'] && this.roleForm) {
      this.patchFormValues();
    }
  }

  private initForm(): void {
    this.roleForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(255)]],
      permissionCodes: this.fb.array([])
    });

    if (this.roleToEdit) {
      this.patchFormValues();
    }
  }

  private patchFormValues(): void {
    if (!this.roleForm) return;
    
    if (this.roleToEdit) {
      this.roleForm.patchValue({
        name: this.roleToEdit.name,
        description: this.roleToEdit.description || ''
      });
      // Limpiamos y agregamos los permisos
      const permissionsArray = this.roleForm.get('permissionCodes') as FormArray;
      permissionsArray.clear();
      
      this.roleToEdit.permissionCodes.forEach(code => {
        permissionsArray.push(new FormControl(code));
      });
    } else {
      this.roleForm.reset();
      (this.roleForm.get('permissionCodes') as FormArray).clear();
    }
  }

  private groupPermissions(): void {
    if (!this.permissions || this.permissions.length === 0) return;

    const grouped = this.permissions.reduce((acc, perm) => {
      const moduleName = perm.module;
      if (!acc[moduleName]) {
        acc[moduleName] = [];
      }
      acc[moduleName].push(perm);
      return acc;
    }, {} as Record<string, SystemPermissionResponse[]>);

    this.groupedPermissions = Object.keys(grouped).map(key => ({
      module: key,
      permissions: grouped[key]
    })).sort((a, b) => a.module.localeCompare(b.module));
  }

  // Helpers para Checkboxes/Switches
  isPermissionSelected(code: string): boolean {
    const permissionsArray = this.roleForm.get('permissionCodes') as FormArray;
    return permissionsArray.value.includes(code);
  }

  togglePermission(code: string, event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    const permissionsArray = this.roleForm.get('permissionCodes') as FormArray;

    if (isChecked) {
      permissionsArray.push(new FormControl(code));
    } else {
      const index = permissionsArray.controls.findIndex(x => x.value === code);
      if (index >= 0) {
        permissionsArray.removeAt(index);
      }
    }
  }

  // Select all inside a module
  isModuleFullySelected(modulePermissions: SystemPermissionResponse[]): boolean {
    if (modulePermissions.length === 0) return false;
    return modulePermissions.every(p => this.isPermissionSelected(p.code));
  }

  toggleModule(modulePermissions: SystemPermissionResponse[], event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    const permissionsArray = this.roleForm.get('permissionCodes') as FormArray;

    modulePermissions.forEach(p => {
      const isCurrentlySelected = this.isPermissionSelected(p.code);
      if (isChecked && !isCurrentlySelected) {
        permissionsArray.push(new FormControl(p.code));
      } else if (!isChecked && isCurrentlySelected) {
        const index = permissionsArray.controls.findIndex(x => x.value === p.code);
        if (index >= 0) {
          permissionsArray.removeAt(index);
        }
      }
    });
  }

  onSubmit(): void {
    if (this.roleForm.valid && this.status !== 'loading') {
      const formValue = this.roleForm.value;
      
      if (this.roleToEdit) {
        const request: UpdateTenantRoleRequest = {
          name: formValue.name,
          description: formValue.description,
          permissionCodes: formValue.permissionCodes
        };
        this.submitUpdate.emit({ id: this.roleToEdit.id, request });
      } else {
        const request: CreateTenantRoleRequest = {
          name: formValue.name,
          description: formValue.description,
          permissionCodes: formValue.permissionCodes
        };
        this.submitCreate.emit(request);
      }
    } else {
      this.roleForm.markAllAsTouched();
    }
  }

  onCancel(): void {
    if (this.status !== 'loading') {
      this.cancel.emit();
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.roleForm?.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }
}

```

### `app/features/role-management/ui/role-table/role-table.component.html`

```html
<div class="bg-card rounded-xl border border-border overflow-hidden shadow-sm transition-all duration-300">
  <div class="overflow-x-auto">
    <table class="w-full text-sm text-left">
      <thead class="text-xs text-muted-foreground uppercase bg-muted/50 border-b border-border">
        <tr>
          <th scope="col" class="px-6 py-4 font-medium">Nombre del Rol</th>
          <th scope="col" class="px-6 py-4 font-medium">Descripción</th>
          <th scope="col" class="px-6 py-4 font-medium text-center">Permisos</th>
          <th scope="col" class="px-6 py-4 font-medium">Estado</th>
          <th scope="col" class="px-6 py-4 font-medium text-right">Acciones</th>
        </tr>
      </thead>
      <tbody>
        @for (role of roles; track role.id) {
          <tr class="border-b border-border last:border-0 hover:bg-muted/20 transition-colors duration-200 group">
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="font-medium text-foreground">{{ role.name }}</div>
            </td>
            <td class="px-6 py-4 text-muted-foreground max-w-xs truncate">
              {{ role.description || 'Sin descripción' }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-center">
              <span class="inline-flex items-center justify-center px-2 py-1 rounded-full text-xs font-semibold bg-primary/10 text-primary border border-primary/20">
                {{ role.permissionCodes.length }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              @if (role.active) {
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-secondary/10 text-secondary border border-secondary/20">
                  Activo
                </span>
              } @else {
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-destructive/10 text-destructive border border-destructive/20">
                  Inactivo
                </span>
              }
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right space-x-2">
              <button 
                type="button"
                (click)="onEdit(role)"
                class="text-muted-foreground hover:text-primary transition-colors px-2 py-1 rounded-md hover:bg-primary/10"
                title="Editar Rol">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/><path d="m15 5 4 4"/></svg>
              </button>
              <button 
                type="button"
                (click)="onToggleStatus(role)"
                [class]="role.active ? 'text-muted-foreground hover:text-destructive hover:bg-destructive/10' : 'text-muted-foreground hover:text-secondary hover:bg-secondary/10'"
                class="transition-colors px-2 py-1 rounded-md"
                [title]="role.active ? 'Desactivar Rol' : 'Activar Rol'">
                @if (role.active) {
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="4.93" y1="4.93" x2="19.07" y2="19.07"/></svg>
                } @else {
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
                }
              </button>
            </td>
          </tr>
        } @empty {
          <tr class="border-b border-border last:border-0 hover:bg-muted/20 transition-colors duration-200">
            <td colspan="5" class="px-6 py-8 text-center text-muted-foreground">
              No se encontraron roles en esta organización.
            </td>
          </tr>
        }
      </tbody>
    </table>
  </div>
</div>

```

### `app/features/role-management/ui/role-table/role-table.component.ts`

```typescript
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TenantRoleResponse } from '../../../../entities/access';

@Component({
  selector: 'app-role-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './role-table.component.html'
})
export class RoleTableComponent {
  @Input() roles: TenantRoleResponse[] = [];
  @Output() edit = new EventEmitter<TenantRoleResponse>();
  @Output() toggleStatus = new EventEmitter<{id: string, currentStatus: boolean}>();

  onEdit(role: TenantRoleResponse): void {
    this.edit.emit(role);
  }

  onToggleStatus(role: TenantRoleResponse): void {
    this.toggleStatus.emit({ id: role.id, currentStatus: role.active });
  }
}

```

### `app/features/transactions-management/application/transactions-list.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { v4 as uuidv4 } from 'uuid';
import { 
  TransactionsService, 
  TransactionResponse, 
  CreateDepositTransactionRequest,
  CreateWithdrawalTransactionRequest,
  CreateTransferTransactionRequest,
  CreatePaymentTransactionRequest
} from '../../../entities/transactions';

export interface TransactionsListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: TransactionResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class TransactionsListUseCase {
  private readonly transactionsService = inject(TransactionsService);

  private readonly state = signal<TransactionsListState>({
    status: 'idle',
    data: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadTransactions(): Promise<void> {
    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.transactionsService.listTransactions());

      if (response.success && response.data) {
        this.state.set({ 
          status: 'success', 
          data: response.data, 
          error: null 
        });
      } else {
        this.state.set({ 
          status: 'error', 
          data: [], 
          error: response.message || 'No se pudieron cargar las transacciones' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }

  async executeTransaction(
    type: 'deposit' | 'withdrawal' | 'transfer' | 'payment',
    request: any
  ): Promise<void> {
    try {
      if (!request.idempotencyKey) {
        request.idempotencyKey = uuidv4(); // ✅ Cambiado a uuidv4()
      }

      let response;
      switch (type) {
        case 'deposit': 
          response = await firstValueFrom(this.transactionsService.createDeposit(request as CreateDepositTransactionRequest));
          break;
        case 'withdrawal':
          response = await firstValueFrom(this.transactionsService.createWithdrawal(request as CreateWithdrawalTransactionRequest));
          break;
        case 'transfer':
          response = await firstValueFrom(this.transactionsService.createTransfer(request as CreateTransferTransactionRequest));
          break;
        case 'payment':
          response = await firstValueFrom(this.transactionsService.createPayment(request as CreatePaymentTransactionRequest));
          break;
      }
      
      if (response.success) {
        await this.loadTransactions();
      } else {
        throw new Error(response.message || `Error al ejecutar la transacción de tipo ${type}`);
      }
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }

  async reverseTransaction(id: string, reason: string): Promise<void> {
    try {
      const request = { 
        description: `Reversión: ${reason}`, 
        reason,
        idempotencyKey: uuidv4() // ✅ Cambiado a uuidv4()
      };
      const response = await firstValueFrom(this.transactionsService.reverseTransaction(id, request));
      if (response.success) {
        await this.loadTransactions();
      } else {
        throw new Error(response.message || 'Error al revertir la transacción');
      }
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }

  async refundTransaction(id: string, reason: string, amount: number): Promise<void> {
    try {
      const request = { 
        description: `Reembolso: ${reason}`, 
        reason, 
        amount,
        idempotencyKey: uuidv4() // ✅ Cambiado a uuidv4()
      };
      const response = await firstValueFrom(this.transactionsService.refundTransaction(id, request));
      if (response.success) {
        await this.loadTransactions();
      } else {
        throw new Error(response.message || 'Error al reembolsar la transacción');
      }
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }
}
```

### `app/features/transactions-management/index.ts`

```typescript
export * from './application/transactions-list.usecase';
export * from './ui/transaction-slide-over/transaction-slide-over.component';

```

### `app/features/transactions-management/ui/transaction-slide-over/transaction-slide-over.component.ts`

```typescript
import { Component, EventEmitter, Input, Output, inject, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LucideAngularModule, X, Send, ArrowRightLeft, ArrowDownToLine, ArrowUpFromLine, CreditCard } from 'lucide-angular';
import { AccountListUseCase } from '../../../account-management';

export type TransactionActionType = 'deposit' | 'withdrawal' | 'transfer' | 'payment';

@Component({
  selector: 'app-transaction-slide-over',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <!-- Overlay -->
    <div 
      *ngIf="isOpen" 
      class="fixed inset-0 bg-background/80 backdrop-blur-sm z-40 transition-opacity"
      (click)="close()">
    </div>

    <!-- Slide-over -->
    <div 
      class="fixed inset-y-0 right-0 z-50 w-full max-w-md bg-card shadow-2xl border-l border-border transform transition-transform duration-300 ease-in-out flex flex-col"
      [class.translate-x-0]="isOpen"
      [class.translate-x-full]="!isOpen">
      
      <!-- Header -->
      <div class="flex items-center justify-between p-6 border-b border-border" [ngClass]="headerConfig.bgClass">
        <div class="flex items-center gap-3">
          <div class="p-2 rounded-lg bg-background/50">
            <lucide-icon [name]="headerConfig.icon" [size]="24" [class]="headerConfig.textClass"></lucide-icon>
          </div>
          <div>
            <h2 class="text-xl font-bold text-foreground">{{ headerConfig.title }}</h2>
            <p class="text-sm text-muted-foreground">{{ headerConfig.description }}</p>
          </div>
        </div>
        <button 
          (click)="close()"
          class="p-2 rounded-full hover:bg-background/50 text-muted-foreground hover:text-foreground transition-colors">
          <lucide-icon name="x" [size]="20"></lucide-icon>
        </button>
      </div>

      <!-- Content -->
      <div class="flex-1 overflow-y-auto p-6">
        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-5">
          
          <!-- Cuenta de Origen -->
          <div *ngIf="transactionType !== 'deposit'" class="space-y-2">
            <label class="text-sm font-medium text-foreground">Cuenta Origen</label>
            <select 
              formControlName="sourceAccountId"
              class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2">
              <option value="" disabled selected>Selecciona una cuenta</option>
              <option *ngFor="let acc of accountListUseCase.data()" [value]="acc.id">
                {{ acc.accountNumber }} - {{ acc.customAlias || acc.accountNameLabel }} ({{ acc.currency }})
              </option>
            </select>
          </div>

          <!-- Cuenta Destino -->
          <div *ngIf="transactionType !== 'withdrawal'" class="space-y-2">
            <label class="text-sm font-medium text-foreground">Cuenta Destino</label>
            <!-- Si es transferencia o pago, asumo que puede seleccionar una cuenta del sistema, de lo contrario podría ser texto libre (ej targetAccountId). Por simplificación, mostramos el combo de cuentas del sistema -->
            <select 
              formControlName="targetAccountId"
              class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2">
              <option value="" disabled selected>Selecciona una cuenta destino</option>
              <option *ngFor="let acc of accountListUseCase.data()" [value]="acc.id">
                {{ acc.accountNumber }} - {{ acc.customAlias || acc.accountNameLabel }} ({{ acc.currency }})
              </option>
            </select>
          </div>

          <!-- Monto y Moneda -->
          <div class="grid grid-cols-3 gap-4">
            <div class="col-span-2 space-y-2">
              <label class="text-sm font-medium text-foreground">Monto</label>
              <input 
                type="number" 
                formControlName="amount"
                placeholder="0.00"
                min="0.01" step="0.01"
                class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2" />
            </div>
            <div class="space-y-2">
              <label class="text-sm font-medium text-foreground">Moneda</label>
              <select 
                formControlName="currency"
                class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2">
                <option value="USD">USD</option>
                <option value="BOB">BOB</option>
                <option value="EUR">EUR</option>
              </select>
            </div>
          </div>

          <!-- Canal (Sólo para Depósitos y Retiros) -->
          <div *ngIf="transactionType === 'deposit' || transactionType === 'withdrawal'" class="space-y-2">
            <label class="text-sm font-medium text-foreground">Canal</label>
            <select 
              formControlName="channel"
              class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2">
              <option value="BRANCH">Sucursal (Caja)</option>
              <option value="ATM">Cajero Automático</option>
              <option value="WEB">Web</option>
            </select>
          </div>

          <!-- Descripción -->
          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground">Descripción</label>
            <textarea 
              formControlName="description"
              placeholder="Motivo o detalle de la transacción"
              rows="3"
              class="flex w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 resize-none"></textarea>
          </div>

          <!-- Referencia Externa -->
          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground">Referencia Externa (Opcional)</label>
            <input 
              type="text" 
              formControlName="externalReference"
              placeholder="Ej. ID de comprobante"
              class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2" />
          </div>

        </form>
      </div>

      <!-- Footer -->
      <div class="p-6 border-t border-border bg-muted/10 flex justify-end gap-3">
        <button 
          type="button" 
          (click)="close()"
          class="inline-flex items-center justify-center rounded-md text-sm font-medium border border-input bg-background hover:bg-accent hover:text-accent-foreground h-10 px-4 py-2 transition-colors">
          Cancelar
        </button>
        <button 
          type="button" 
          (click)="onSubmit()"
          [disabled]="form.invalid || isSubmitting"
          class="inline-flex items-center justify-center rounded-md text-sm font-medium bg-primary text-primary-foreground hover:bg-primary/90 h-10 px-4 py-2 gap-2 shadow-sm transition-colors disabled:opacity-50">
          <lucide-icon *ngIf="!isSubmitting" name="send" [size]="16"></lucide-icon>
          <svg *ngIf="isSubmitting" class="animate-spin h-4 w-4 text-primary-foreground" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          Procesar
        </button>
      </div>
    </div>
  `
})
export class TransactionSlideOverComponent implements OnInit, OnChanges {
  @Input() isOpen = false;
  @Input() transactionType: TransactionActionType = 'deposit';
  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<{ type: TransactionActionType, request: any }>();

  public readonly accountListUseCase = inject(AccountListUseCase);
  private fb = inject(FormBuilder);
  
  form!: FormGroup;
  isSubmitting = false;

  get headerConfig() {
    switch (this.transactionType) {
      case 'deposit': return { title: 'Depósito', description: 'Abonar fondos a una cuenta', icon: 'arrow-down-to-line', bgClass: 'bg-green-500/10', textClass: 'text-green-600' };
      case 'withdrawal': return { title: 'Retiro', description: 'Debitar fondos de una cuenta', icon: 'arrow-up-from-line', bgClass: 'bg-orange-500/10', textClass: 'text-orange-600' };
      case 'transfer': return { title: 'Transferencia', description: 'Mover fondos entre cuentas', icon: 'arrow-right-left', bgClass: 'bg-blue-500/10', textClass: 'text-blue-600' };
      case 'payment': return { title: 'Pago', description: 'Registrar un pago', icon: 'credit-card', bgClass: 'bg-purple-500/10', textClass: 'text-purple-600' };
      default: return { title: 'Transacción', description: '', icon: 'send', bgClass: 'bg-muted', textClass: 'text-foreground' };
    }
  }

  ngOnInit() {
    this.initForm();
    if (this.accountListUseCase.data().length === 0) {
      this.accountListUseCase.loadAccounts();
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['transactionType'] || changes['isOpen']) {
      if (this.isOpen) {
        this.initForm();
        this.isSubmitting = false;
      }
    }
  }

  initForm() {
    this.form = this.fb.group({
      amount: ['', [Validators.required, Validators.min(0.01)]],
      currency: ['USD', Validators.required],
      description: ['', Validators.required],
      externalReference: ['']
    });

    if (this.transactionType !== 'deposit') {
      this.form.addControl('sourceAccountId', this.fb.control('', Validators.required));
    }
    
    if (this.transactionType !== 'withdrawal') {
      this.form.addControl('targetAccountId', this.fb.control('', Validators.required));
    }

    if (this.transactionType === 'deposit' || this.transactionType === 'withdrawal') {
      this.form.addControl('channel', this.fb.control('BRANCH', Validators.required));
    }
  }

  close() {
    this.isOpen = false;
    this.closed.emit();
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.saved.emit({
      type: this.transactionType,
      request: this.form.value
    });
  }

  setSubmitting(val: boolean) {
    this.isSubmitting = val;
    if (!val) {
      this.close();
    }
  }
}

```

### `app/features/user-management/application/user-create.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { UserService, CreateTenantUserRequest } from '../../../entities/user';

export interface UserCreateState {
  status: 'idle' | 'loading' | 'success' | 'error';
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class UserCreateUseCase {
  private readonly userService = inject(UserService);

  private readonly state = signal<UserCreateState>({
    status: 'idle',
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly error = computed(() => this.state().error);

  async createUser(request: CreateTenantUserRequest): Promise<void> {
    this.state.set({ status: 'loading', error: null });

    try {
      const response = await firstValueFrom(this.userService.createUser(request));

      if (response.success && response.data) {
        this.state.set({ status: 'success', error: null });
      } else {
        this.state.set({ 
          status: 'error', 
          error: response.message || 'Error al crear el usuario' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', error: errorMsg });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', error: null });
  }
}

```

### `app/features/user-management/application/user-list.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { UserService, TenantUserResponse } from '../../../entities/user';

export interface UserListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: TenantUserResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class UserListUseCase {
  private readonly userService = inject(UserService);

  private readonly state = signal<UserListState>({
    status: 'idle',
    data: [],
    error: null
  });

  // Selectores
  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadUsers(): Promise<void> {
    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.userService.getUsers());

      if (response.success && response.data) {
        this.state.set({ 
          status: 'success', 
          data: response.data, 
          error: null 
        });
      } else {
        this.state.set({ 
          status: 'error', 
          data: [], 
          error: response.message || 'No se pudieron cargar los usuarios' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }

  async reloadUsers(): Promise<void> {
    // Al recargar, mantenemos la data actual pero cambiamos el status para mostrar algún indicador si se desea
    // o simplemente actualizamos silenciosamente
    const currentData = this.state().data;
    this.state.set({ status: 'loading', data: currentData, error: null });

    try {
      const response = await firstValueFrom(this.userService.getUsers());

      if (response.success && response.data) {
        this.state.set({ 
          status: 'success', 
          data: response.data, 
          error: null 
        });
      } else {
        this.state.set({ 
          status: 'error', 
          data: currentData, // Mantenemos la data vieja en caso de error? Depende UX, pero el prompt dice "evitar parpadeo visual".
          error: response.message || 'No se pudieron recargar los usuarios' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: currentData, error: errorMsg });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', data: [], error: null });
  }
}

```

### `app/features/user-management/application/user-status.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { UserService } from '../../../entities/user';

export interface UserStatusState {
  status: 'idle' | 'loading' | 'success' | 'error';
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class UserStatusUseCase {
  private readonly userService = inject(UserService);

  private readonly state = signal<UserStatusState>({
    status: 'idle',
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly error = computed(() => this.state().error);

  async toggleStatus(id: string, currentlyActive: boolean): Promise<boolean> {
    this.state.set({ status: 'loading', error: null });

    try {
      const request = currentlyActive 
        ? this.userService.deactivateUser(id)
        : this.userService.activateUser(id);

      const response = await firstValueFrom(request);

      if (response.success) {
        this.state.set({ status: 'success', error: null });
        return true;
      } else {
        this.state.set({ 
          status: 'error', 
          error: response.message || 'Error al cambiar estado del usuario' 
        });
        return false;
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', error: errorMsg });
      return false;
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', error: null });
  }
}

```

### `app/features/user-management/application/user-update.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { UserService, UpdateTenantUserRequest } from '../../../entities/user';

export interface UserUpdateState {
  status: 'idle' | 'loading' | 'success' | 'error';
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class UserUpdateUseCase {
  private readonly userService = inject(UserService);

  private readonly state = signal<UserUpdateState>({
    status: 'idle',
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly error = computed(() => this.state().error);

  async updateUser(id: string, request: UpdateTenantUserRequest): Promise<boolean> {
    this.state.set({ status: 'loading', error: null });

    try {
      const response = await firstValueFrom(this.userService.updateUser(id, request));

      if (response.success) {
        this.state.set({ status: 'success', error: null });
        return true;
      } else {
        this.state.set({ 
          status: 'error', 
          error: response.message || 'Error al actualizar el usuario' 
        });
        return false;
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', error: errorMsg });
      return false;
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', error: null });
  }
}

```

### `app/features/user-management/index.ts`

```typescript
export * from './application/user-list.usecase';
export * from './application/user-create.usecase';
export * from './application/user-update.usecase';
export * from './application/user-status.usecase';
export * from './ui/user-table/user-table.component';
export * from './ui/user-create-form/user-create-form.component';
export * from './ui/user-edit-form/user-edit-form.component';

```

### `app/features/user-management/ui/user-create-form/user-create-form.component.html`

```html
<form [formGroup]="userForm" (ngSubmit)="onSubmit()" class="space-y-4">
  <div class="grid grid-cols-2 gap-4">
    <div>
      <label for="firstName" class="block text-sm font-medium text-foreground mb-1">Nombre</label>
      <input 
        id="firstName" 
        type="text" 
        formControlName="firstName"
        class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
        [class.border-destructive]="isFieldInvalid('firstName')"
        placeholder="Ej: Carlos">
      <span *ngIf="isFieldInvalid('firstName')" class="text-xs text-destructive mt-1">El nombre es requerido.</span>
    </div>
    
    <div>
      <label for="lastName" class="block text-sm font-medium text-foreground mb-1">Apellido</label>
      <input 
        id="lastName" 
        type="text" 
        formControlName="lastName"
        class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
        [class.border-destructive]="isFieldInvalid('lastName')"
        placeholder="Ej: Rojas">
      <span *ngIf="isFieldInvalid('lastName')" class="text-xs text-destructive mt-1">El apellido es requerido.</span>
    </div>
  </div>

  <div>
    <label for="email" class="block text-sm font-medium text-foreground mb-1">Correo Electrónico</label>
    <input 
      id="email" 
      type="email" 
      formControlName="email"
      class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
      [class.border-destructive]="isFieldInvalid('email')"
      placeholder="usuario@empresa.com">
    <span *ngIf="isFieldInvalid('email')" class="text-xs text-destructive mt-1">Ingresa un correo válido.</span>
  </div>

  <div>
    <label for="password" class="block text-sm font-medium text-foreground mb-1">Contraseña Temporal</label>
    <input 
      id="password" 
      type="password" 
      formControlName="password"
      class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
      [class.border-destructive]="isFieldInvalid('password')"
      placeholder="••••••••">
    <span *ngIf="isFieldInvalid('password')" class="text-xs text-destructive mt-1">Mínimo 8 caracteres requeridos.</span>
  </div>

  <!-- Actions -->
  <div class="flex items-center justify-end gap-3 pt-4 border-t border-border mt-6">
    <button 
      type="button" 
      (click)="onCancel()"
      [disabled]="status === 'loading'"
      class="px-4 py-2 text-sm font-medium text-foreground bg-background border border-input rounded-md hover:bg-muted transition-colors disabled:opacity-50">
      Cancelar
    </button>
    <button 
      type="submit" 
      [disabled]="status === 'loading'"
      class="px-4 py-2 text-sm font-medium text-primary-foreground bg-primary rounded-md hover:bg-primary/90 transition-colors disabled:opacity-50 flex items-center">
      
      <ng-container *ngIf="status !== 'loading'; else loadingSpinner">
        Guardar Usuario
      </ng-container>
      
      <ng-template #loadingSpinner>
        <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        Guardando...
      </ng-template>
    </button>
  </div>
</form>

```

### `app/features/user-management/ui/user-create-form/user-create-form.component.ts`

```typescript
import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CreateTenantUserRequest } from '../../../../entities/user';

@Component({
  selector: 'app-user-create-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-create-form.component.html'
})
export class UserCreateFormComponent {
  @Input() status: 'idle' | 'loading' | 'success' | 'error' = 'idle';
  
  @Output() formSubmit = new EventEmitter<CreateTenantUserRequest>();
  @Output() cancel = new EventEmitter<void>();

  private readonly fb = inject(FormBuilder);

  userForm: FormGroup = this.fb.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  onSubmit(): void {
    if (this.userForm.valid && this.status !== 'loading') {
      this.formSubmit.emit(this.userForm.value);
    } else {
      this.userForm.markAllAsTouched();
    }
  }

  onCancel(): void {
    if (this.status !== 'loading') {
      this.cancel.emit();
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.userForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }
}

```

### `app/features/user-management/ui/user-edit-form/user-edit-form.component.ts`

```typescript
import { Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UpdateTenantUserRequest, TenantUserResponse } from '../../../../entities/user';

@Component({
  selector: 'app-user-edit-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <form [formGroup]="userForm" (ngSubmit)="onSubmit()" class="space-y-4">
      <div class="grid grid-cols-2 gap-4">
        <div>
          <label for="firstNameEdit" class="block text-sm font-medium text-foreground mb-1">Nombre</label>
          <input 
            id="firstNameEdit" 
            type="text" 
            formControlName="firstName"
            class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
            [class.border-destructive]="isFieldInvalid('firstName')"
            placeholder="Ej: Carlos">
          <span *ngIf="isFieldInvalid('firstName')" class="text-xs text-destructive mt-1">El nombre es requerido.</span>
        </div>
        
        <div>
          <label for="lastNameEdit" class="block text-sm font-medium text-foreground mb-1">Apellido</label>
          <input 
            id="lastNameEdit" 
            type="text" 
            formControlName="lastName"
            class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
            [class.border-destructive]="isFieldInvalid('lastName')"
            placeholder="Ej: Rojas">
          <span *ngIf="isFieldInvalid('lastName')" class="text-xs text-destructive mt-1">El apellido es requerido.</span>
        </div>
      </div>

      <div>
        <label for="emailEdit" class="block text-sm font-medium text-foreground mb-1">Correo Electrónico</label>
        <input 
          id="emailEdit" 
          type="email" 
          formControlName="email"
          class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
          [class.border-destructive]="isFieldInvalid('email')"
          placeholder="usuario@empresa.com">
        <span *ngIf="isFieldInvalid('email')" class="text-xs text-destructive mt-1">Ingresa un correo válido.</span>
      </div>

      <!-- Actions -->
      <div class="flex items-center justify-end gap-3 pt-4 border-t border-border mt-6">
        <button 
          type="button" 
          (click)="onCancel()"
          [disabled]="status === 'loading'"
          class="px-4 py-2 text-sm font-medium text-foreground bg-background border border-input rounded-md hover:bg-muted transition-colors disabled:opacity-50">
          Cancelar
        </button>
        <button 
          type="submit" 
          [disabled]="status === 'loading'"
          class="px-4 py-2 text-sm font-medium text-primary-foreground bg-primary rounded-md hover:bg-primary/90 transition-colors disabled:opacity-50 flex items-center">
          
          <ng-container *ngIf="status !== 'loading'; else loadingSpinner">
            Guardar Cambios
          </ng-container>
          
          <ng-template #loadingSpinner>
            <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Guardando...
          </ng-template>
        </button>
      </div>
    </form>
  `
})
export class UserEditFormComponent implements OnInit {
  @Input() status: 'idle' | 'loading' | 'success' | 'error' = 'idle';
  @Input() user!: TenantUserResponse;
  
  @Output() formSubmit = new EventEmitter<UpdateTenantUserRequest>();
  @Output() cancel = new EventEmitter<void>();

  private readonly fb = inject(FormBuilder);

  userForm: FormGroup = this.fb.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]]
  });

  ngOnInit() {
    if (this.user) {
      this.userForm.patchValue({
        firstName: this.user.firstName,
        lastName: this.user.lastName,
        email: this.user.email
      });
    }
  }

  onSubmit(): void {
    if (this.userForm.valid && this.status !== 'loading') {
      this.formSubmit.emit(this.userForm.value);
    } else {
      this.userForm.markAllAsTouched();
    }
  }

  onCancel(): void {
    if (this.status !== 'loading') {
      this.cancel.emit();
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.userForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }
}

```

### `app/features/user-management/ui/user-table/user-table.component.html`

```html
<div class="bg-card rounded-xl border border-border overflow-hidden shadow-sm">
  <div class="overflow-x-auto">
    <table class="w-full text-sm text-left">
      <thead class="text-xs text-muted-foreground uppercase bg-muted/50 border-b border-border">
        <tr>
          <th scope="col" class="px-6 py-4 font-medium">Nombre Completo</th>
          <th scope="col" class="px-6 py-4 font-medium">Email</th>
          <th scope="col" class="px-6 py-4 font-medium">Estado</th>
          <th scope="col" class="px-6 py-4 font-medium text-right">Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngIf="users.length === 0" class="border-b border-border last:border-0 hover:bg-muted/20 transition-colors">
          <td colspan="4" class="px-6 py-8 text-center text-muted-foreground">
            No se encontraron usuarios en esta organización.
          </td>
        </tr>
        
        <tr *ngFor="let user of users" class="border-b border-border last:border-0 hover:bg-muted/20 transition-colors">
          <td class="px-6 py-4 whitespace-nowrap">
            <div class="font-medium text-foreground">{{ user.firstName }} {{ user.lastName }}</div>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-muted-foreground">
            {{ user.email }}
          </td>
          <td class="px-6 py-4 whitespace-nowrap">
            <span *ngIf="user.active" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-secondary/10 text-secondary">
              Activo
            </span>
            <span *ngIf="!user.active" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-destructive/10 text-destructive">
              Inactivo
            </span>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-right space-x-2 flex justify-end items-center">
            <button 
              type="button"
              (click)="onToggleStatus(user)"
              class="text-muted-foreground hover:text-foreground transition-colors px-2 py-1 rounded-md"
              [title]="user.active ? 'Desactivar' : 'Activar'">
              <svg *ngIf="user.active" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive hover:text-destructive/80"><rect width="18" height="18" x="3" y="3" rx="2" ry="2"/><line x1="9" x2="15" y1="9" y2="15"/><line x1="15" x2="9" y1="9" y2="15"/></svg>
              <svg *ngIf="!user.active" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-green-600 hover:text-green-500"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
            </button>
            <button 
              type="button"
              (click)="onEditUser(user)"
              class="text-muted-foreground hover:text-primary transition-colors px-2 py-1 rounded-md hover:bg-primary/10"
              title="Editar Usuario">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/><path d="m15 5 4 4"/></svg>
            </button>
            <button 
              type="button"
              (click)="onManageRoles(user)"
              class="text-muted-foreground hover:text-primary transition-colors px-2 py-1 rounded-md hover:bg-primary/10"
              title="Gestionar Roles">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
  
  <!-- Preparado para futura paginación -->
  <div class="px-6 py-4 border-t border-border bg-muted/20 flex items-center justify-between text-sm text-muted-foreground">
    <div>Mostrando <span class="font-medium text-foreground">{{ users.length }}</span> resultados</div>
    <div class="flex gap-2">
      <button disabled class="px-3 py-1 rounded border border-border bg-card text-muted-foreground opacity-50 cursor-not-allowed">Anterior</button>
      <button disabled class="px-3 py-1 rounded border border-border bg-card text-muted-foreground opacity-50 cursor-not-allowed">Siguiente</button>
    </div>
  </div>
</div>

```

### `app/features/user-management/ui/user-table/user-table.component.ts`

```typescript
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TenantUserResponse } from '../../../../entities/user';

@Component({
  selector: 'app-user-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-table.component.html'
})
export class UserTableComponent {
  @Input() users: TenantUserResponse[] = [];
  @Output() manageRoles = new EventEmitter<TenantUserResponse>();
  @Output() editUser = new EventEmitter<TenantUserResponse>();
  @Output() toggleStatus = new EventEmitter<TenantUserResponse>();

  onManageRoles(user: TenantUserResponse): void {
    this.manageRoles.emit(user);
  }

  onEditUser(user: TenantUserResponse): void {
    this.editUser.emit(user);
  }

  onToggleStatus(user: TenantUserResponse): void {
    this.toggleStatus.emit(user);
  }
}

```

### `app/features/user-role-management/application/user-role.usecase.ts`

```typescript
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AccessService, AssignUserRolesRequest, TenantRoleResponse, UserRolesResponse } from '../../../entities/access';

export interface UserRoleState {
  status: 'idle' | 'loading' | 'success' | 'error';
  userId: string | null;
  userRoles: TenantRoleResponse[];
  availableRoles: TenantRoleResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class UserRoleUseCase {
  private readonly accessService = inject(AccessService);

  private readonly state = signal<UserRoleState>({
    status: 'idle',
    userId: null,
    userRoles: [],
    availableRoles: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly userRoles = computed(() => this.state().userRoles);
  readonly availableRoles = computed(() => this.state().availableRoles);
  readonly error = computed(() => this.state().error);

  async loadRolesForUser(userId: string): Promise<void> {
    this.state.set({ status: 'loading', userId, userRoles: [], availableRoles: [], error: null });

    try {
      // Cargamos en paralelo los roles del usuario y todos los roles del tenant para mostrar opciones
      const [userRolesRes, allRolesRes] = await Promise.all([
        firstValueFrom(this.accessService.getUserRoles(userId)),
        firstValueFrom(this.accessService.getRoles())
      ]);

      if (userRolesRes.success && allRolesRes.success) {
        this.state.set({ 
          status: 'idle', // Lo dejamos ready pero idle para asignación
          userId,
          userRoles: userRolesRes.data.roles,
          availableRoles: allRolesRes.data.filter(r => r.active), // solo asignamos roles activos
          error: null 
        });
      } else {
        this.state.set({ 
          status: 'error', 
          userId,
          userRoles: [], 
          availableRoles: [],
          error: 'Error al cargar los roles disponibles o asignados.' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', userId, userRoles: [], availableRoles: [], error: errorMsg });
    }
  }

  async assignRoles(userId: string, roleIds: string[]): Promise<void> {
    const currentState = this.state();
    this.state.set({ ...currentState, status: 'loading', error: null });

    try {
      const request: AssignUserRolesRequest = { roleIds };
      const response = await firstValueFrom(this.accessService.assignUserRoles(userId, request));

      if (response.success && response.data) {
        this.state.set({ 
          ...currentState,
          status: 'success', 
          userRoles: response.data.roles,
          error: null 
        });
      } else {
        this.state.set({ 
          ...currentState,
          status: 'error', 
          error: response.message || 'Error al asignar roles' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ ...currentState, status: 'error', error: errorMsg });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', userId: null, userRoles: [], availableRoles: [], error: null });
  }
}

```

### `app/features/user-role-management/index.ts`

```typescript
export * from './application/user-role.usecase';
export * from './ui/user-role-assignment/user-role-assignment.component';

```

### `app/features/user-role-management/ui/user-role-assignment/user-role-assignment.component.html`

```html
<div class="flex flex-col h-full">
  <div class="flex-1 overflow-y-auto p-6 space-y-6">
    
    @if (user) {
      <div class="bg-muted/30 p-4 rounded-lg border border-border flex items-center gap-4">
        <div class="w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center text-primary font-bold text-lg border border-primary/20">
          {{ user.firstName.charAt(0) }}{{ user.lastName.charAt(0) }}
        </div>
        <div>
          <h4 class="font-medium text-foreground">{{ user.firstName }} {{ user.lastName }}</h4>
          <p class="text-sm text-muted-foreground">{{ user.email }}</p>
        </div>
      </div>
    }

    <div class="space-y-3">
      <h4 class="text-sm font-semibold text-foreground border-b border-border pb-2 uppercase tracking-wider">Roles Disponibles</h4>
      
      @if (status === 'loading' && availableRoles.length === 0) {
        <div class="py-8 flex justify-center text-muted-foreground">Cargando roles...</div>
      }

      @if (availableRoles.length === 0 && status !== 'loading') {
        <div class="p-4 border border-dashed border-border rounded-lg text-center text-muted-foreground text-sm">
          No hay roles activos disponibles en este tenant.
        </div>
      }

      <div class="grid grid-cols-1 gap-3 mt-2">
        @for (role of availableRoles; track role.id) {
          <label class="flex items-start gap-3 p-3 rounded-lg border border-border hover:bg-muted/20 cursor-pointer transition-colors"
                 [class.bg-primary/5]="hasRole(role.id)"
                 [class.border-primary/30]="hasRole(role.id)">
            <div class="relative flex items-center justify-center mt-1">
              <input type="checkbox" class="peer sr-only" [checked]="hasRole(role.id)" (change)="toggleRole(role.id, $event)">
              <div class="block w-10 h-5.5 rounded-full bg-input peer-checked:bg-primary transition-colors duration-300"></div>
              <div class="absolute left-1 top-1 bg-white w-3.5 h-3.5 rounded-full transition-transform duration-300 peer-checked:translate-x-4.5"></div>
            </div>
            <div class="flex-1">
              <div class="flex items-center justify-between">
                <span class="font-medium text-foreground">{{ role.name }}</span>
                @if (hasRole(role.id)) {
                  <span class="text-[10px] uppercase font-bold text-primary bg-primary/10 px-2 py-0.5 rounded-full">Asignado</span>
                }
              </div>
              <p class="text-xs text-muted-foreground mt-0.5 line-clamp-2">{{ role.description }}</p>
            </div>
          </label>
        }
      </div>
    </div>
  </div>

  <div class="p-6 border-t border-border bg-card/50 flex items-center justify-end gap-3 mt-auto">
    <button 
      type="button" 
      (click)="onCancel()"
      [disabled]="status === 'loading'"
      class="px-4 py-2 text-sm font-medium text-foreground bg-background border border-input rounded-md hover:bg-muted transition-colors duration-200 disabled:opacity-50">
      Cancelar
    </button>
    <button 
      type="button" 
      (click)="onSubmit()"
      [disabled]="status === 'loading'"
      class="px-4 py-2 text-sm font-medium text-primary-foreground bg-primary rounded-md hover:bg-primary/90 transition-colors duration-200 disabled:opacity-50 flex items-center min-w-[120px] justify-center">
      
      @if (status === 'loading') {
        <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        Guardando...
      } @else {
        Asignar Roles
      }
    </button>
  </div>
</div>

```

### `app/features/user-role-management/ui/user-role-assignment/user-role-assignment.component.ts`

```typescript
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TenantRoleResponse } from '../../../../entities/access';
import { TenantUserResponse } from '../../../../entities/user';

@Component({
  selector: 'app-user-role-assignment',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-role-assignment.component.html'
})
export class UserRoleAssignmentComponent implements OnChanges {
  @Input() status: 'idle' | 'loading' | 'success' | 'error' = 'idle';
  @Input() user: TenantUserResponse | null = null;
  @Input() availableRoles: TenantRoleResponse[] = [];
  @Input() assignedRoles: TenantRoleResponse[] = [];
  
  @Output() submitRoles = new EventEmitter<string[]>();
  @Output() cancel = new EventEmitter<void>();

  // Estado local para los checkboxes
  selectedRoleIds: Set<string> = new Set<string>();

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['assignedRoles']) {
      this.selectedRoleIds = new Set(this.assignedRoles.map(r => r.id));
    }
  }

  toggleRole(roleId: string, event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    if (isChecked) {
      this.selectedRoleIds.add(roleId);
    } else {
      this.selectedRoleIds.delete(roleId);
    }
  }

  hasRole(roleId: string): boolean {
    return this.selectedRoleIds.has(roleId);
  }

  onSubmit(): void {
    if (this.status !== 'loading') {
      this.submitRoles.emit(Array.from(this.selectedRoleIds));
    }
  }

  onCancel(): void {
    if (this.status !== 'loading') {
      this.cancel.emit();
    }
  }
}

```

### `app/pages/accounting-journal-entries-page/accounting-journal-entries-page.component.ts`

```typescript
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe, CurrencyPipe } from '@angular/common';
import { JournalEntryListUseCase } from '../../features/accounting';

@Component({
  selector: 'app-accounting-journal-entries-page',
  standalone: true,
  imports: [CommonModule],
  providers: [DatePipe, CurrencyPipe],
  template: `
    <div class="space-y-6 relative">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold tracking-tight text-foreground">Asientos de Diario</h2>
          <p class="text-muted-foreground">Visualiza todos los movimientos contables registrados.</p>
        </div>
      </div>

      <!-- Estado: Error Lista -->
      @if (useCase.status() === 'error') {
        <div class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-destructive">Error al cargar asientos</h3>
            <p class="text-sm text-destructive/80 mt-1">{{ useCase.error() }}</p>
            <button (click)="loadEntries()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      <!-- Estado: Cargando Inicial -->
      @if (useCase.status() === 'loading' && useCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center p-12 text-muted-foreground gap-4">
          <svg class="animate-spin h-8 w-8 text-primary" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando asientos...</p>
        </div>
      }

      <!-- Estado: Éxito o Recargando -->
      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <div class="bg-card border border-border rounded-xl shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-sm text-left">
              <thead class="text-xs text-muted-foreground uppercase bg-muted/50 border-b border-border">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Nº Asiento</th>
                  <th scope="col" class="px-6 py-4 font-medium">Referencia</th>
                  <th scope="col" class="px-6 py-4 font-medium">Tipo</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Total Débitos</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Total Créditos</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium">Fecha de Registro</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-border">
                @for (entry of useCase.data(); track entry.id) {
                  <tr class="hover:bg-muted/30 transition-colors">
                    <td class="px-6 py-4 font-medium text-foreground">{{ entry.entryNumber }}</td>
                    <td class="px-6 py-4 text-muted-foreground">{{ entry.reference || '-' }}</td>
                    <td class="px-6 py-4 text-muted-foreground">{{ entry.entryType }}</td>
                    <td class="px-6 py-4 font-medium text-right text-foreground">{{ entry.totalDebits | currency:'USD' }}</td>
                    <td class="px-6 py-4 font-medium text-right text-foreground">{{ entry.totalCredits | currency:'USD' }}</td>
                    <td class="px-6 py-4 text-center">
                      <span class="px-2.5 py-1 text-xs font-semibold rounded-full"
                            [ngClass]="{
                              'bg-green-500/10 text-green-600': entry.status === 'POSTED',
                              'bg-yellow-500/10 text-yellow-600': entry.status === 'DRAFT',
                              'bg-red-500/10 text-red-600': entry.status === 'VOIDED'
                            }">
                        {{ entry.status }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-muted-foreground text-sm">{{ entry.createdAt | date:'short' }}</td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="7" class="px-6 py-12 text-center text-muted-foreground">
                      No hay asientos contables registrados.
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }
    </div>
  `
})
export class AccountingJournalEntriesPageComponent implements OnInit {
  public readonly useCase = inject(JournalEntryListUseCase);

  ngOnInit() {
    this.loadEntries();
  }

  loadEntries() {
    this.useCase.loadEntries();
  }
}

```

### `app/pages/accounting-periods-page/accounting-periods-page.component.ts`

```typescript
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { PeriodListUseCase } from '../../features/accounting';

@Component({
  selector: 'app-accounting-periods-page',
  standalone: true,
  imports: [CommonModule],
  providers: [DatePipe],
  template: `
    <div class="space-y-6 relative">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold tracking-tight text-foreground">Períodos Contables</h2>
          <p class="text-muted-foreground">Gestiona los períodos contables de tu empresa.</p>
        </div>
        
        <button 
          type="button"
          class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2 shadow-sm">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" x2="12" y1="5" y2="19"/><line x1="5" x2="19" y1="12" y2="12"/></svg>
          Nuevo Período
        </button>
      </div>

      <!-- Estado: Error Lista -->
      @if (useCase.status() === 'error') {
        <div class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-destructive">Error al cargar períodos</h3>
            <p class="text-sm text-destructive/80 mt-1">{{ useCase.error() }}</p>
            <button (click)="loadPeriods()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      <!-- Estado: Cargando Inicial -->
      @if (useCase.status() === 'loading' && useCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center p-12 text-muted-foreground gap-4">
          <svg class="animate-spin h-8 w-8 text-primary" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando períodos...</p>
        </div>
      }

      <!-- Estado: Éxito o Recargando -->
      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <div class="bg-card border border-border rounded-xl shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-sm text-left">
              <thead class="text-xs text-muted-foreground uppercase bg-muted/50 border-b border-border">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Código</th>
                  <th scope="col" class="px-6 py-4 font-medium">Tipo</th>
                  <th scope="col" class="px-6 py-4 font-medium">Fecha Inicio</th>
                  <th scope="col" class="px-6 py-4 font-medium">Fecha Fin</th>
                  <th scope="col" class="px-6 py-4 font-medium">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-border">
                @for (period of useCase.data(); track period.id) {
                  <tr class="hover:bg-muted/30 transition-colors">
                    <td class="px-6 py-4 font-medium text-foreground">{{ period.periodCode }}</td>
                    <td class="px-6 py-4 text-muted-foreground">{{ period.periodType }}</td>
                    <td class="px-6 py-4 text-muted-foreground">{{ period.startDate | date:'mediumDate' }}</td>
                    <td class="px-6 py-4 text-muted-foreground">{{ period.endDate | date:'mediumDate' }}</td>
                    <td class="px-6 py-4">
                      <span class="px-2.5 py-1 text-xs font-semibold rounded-full"
                            [ngClass]="period.status === 'OPEN' ? 'bg-green-500/10 text-green-600' : 'bg-gray-500/10 text-gray-600'">
                        {{ period.status }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-right">
                      @if(period.status === 'OPEN') {
                        <button class="text-xs font-medium text-destructive hover:underline">
                          Cerrar Período
                        </button>
                      }
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-muted-foreground">
                      No hay períodos contables registrados.
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }
    </div>
  `
})
export class AccountingPeriodsPageComponent implements OnInit {
  public readonly useCase = inject(PeriodListUseCase);

  ngOnInit() {
    this.loadPeriods();
  }

  loadPeriods() {
    this.useCase.loadPeriods();
  }
}

```

### `app/pages/accounts-page/accounts-page.component.ts`

```typescript
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { AccountListUseCase, AccountFormComponent } from '../../features/account-management';
import { LucideAngularModule, Plus, MoreHorizontal, CheckCircle, Play, Ban, Snowflake, XCircle, Pencil, Wallet } from 'lucide-angular';
import { AccountOwnerResponse, CreateAccountRequest, UpdateAccountRequest } from '../../entities/accounts';
import { HasPermissionPipe } from '../../shared/api';
import { ToastService } from '../../shared/ui/toast/toast.service';

@Component({
  selector: 'app-accounts-page',
  standalone: true,
  imports: [CommonModule, AccountFormComponent, LucideAngularModule, HasPermissionPipe],
  providers: [CurrencyPipe],
  template: `
    <div class="space-y-6 relative">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold tracking-tight text-foreground">Cuentas Bancarias</h2>
          <p class="text-muted-foreground">Administra las cuentas bancarias de los clientes del sistema.</p>
        </div>
        
        <button 
          *ngIf="'accounts.create' | hasPermission"
          (click)="openCreateForm()"
          type="button"
          class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2 shadow-sm">
          <lucide-icon name="plus" [size]="16"></lucide-icon>
          Nueva Cuenta
        </button>
      </div>

      <!-- Estado: Error Lista -->
      @if (useCase.status() === 'error') {
        <div class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-destructive">Error al cargar cuentas</h3>
            <p class="text-sm text-destructive/80 mt-1">{{ useCase.error() }}</p>
            <button (click)="loadAccounts()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      <!-- Estado: Cargando Inicial -->
      @if (useCase.status() === 'loading' && useCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center p-12 text-muted-foreground gap-4">
          <svg class="animate-spin h-8 w-8 text-primary" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando cuentas...</p>
        </div>
      }

      <!-- Estado: Éxito o Recargando -->
      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <div class="bg-card border border-border rounded-xl shadow-sm">
          <div class="overflow-x-auto min-h-[300px]">
            <table class="w-full text-sm text-left">
              <thead class="text-xs text-muted-foreground uppercase bg-muted/50 border-b border-border">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Cliente</th>
                  <th scope="col" class="px-6 py-4 font-medium">Nº Cuenta / Alias</th>
                  <th scope="col" class="px-6 py-4 font-medium">Tipo</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Saldo Disp.</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Saldo Reten.</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center w-16">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-border">
                @for (account of useCase.data(); track account.id) {
                  <tr class="hover:bg-muted/30 transition-colors">
                    <td class="px-6 py-4">
                      <div class="font-medium text-foreground">{{ account.userFullName }}</div>
                      <div class="text-xs text-muted-foreground">{{ account.userEmail }}</div>
                    </td>
                    <td class="px-6 py-4">
                      <div class="font-medium text-foreground">{{ account.accountNumber }}</div>
                      <div class="text-xs text-muted-foreground">{{ account.customAlias || account.accountNameLabel }}</div>
                    </td>
                    <td class="px-6 py-4 text-muted-foreground">{{ account.accountType }} - {{ account.currency }}</td>
                    <td class="px-6 py-4 font-medium text-right text-foreground">{{ account.availableBalance | currency:account.currency }}</td>
                    <td class="px-6 py-4 font-medium text-right text-foreground">{{ account.heldBalance | currency:account.currency }}</td>
                    <td class="px-6 py-4 text-center">
                      <span class="px-2.5 py-1 text-xs font-semibold rounded-full"
                            [ngClass]="{
                              'bg-green-500/10 text-green-600': account.status === 'ACTIVE',
                              'bg-yellow-500/10 text-yellow-600': account.status === 'FROZEN' || account.status === 'PENDING_APPROVAL',
                              'bg-red-500/10 text-red-600': account.status === 'BLOCKED' || account.status === 'CLOSED'
                            }">
                        {{ account.status }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-center">
                      <div class="relative group inline-block text-left">
                        <button class="p-2 rounded-md hover:bg-muted text-muted-foreground transition-colors focus:outline-none">
                          <lucide-icon name="more-horizontal" [size]="16"></lucide-icon>
                        </button>
                        <div class="absolute right-0 mt-1 w-48 bg-card border border-border rounded-lg shadow-lg opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all z-50 overflow-hidden">
                          <div class="py-1">
                            <button *ngIf="'accounts.balance.read' | hasPermission" (click)="viewBalance(account)" class="w-full text-left px-4 py-2 text-xs text-foreground hover:bg-muted transition-colors flex items-center gap-2">
                              <lucide-icon name="wallet" [size]="14"></lucide-icon> Ver Saldo
                            </button>
                            <button *ngIf="'accounts.update' | hasPermission" (click)="openEditForm(account)" class="w-full text-left px-4 py-2 text-xs text-foreground hover:bg-muted transition-colors flex items-center gap-2">
                              <lucide-icon name="pencil" [size]="14"></lucide-icon> Editar
                            </button>
                            
                            <div class="h-px bg-border my-1"></div>
                            
                            <!-- Acciones de estado (condicionales al rol) -->
                            <button *ngIf="('accounts.approve' | hasPermission) && account.status === 'PENDING_APPROVAL'" (click)="changeState(account, 'approve')" class="w-full text-left px-4 py-2 text-xs text-green-600 hover:bg-green-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="check-circle" [size]="14"></lucide-icon> Aprobar
                            </button>
                            <button *ngIf="('accounts.activate' | hasPermission) && (account.status === 'PENDING_APPROVAL' || account.status === 'FROZEN')" (click)="changeState(account, 'activate')" class="w-full text-left px-4 py-2 text-xs text-green-600 hover:bg-green-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="play" [size]="14"></lucide-icon> Activar
                            </button>
                            <button *ngIf="('accounts.block' | hasPermission) && account.status !== 'BLOCKED' && account.status !== 'CLOSED'" (click)="changeStateWithReason(account, 'block')" class="w-full text-left px-4 py-2 text-xs text-red-600 hover:bg-red-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="ban" [size]="14"></lucide-icon> Bloquear
                            </button>
                            <button *ngIf="('accounts.freeze' | hasPermission) && account.status === 'ACTIVE'" (click)="changeStateWithReason(account, 'freeze')" class="w-full text-left px-4 py-2 text-xs text-yellow-600 hover:bg-yellow-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="snowflake" [size]="14"></lucide-icon> Congelar
                            </button>
                            <button *ngIf="('accounts.close' | hasPermission) && account.status !== 'CLOSED'" (click)="changeStateWithReason(account, 'close')" class="w-full text-left px-4 py-2 text-xs text-red-600 hover:bg-red-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="x-circle" [size]="14"></lucide-icon> Cerrar Cuenta
                            </button>
                          </div>
                        </div>
                      </div>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-muted-foreground">
                      No hay cuentas bancarias registradas.
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }
    </div>

    <!-- Formulario Slide-over -->
    <app-account-form
      [isOpen]="isFormOpen"
      [account]="selectedAccount"
      (closed)="isFormOpen = false"
      (saved)="onFormSaved($event)">
    </app-account-form>

    <!-- Modal de Saldo -->
    <div *ngIf="balanceData" class="fixed inset-0 z-[100] flex items-center justify-center">
      <!-- Overlay blur -->
      <div 
        class="absolute inset-0 bg-background/80 backdrop-blur-sm transition-opacity"
        (click)="closeBalanceModal()">
      </div>
      
      <!-- Contenido del modal -->
      <div class="relative w-full max-w-sm bg-card rounded-2xl shadow-2xl border border-border p-6 transform transition-all">
        <!-- Cerrar -->
        <button 
          (click)="closeBalanceModal()"
          class="absolute right-4 top-4 p-2 rounded-full text-muted-foreground hover:bg-muted hover:text-foreground transition-colors">
          <lucide-icon name="x" [size]="20"></lucide-icon>
        </button>

        <div class="flex flex-col items-center text-center mt-2">
          <div class="h-16 w-16 bg-primary/10 text-primary rounded-full flex items-center justify-center mb-4">
            <lucide-icon name="wallet" [size]="32"></lucide-icon>
          </div>
          <h3 class="text-xl font-bold text-foreground">Saldo de la Cuenta</h3>
          <p class="text-sm text-muted-foreground mt-1">{{ balanceData.accountNumber }}</p>
          <p class="text-xs text-muted-foreground">{{ balanceData.accountNameLabel || balanceData.customAlias }}</p>
        </div>

        <div class="mt-8 space-y-4">
          <div class="bg-green-500/10 border border-green-500/20 rounded-xl p-4 flex flex-col items-center">
            <span class="text-xs font-semibold text-green-600 uppercase tracking-wider">Saldo Disponible</span>
            <span class="text-2xl font-black text-green-700 dark:text-green-500 mt-1">
              {{ balanceData.availableBalance | currency:balanceData.currency }}
            </span>
          </div>

          <div class="bg-muted rounded-xl p-4 flex justify-between items-center">
            <span class="text-sm font-medium text-muted-foreground">Saldo Retenido</span>
            <span class="text-base font-bold text-foreground">
              {{ balanceData.heldBalance | currency:balanceData.currency }}
            </span>
          </div>
        </div>

        <button 
          (click)="closeBalanceModal()"
          class="mt-6 w-full bg-primary text-primary-foreground hover:bg-primary/90 font-medium py-2.5 rounded-xl transition-colors">
          Entendido
        </button>
      </div>
    </div>
  `
})
export class AccountsPageComponent implements OnInit {
  public readonly useCase = inject(AccountListUseCase);
  private readonly toastService = inject(ToastService);

  isFormOpen = false;
  selectedAccount: AccountOwnerResponse | null = null;
  balanceData: any = null;

  ngOnInit() {
    this.loadAccounts();
  }

  loadAccounts() {
    this.useCase.loadAccounts();
  }

  openCreateForm() {
    this.selectedAccount = null;
    this.isFormOpen = true;
  }

  openEditForm(account: AccountOwnerResponse) {
    this.selectedAccount = account;
    this.isFormOpen = true;
  }

  async onFormSaved(event: { request: CreateAccountRequest | UpdateAccountRequest, isEditing: boolean }) {
    try {
      if (event.isEditing && this.selectedAccount) {
        await this.useCase.updateAccount(this.selectedAccount.id, event.request as UpdateAccountRequest);
      } else {
        await this.useCase.createAccount(event.request as CreateAccountRequest);
      }
      this.isFormOpen = false;
    } catch (error) {
      this.toastService.error('Error al guardar la cuenta: ' + error);
    }
  }

  async changeState(account: AccountOwnerResponse, action: 'approve' | 'activate') {
    if (confirm(`¿Estás seguro que deseas ${action === 'approve' ? 'aprobar' : 'activar'} esta cuenta?`)) {
      try {
        await this.useCase.changeAccountState(account.id, action);
        this.toastService.success(`Cuenta ${action === 'approve' ? 'aprobada' : 'activada'} con éxito.`);
      } catch (error) {
        this.toastService.error(`Error al ${action === 'approve' ? 'aprobar' : 'activar'}: ` + error);
      }
    }
  }

  async changeStateWithReason(account: AccountOwnerResponse, action: 'block' | 'freeze' | 'close') {
    const actionLabel = action === 'block' ? 'bloquear' : action === 'freeze' ? 'congelar' : 'cerrar';
    const reason = prompt(`Por favor, ingresa el motivo para ${actionLabel} la cuenta:`);
    if (reason !== null) {
      try {
        await this.useCase.changeAccountState(account.id, action, reason);
        this.toastService.success(`Cuenta ${actionLabel}da con éxito.`);
      } catch (error) {
        this.toastService.error('Error al cambiar estado: ' + error);
      }
    }
  }

  async viewBalance(account: AccountOwnerResponse) {
    try {
      const response = await this.useCase.getAccountBalance(account.id);
      if (response && response.data) {
        this.balanceData = response.data;
      }
    } catch (error) {
      this.toastService.error('Error al consultar saldo: ' + error);
    }
  }

  closeBalanceModal() {
    this.balanceData = null;
  }
}

```

### `app/pages/dashboard-page/dashboard-page.component.ts`

```typescript
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterOutlet } from '@angular/router';
import { AuthFacade } from '../../shared/lib/auth/auth.facade';
import { SidebarComponent, HeaderComponent } from '../../widgets/layoutAdmin';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterOutlet, SidebarComponent, HeaderComponent],
  template: `
    <div class="flex h-screen w-full bg-background overflow-hidden">
      
      <!-- Sidebar Separado como Widget. El w-0 md:w-64 es crucial para que el contenedor reserve el espacio del Sidebar y no desalinee el header -->
      <app-sidebar class="w-0 md:w-64 shrink-0 transition-all duration-300 relative z-50" (logoutAction)="logout()"></app-sidebar>

      <!-- Main Content Area -->
      <main class="flex-1 flex flex-col min-w-0 overflow-hidden bg-muted/20 relative">
        
        <!-- Header Separado como Widget -->
        <app-header (logoutAction)="logout()"></app-header>

        <!-- Content Area -->
        <div class="flex-1 overflow-y-auto p-8">
          <router-outlet></router-outlet>
        </div>
      </main>

    </div>
  `
})
export class DashboardPageComponent {
  private readonly authFacade = inject(AuthFacade);

  logout(): void {
    this.authFacade.logout();
  }
}

```

### `app/pages/fx-fees-page/fx-fees-page.component.ts`

```typescript
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe, PercentPipe, CurrencyPipe } from '@angular/common';
import { FxFeesListUseCase, FxFeeFormComponent } from '../../features/fx-management';
import { HasPermissionPipe } from '../../shared/api';
import { ToastService } from '../../shared/ui/toast/toast.service';
import { OperationFeeResponse } from '../../entities/fx';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-fx-fees-page',
  standalone: true,
  imports: [CommonModule, FxFeeFormComponent, HasPermissionPipe, LucideAngularModule],
  providers: [DatePipe, PercentPipe, CurrencyPipe],
  template: `
    <div class="space-y-6 relative">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold tracking-tight text-foreground">Comisiones Operativas</h2>
          <p class="text-muted-foreground">Gestiona las tarifas cobradas por operaciones en la plataforma.</p>
        </div>
        
        <button 
          *ngIf="'fx.fees.create' | hasPermission"
          (click)="openCreateForm()"
          type="button"
          class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2 shadow-sm">
          <lucide-icon name="plus" [size]="16"></lucide-icon>
          Nueva Comisión
        </button>
      </div>

      <!-- Estado: Error Lista -->
      @if (useCase.status() === 'error') {
        <div class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-destructive">Error al cargar comisiones</h3>
            <p class="text-sm text-destructive/80 mt-1">{{ useCase.error() }}</p>
            <button (click)="loadFees()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      <!-- Estado: Cargando Inicial -->
      @if (useCase.status() === 'loading' && useCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center p-12 text-muted-foreground gap-4">
          <svg class="animate-spin h-8 w-8 text-primary" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando tarifas...</p>
        </div>
      }

      <!-- Estado: Éxito o Recargando -->
      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <div class="bg-card border border-border rounded-xl shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-sm text-left">
              <thead class="text-xs text-muted-foreground uppercase bg-muted/50 border-b border-border">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Código Operación</th>
                  <th scope="col" class="px-6 py-4 font-medium">Tipo Comisión</th>
                  <th scope="col" class="px-6 py-4 font-medium">Modo Cálculo</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Valor</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-border">
                @for (fee of useCase.data(); track fee.id) {
                  <tr class="hover:bg-muted/30 transition-colors">
                    <td class="px-6 py-4 font-medium text-foreground">{{ fee.operationCode }}</td>
                    <td class="px-6 py-4 text-muted-foreground">{{ fee.feeType }}</td>
                    <td class="px-6 py-4 text-muted-foreground">
                      <span class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-secondary text-secondary-foreground">
                        {{ fee.calculationMode }}
                      </span>
                    </td>
                    <td class="px-6 py-4 font-medium text-right text-foreground">
                      @if (fee.calculationMode === 'PERCENTAGE') {
                        {{ fee.feeValue / 100 | percent:'1.2-2' }}
                      } @else {
                        {{ fee.feeValue | currency:'USD' }}
                      }
                    </td>
                    <td class="px-6 py-4 text-center">
                      <span class="px-2.5 py-1 text-xs font-semibold rounded-full"
                            [ngClass]="fee.active ? 'bg-green-500/10 text-green-600' : 'bg-gray-500/10 text-gray-600'">
                        {{ fee.active ? 'ACTIVA' : 'INACTIVA' }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-right space-x-3">
                      <button *ngIf="'fx.fees.update' | hasPermission" (click)="openEditForm(fee)" class="text-primary hover:underline text-xs font-medium">Editar</button>
                      <button *ngIf="'fx.fees.delete' | hasPermission" (click)="deleteFee(fee.id)" class="text-destructive hover:underline text-xs font-medium">Eliminar</button>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-muted-foreground">
                      No hay comisiones registradas.
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }

      @if (isFormOpen) {
        <app-fx-fee-form
          [fee]="selectedFee"
          [loading]="isSubmitting"
          (formSubmit)="onFormSubmit($event)"
          (cancel)="closeForm()"
        ></app-fx-fee-form>
      }
    </div>
  `
})
export class FxFeesPageComponent implements OnInit {
  public readonly useCase = inject(FxFeesListUseCase);
  private readonly toastService = inject(ToastService);

  isFormOpen = false;
  selectedFee: OperationFeeResponse | null = null;
  isSubmitting = false;

  ngOnInit() {
    this.loadFees();
  }

  loadFees() {
    this.useCase.loadFees();
  }

  openCreateForm() {
    this.selectedFee = null;
    this.isFormOpen = true;
  }

  openEditForm(fee: OperationFeeResponse) {
    this.selectedFee = fee;
    this.isFormOpen = true;
  }

  closeForm() {
    this.isFormOpen = false;
    this.selectedFee = null;
  }

  async onFormSubmit(event: { type: 'create' | 'update', data: any }) {
    this.isSubmitting = true;
    try {
      if (event.type === 'create') {
        await this.useCase.createFee(event.data);
        this.toastService.success('Comisión creada exitosamente');
      } else {
        await this.useCase.updateFee(this.selectedFee!.id, event.data);
        this.toastService.success('Comisión actualizada exitosamente');
      }
      this.closeForm();
    } catch (error: any) {
      let msg = error.error?.message || error.message || 'Error al procesar la solicitud';
      
      // Traducciones amigables para el usuario
      if (msg.includes('already exists')) {
        msg = 'Ya existe una comisión operativa registrada con ese código.';
      } else if (msg.includes('Fee value must be zero')) {
        msg = 'Si el tipo de comisión es "Sin Comisión", el valor debe ser exactamente 0.';
      }
      
      this.toastService.error(msg, 'No se pudo guardar');
    } finally {
      this.isSubmitting = false;
    }
  }

  async deleteFee(id: string) {
    if (confirm('¿Estás seguro de eliminar esta comisión?')) {
      const success = await this.useCase.deleteFee(id);
      if (success) {
        this.toastService.success('Comisión eliminada');
      } else {
        this.toastService.error('No se pudo eliminar la comisión');
      }
    }
  }
}

```

### `app/pages/fx-rates-page/fx-rates-page.component.ts`

```typescript
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FxRatesListUseCase, FxRateFormComponent } from '../../features/fx-management';
import { HasPermissionPipe } from '../../shared/api';
import { ToastService } from '../../shared/ui/toast/toast.service';
import { FxExchangeRateResponse } from '../../entities/fx';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-fx-rates-page',
  standalone: true,
  imports: [CommonModule, FxRateFormComponent, HasPermissionPipe, LucideAngularModule],
  providers: [DatePipe],
  template: `
    <div class="space-y-6 relative">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold tracking-tight text-foreground">Tipos de Cambio</h2>
          <p class="text-muted-foreground">Gestiona las tasas de cambio de divisas del sistema.</p>
        </div>
        
        <button 
          *ngIf="'fx.rates.create' | hasPermission"
          (click)="openCreateForm()"
          type="button"
          class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2 shadow-sm">
          <lucide-icon name="plus" [size]="16"></lucide-icon>
          Nueva Tasa
        </button>
      </div>

      <!-- Estado: Error Lista -->
      @if (useCase.status() === 'error') {
        <div class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-destructive">Error al cargar tipos de cambio</h3>
            <p class="text-sm text-destructive/80 mt-1">{{ useCase.error() }}</p>
            <button (click)="loadRates()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      <!-- Estado: Cargando Inicial -->
      @if (useCase.status() === 'loading' && useCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center p-12 text-muted-foreground gap-4">
          <svg class="animate-spin h-8 w-8 text-primary" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando tasas...</p>
        </div>
      }

      <!-- Estado: Éxito o Recargando -->
      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <div class="bg-card border border-border rounded-xl shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-sm text-left">
              <thead class="text-xs text-muted-foreground uppercase bg-muted/50 border-b border-border">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Par de Divisas</th>
                  <th scope="col" class="px-6 py-4 font-medium">Tasa (Rate)</th>
                  <th scope="col" class="px-6 py-4 font-medium">Descripción</th>
                  <th scope="col" class="px-6 py-4 font-medium">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium">Última Actualización</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-border">
                @for (rate of useCase.data(); track rate.id) {
                  <tr class="hover:bg-muted/30 transition-colors">
                    <td class="px-6 py-4">
                      <div class="flex items-center gap-2">
                        <span class="font-bold text-foreground">{{ rate.sourceCurrency }}</span>
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-muted-foreground"><path d="m9 18 6-6-6-6"/></svg>
                        <span class="font-bold text-foreground">{{ rate.targetCurrency }}</span>
                      </div>
                    </td>
                    <td class="px-6 py-4 font-medium text-foreground">{{ rate.rate | number:'1.2-6' }}</td>
                    <td class="px-6 py-4 text-muted-foreground">{{ rate.description || '-' }}</td>
                    <td class="px-6 py-4">
                      <span class="px-2.5 py-1 text-xs font-semibold rounded-full"
                            [ngClass]="rate.active ? 'bg-green-500/10 text-green-600' : 'bg-gray-500/10 text-gray-600'">
                        {{ rate.active ? 'ACTIVA' : 'INACTIVA' }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-muted-foreground">{{ rate.updatedAt | date:'short' }}</td>
                    <td class="px-6 py-4 text-right space-x-3">
                      <button *ngIf="'fx.rates.update' | hasPermission" (click)="openEditForm(rate)" class="text-primary hover:underline text-xs font-medium">Editar</button>
                      <button *ngIf="'fx.rates.delete' | hasPermission" (click)="deleteRate(rate.id)" class="text-destructive hover:underline text-xs font-medium">Eliminar</button>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-muted-foreground">
                      No hay tasas de cambio registradas.
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }

      @if (isFormOpen) {
        <app-fx-rate-form
          [rate]="selectedRate"
          [loading]="isSubmitting"
          (formSubmit)="onFormSubmit($event)"
          (cancel)="closeForm()"
        ></app-fx-rate-form>
      }
    </div>
  `
})
export class FxRatesPageComponent implements OnInit {
  public readonly useCase = inject(FxRatesListUseCase);
  private readonly toastService = inject(ToastService);

  isFormOpen = false;
  selectedRate: FxExchangeRateResponse | null = null;
  isSubmitting = false;

  ngOnInit() {
    this.loadRates();
  }

  loadRates() {
    this.useCase.loadRates();
  }

  openCreateForm() {
    this.selectedRate = null;
    this.isFormOpen = true;
  }

  openEditForm(rate: FxExchangeRateResponse) {
    this.selectedRate = rate;
    this.isFormOpen = true;
  }

  closeForm() {
    this.isFormOpen = false;
    this.selectedRate = null;
  }

  async onFormSubmit(event: { type: 'create' | 'update', data: any }) {
    this.isSubmitting = true;
    try {
      if (event.type === 'create') {
        await this.useCase.createRate(event.data);
        this.toastService.success('Tasa de cambio creada exitosamente');
      } else {
        await this.useCase.updateRate(this.selectedRate!.id, event.data);
        this.toastService.success('Tasa de cambio actualizada exitosamente');
      }
      this.closeForm();
    } catch (error: any) {
      let msg = error.error?.message || error.message || 'Error al procesar la solicitud';
      
      // Traducciones amigables para el usuario
      if (msg.includes('already exists for pair')) {
        msg = 'Ya existe una tasa de cambio activa para este par de divisas.';
      } else if (msg.includes('already exists')) {
        msg = 'Esta tasa de cambio ya está registrada en el sistema.';
      } else if (msg.includes('Self exchange rate must be 1')) {
        msg = 'La tasa de cambio para una misma moneda (ej. USD a USD) debe ser exactamente 1.';
      }
      
      this.toastService.error(msg, 'No se pudo guardar');
    } finally {
      this.isSubmitting = false;
    }
  }

  async deleteRate(id: string) {
    if (confirm('¿Estás seguro de eliminar este tipo de cambio?')) {
      const success = await this.useCase.deleteRate(id);
      if (success) {
        this.toastService.success('Tasa de cambio eliminada');
      } else {
        this.toastService.error('No se pudo eliminar la tasa de cambio');
      }
    }
  }
}

```

### `app/pages/landing-page/landing-page.component.ts`

```typescript
import { Component } from '@angular/core';
import { LandingPageComponent as LandingFeature } from '../../features/landing/ui/landing-page/landing-page.component';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [LandingFeature],
  template: `<app-landing-page></app-landing-page>`
})
export class LandingPageComponent {}
```

### `app/pages/limits-rules-page/limits-rules-page.component.ts`

```typescript
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe, CurrencyPipe } from '@angular/common';
import { LimitRulesListUseCase, LimitRuleFormComponent } from '../../features/limits-management';
import { HasPermissionPipe } from '../../shared/api';
import { ToastService } from '../../shared/ui/toast/toast.service';
import { LimitRuleResponse } from '../../entities/limits';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-limits-rules-page',
  standalone: true,
  imports: [CommonModule, LimitRuleFormComponent, HasPermissionPipe, LucideAngularModule],
  providers: [DatePipe, CurrencyPipe],
  template: `
    <div class="space-y-6 relative">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold tracking-tight text-foreground">Reglas de Límite Operativo</h2>
          <p class="text-muted-foreground">Administra los límites transaccionales y reglas de prevención aplicadas a los usuarios y cuentas.</p>
        </div>
        
        <button 
          *ngIf="'limits.create' | hasPermission"
          (click)="openCreateForm()"
          type="button"
          class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2 shadow-sm">
          <lucide-icon name="plus" [size]="16"></lucide-icon>
          Nueva Regla
        </button>
      </div>

      <!-- Estado: Error Lista -->
      @if (useCase.status() === 'error') {
        <div class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-destructive">Error al cargar reglas</h3>
            <p class="text-sm text-destructive/80 mt-1">{{ useCase.error() }}</p>
            <button (click)="loadRules()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      <!-- Estado: Cargando Inicial -->
      @if (useCase.status() === 'loading' && useCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center p-12 text-muted-foreground gap-4">
          <svg class="animate-spin h-8 w-8 text-primary" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando reglas...</p>
        </div>
      }

      <!-- Estado: Éxito o Recargando -->
      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <div class="bg-card border border-border rounded-xl shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-sm text-left">
              <thead class="text-xs text-muted-foreground uppercase bg-muted/50 border-b border-border">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Regla</th>
                  <th scope="col" class="px-6 py-4 font-medium">Alcance</th>
                  <th scope="col" class="px-6 py-4 font-medium">Tipo / Moneda</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Límites (Mín - Máx)</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-border">
                @for (rule of useCase.data(); track rule.id) {
                  <tr class="hover:bg-muted/30 transition-colors">
                    <td class="px-6 py-4">
                      <div class="font-bold text-foreground">{{ rule.name }}</div>
                      <div class="text-xs text-muted-foreground mt-0.5">Cód: {{ rule.code }}</div>
                    </td>
                    <td class="px-6 py-4">
                      <div class="font-medium text-foreground">{{ rule.scopeType }}</div>
                      <div class="text-xs text-muted-foreground mt-0.5 flex items-center gap-1">
                        <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                        {{ rule.period }}
                      </div>
                    </td>
                    <td class="px-6 py-4">
                      <div class="flex items-center gap-2">
                        @if (rule.transactionType) {
                          <span class="px-2 py-0.5 bg-secondary text-secondary-foreground text-[10px] font-bold rounded-sm uppercase tracking-wide">{{ rule.transactionType }}</span>
                        } @else {
                          <span class="text-xs text-muted-foreground italic">Cualquiera</span>
                        }
                        
                        @if (rule.currency) {
                          <span class="px-2 py-0.5 bg-primary/10 text-primary text-[10px] font-bold rounded-sm">{{ rule.currency }}</span>
                        }
                      </div>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="font-medium text-foreground">
                        @if (rule.minAmount !== null && rule.maxAmount !== null) {
                          {{ rule.minAmount | number:'1.2-2' }} - {{ rule.maxAmount | number:'1.2-2' }}
                        } @else if (rule.maxAmount !== null) {
                          Máx: {{ rule.maxAmount | number:'1.2-2' }}
                        } @else {
                          Ilimitado
                        }
                      </div>
                      @if (rule.maxCount) {
                        <div class="text-xs text-muted-foreground mt-0.5">Máx {{ rule.maxCount }} ops</div>
                      }
                    </td>
                    <td class="px-6 py-4 text-center space-y-1">
                      <div>
                        <span class="px-2.5 py-1 text-[10px] font-semibold rounded-full"
                              [ngClass]="rule.active ? 'bg-green-500/10 text-green-600' : 'bg-gray-500/10 text-gray-600'">
                          {{ rule.active ? 'ACTIVA' : 'INACTIVA' }}
                        </span>
                      </div>
                      @if (rule.requireReviewExceed) {
                        <div>
                          <span class="px-2 py-0.5 text-[9px] font-semibold rounded bg-amber-500/10 text-amber-600" title="Requiere revisión al exceder">
                            REVISIÓN req.
                          </span>
                        </div>
                      }
                    </td>
                    <td class="px-6 py-4 text-right space-x-3">
                      <button *ngIf="'limits.update' | hasPermission" (click)="openEditForm(rule)" class="text-primary hover:underline text-xs font-medium">Editar</button>
                      <button *ngIf="'limits.delete' | hasPermission" (click)="deleteRule(rule.id)" class="text-destructive hover:underline text-xs font-medium">Eliminar</button>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-muted-foreground">
                      No hay reglas de límite registradas.
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }

      @if (isFormOpen) {
        <app-limit-rule-form
          [rule]="selectedRule"
          [loading]="isSubmitting"
          (formSubmit)="onFormSubmit($event)"
          (cancel)="closeForm()"
        ></app-limit-rule-form>
      }
    </div>
  `
})
export class LimitsRulesPageComponent implements OnInit {
  public readonly useCase = inject(LimitRulesListUseCase);
  private readonly toastService = inject(ToastService);

  isFormOpen = false;
  selectedRule: LimitRuleResponse | null = null;
  isSubmitting = false;

  ngOnInit() {
    this.loadRules();
  }

  loadRules() {
    this.useCase.loadRules();
  }

  openCreateForm() {
    this.selectedRule = null;
    this.isFormOpen = true;
  }

  openEditForm(rule: LimitRuleResponse) {
    this.selectedRule = rule;
    this.isFormOpen = true;
  }

  closeForm() {
    this.isFormOpen = false;
    this.selectedRule = null;
  }

  async onFormSubmit(event: { type: 'create' | 'update', data: any }) {
    this.isSubmitting = true;
    try {
      if (event.type === 'create') {
        await this.useCase.createRule(event.data);
        this.toastService.success('Regla de límite creada exitosamente');
      } else {
        await this.useCase.updateRule(this.selectedRule!.id, event.data);
        this.toastService.success('Regla de límite actualizada exitosamente');
      }
      this.closeForm();
    } catch (error: any) {
      let msg = error.error?.message || error.message || 'Error al procesar la solicitud';
      
      // Traducciones amigables para el usuario
      if (msg.includes('already exists')) {
        msg = 'Ya existe una regla de límite con este código único.';
      }
      
      this.toastService.error(msg, 'No se pudo guardar la regla');
    } finally {
      this.isSubmitting = false;
    }
  }

  async deleteRule(id: string) {
    if (confirm('¿Estás seguro de eliminar (desactivar) esta regla de límite?')) {
      const success = await this.useCase.deleteRule(id);
      if (success) {
        this.toastService.success('Regla desactivada exitosamente');
      } else {
        this.toastService.error('No se pudo desactivar la regla');
      }
    }
  }
}

```

### `app/pages/login-page/login-page.component.ts`

```typescript
import { Component, effect, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { LoginFormComponent, LoginUseCase } from '../../features/auth';
import { LoginRequest } from '../../entities/auth';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, LoginFormComponent],
  template: `
    <div class="min-h-screen flex items-center justify-center p-4 bg-muted/30">
      <app-login-form
        [status]="loginUseCase.status()"
        [error]="loginUseCase.error()"
        (loginSubmit)="handleLogin($event)"
      />
    </div>
  `
})
export class LoginPageComponent implements OnInit {
  public readonly loginUseCase = inject(LoginUseCase);
  private readonly router = inject(Router);

  constructor() {
    // Escuchar el estado de éxito para navegar al dashboard
    effect(() => {
      if (this.loginUseCase.status() === 'success') {
        this.router.navigate(['/dashboard']); // Ajustar a la ruta real de tu dashboard
      }
    });
  }

  ngOnInit(): void {
    this.loginUseCase.resetState();
  }

  handleLogin(request: LoginRequest): void {
    this.loginUseCase.login(request);
  }
}

```

### `app/pages/onboarding-page/onboarding-page.component.ts`

```typescript
import { Component, inject, OnInit } from '@angular/core';
import { SignupFormComponent, SignupUseCase } from '../../features/onboarding';
import { PublicSignupRequest } from '../../entities/tenant';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-onboarding-page',
  standalone: true,
  imports: [CommonModule, SignupFormComponent],
  template: `
    <div class="min-h-screen flex items-center justify-center p-4 bg-muted/30">
      <app-signup-form
        [loading]="signupUseCase.loading()"
        [error]="signupUseCase.error()"
        [success]="signupUseCase.success()"
        (formSubmit)="handleSignup($event)"
      />
    </div>
  `
})
export class OnboardingPageComponent implements OnInit {
  // Inyección del caso de uso (Clean Architecture)
  public readonly signupUseCase = inject(SignupUseCase);

  ngOnInit(): void {
    // Limpiamos el estado al entrar a la página
    this.signupUseCase.resetState();
  }

  handleSignup(request: PublicSignupRequest): void {
    // Delegamos la lógica de negocio al UseCase
    this.signupUseCase.signup(request);
  }
}

```

### `app/pages/platform-audit-page/platform-audit-page.component.ts`

```typescript
// pages/platform-audit-page/platform-audit-page.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule } from 'lucide-angular';
import { PlatformAuditListUseCase } from '../../features/platform/application/platform-audit-list.usecase';
import { PlatformAuditTableComponent } from '../../features/platform/ui/platform-audit-table/platform-audit-table.component';
import { AuditEvent } from '../../entities/platform/api/platform.service';

@Component({
  selector: 'app-platform-audit-page',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, PlatformAuditTableComponent],
  template: `
    <div class="p-6">
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-[#2E7D32]">Auditoría de la Plataforma</h1>
        <p class="text-sm text-[#666666] mt-1">Registro de eventos y acciones del sistema</p>
      </div>

      <app-platform-audit-table
        [events]="auditEvents"
        [isLoading]="isLoading">
      </app-platform-audit-table>
    </div>
  `
})
export class PlatformAuditPageComponent implements OnInit {
  private listUseCase = inject(PlatformAuditListUseCase);
  private cdr = inject(ChangeDetectorRef);

  // ✅ Getters con tipos explícitos
  get auditEvents(): AuditEvent[] {
    return this.listUseCase.events();
  }

  get isLoading(): boolean {
    return this.listUseCase.status() === 'loading';
  }

  ngOnInit(): void {
    this.loadAuditEvents();
  }

  async loadAuditEvents(): Promise<void> {
    await this.listUseCase.loadAuditEvents(50);
    this.cdr.detectChanges();
  }
}
```

### `app/pages/platform-dashboard-page/platform-dashboard-page.component.ts`

```typescript
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { PlatformStorageService } from '../../features/platform/lib/platform-storage.service';

@Component({
  selector: 'app-platform-dashboard-page',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="p-6">
      <h1 class="text-2xl font-bold text-[#2E7D32] mb-6">Dashboard de la Plataforma</h1>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div class="bg-white p-6 rounded-xl border border-[#C8E6C9] shadow-sm">
          <h3 class="text-lg font-semibold text-[#2E7D32]">Total Tenants</h3>
          <p class="text-3xl font-bold mt-2">0</p>
        </div>
        <div class="bg-white p-6 rounded-xl border border-[#C8E6C9] shadow-sm">
          <h3 class="text-lg font-semibold text-[#2E7D32]">Planes Activos</h3>
          <p class="text-3xl font-bold mt-2">0</p>
        </div>
        <div class="bg-white p-6 rounded-xl border border-[#C8E6C9] shadow-sm">
          <h3 class="text-lg font-semibold text-[#2E7D32]">Suscripciones</h3>
          <p class="text-3xl font-bold mt-2">0</p>
        </div>
      </div>
    </div>
  `
})
export class PlatformDashboardPageComponent {
  private router = inject(Router);
  private platformStorage = inject(PlatformStorageService);

  onLogout(): void {
    this.platformStorage.clear();
    this.router.navigate(['/platform/login']);
  }
}
```

### `app/pages/platform-login-page/platform-login-page.component.ts`

```typescript
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PlatformLoginFormComponent } from '../../features/platform/ui/platform-login-form/platform-login-form.component';
import { PlatformLoginUseCase } from '../../features/platform/application/platform-login.usecase';

@Component({
  selector: 'app-platform-login-page',
  standalone: true,
  imports: [CommonModule, PlatformLoginFormComponent],
  template: `
    <app-platform-login-form 
      [status]="loginUseCase.status()"
      [error]="loginUseCase.error()"
      (loginSubmit)="onLogin($event)">
    </app-platform-login-form>
  `
})
export class PlatformLoginPageComponent {
  private router = inject(Router);
  protected loginUseCase = inject(PlatformLoginUseCase);

  async onLogin(credentials: { email: string; password: string }) {
    await this.loginUseCase.login(credentials);
    if (this.loginUseCase.status() === 'success') {
      this.router.navigate(['/platform/dashboard']);
    }
  }
}
```

### `app/pages/platform-plans-page/platform-plans-page.component.ts`

```typescript
// pages/platform-plans-page/platform-plans-page.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LucideAngularModule, Plus } from 'lucide-angular';
import { ToastService } from '../../shared/ui/toast/toast.service';
import { PlatformPlanListUseCase } from '../../features/platform/application/platform-plan-list.usecase';
import { PlatformPlanCreateUseCase } from '../../features/platform/application/platform-plan-create.usecase';
import { PlatformPlanActivateUseCase } from '../../features/platform/application/platform-plan-activate.usecase';
import { PlatformPlanDeactivateUseCase } from '../../features/platform/application/platform-plan-deactivate.usecase';
import { PlatformPlanTableComponent } from '../../features/platform/ui/platform-plan-table/platform-plan-table.component';
import { PlatformPlanFormComponent } from '../../features/platform/ui/platform-plan-form/platform-plan-form.component';

@Component({
  selector: 'app-platform-plans-page',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule, 
    LucideAngularModule,
    PlatformPlanTableComponent, 
    PlatformPlanFormComponent
  ],
  template: `
    <div class="p-6">
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold text-[#2E7D32]">Gestión de Planes</h1>
        <button (click)="showCreateForm = !showCreateForm" 
                class="bg-[#2E7D32] text-white px-4 py-2 rounded-lg hover:bg-[#4CAF50] transition-colors flex items-center gap-2">
          <lucide-icon name="plus" class="h-5 w-5"></lucide-icon>
          Nuevo Plan
        </button>
      </div>

      @if (showCreateForm) {
        <div class="mb-6">
          <app-platform-plan-form
            [isLoading]="createUseCase.status() === 'loading'"
            [error]="createUseCase.error()"
            (submitForm)="onCreatePlan($event)"
            (cancel)="showCreateForm = false">
          </app-platform-plan-form>
        </div>
      }

      <app-platform-plan-table
        [plans]="listUseCase.plans()"
        [isLoading]="listUseCase.status() === 'loading'"
        (viewDetails)="viewDetails($event)"
        (activate)="onActivate($event)"
        (deactivate)="onDeactivate($event)">
      </app-platform-plan-table>

      <!-- Modal de detalles -->
      @if (selectedPlan) {
        <div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" (click)="closeModal()">
          <div class="bg-white rounded-xl max-w-lg w-full mx-4 p-6" (click)="$event.stopPropagation()">
            <div class="flex justify-between items-center mb-4">
              <h2 class="text-xl font-bold text-[#2E7D32]">Detalle del Plan</h2>
              <button (click)="closeModal()" class="text-[#666666] hover:text-[#2E7D32]">
                <lucide-icon name="x" class="h-5 w-5"></lucide-icon>
              </button>
            </div>
            <div class="space-y-3">
              <div><span class="font-medium text-[#2E7D32]">ID:</span> {{ selectedPlan.id }}</div>
              <div><span class="font-medium text-[#2E7D32]">Código:</span> {{ selectedPlan.code }}</div>
              <div><span class="font-medium text-[#2E7D32]">Nombre:</span> {{ selectedPlan.name }}</div>
              <div><span class="font-medium text-[#2E7D32]">Descripción:</span> {{ selectedPlan.description }}</div>
              <div><span class="font-medium text-[#2E7D32]">Tipo:</span> {{ selectedPlan.planType }}</div>
              <div><span class="font-medium text-[#2E7D32]">Máx. usuarios:</span> {{ selectedPlan.maxUsers }}</div>
              <div><span class="font-medium text-[#2E7D32]">Máx. roles:</span> {{ selectedPlan.maxRoles }}</div>
              <div><span class="font-medium text-[#2E7D32]">Días de prueba:</span> {{ selectedPlan.trialDays || 'N/A' }}</div>
              <div><span class="font-medium text-[#2E7D32]">Estado:</span> 
                <span [class]="selectedPlan.active ? 'text-[#2E7D32]' : 'text-[#C62828]'">{{ selectedPlan.active ? 'ACTIVO' : 'INACTIVO' }}</span>
              </div>
            </div>
            <div class="flex justify-end mt-6">
              <button (click)="closeModal()" class="px-4 py-2 bg-[#2E7D32] text-white rounded-lg hover:bg-[#4CAF50]">Cerrar</button>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class PlatformPlansPageComponent implements OnInit {
  protected listUseCase = inject(PlatformPlanListUseCase);
  protected createUseCase = inject(PlatformPlanCreateUseCase);
  protected activateUseCase = inject(PlatformPlanActivateUseCase);
  protected deactivateUseCase = inject(PlatformPlanDeactivateUseCase);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  showCreateForm = false;
  selectedPlan: any = null;

  ngOnInit(): void {
    this.loadPlans();
  }

  async loadPlans(): Promise<void> {
    await this.listUseCase.loadPlans();
    this.cdr.detectChanges();
  }

  async onCreatePlan(data: any): Promise<void> {
    const success = await this.createUseCase.createPlan(data);
    if (success) {
      this.toastService.success('Plan creado exitosamente');
      this.showCreateForm = false;
      this.createUseCase.resetState();
      await this.loadPlans();
    } else {
      this.toastService.error(this.createUseCase.error() || 'Error al crear plan');
    }
  }

  async onActivate(id: string): Promise<void> {
    const plan = this.listUseCase.plans().find(p => p.id === id);
    if (!plan) return;

    const confirmActivate = confirm(`¿Estás seguro de que deseas activar el plan "${plan.name}"?`);
    if (!confirmActivate) return;

    const success = await this.activateUseCase.activatePlan(id);
    if (success) {
      this.toastService.success(`Plan "${plan.name}" activado exitosamente`);
      this.activateUseCase.resetState();
      await this.loadPlans();
    } else {
      this.toastService.error(this.activateUseCase.error() || 'Error al activar plan');
    }
  }

  async onDeactivate(id: string): Promise<void> {
    const plan = this.listUseCase.plans().find(p => p.id === id);
    if (!plan) return;

    const confirmDeactivate = confirm(`¿Estás seguro de que deseas desactivar el plan "${plan.name}"?`);
    if (!confirmDeactivate) return;

    const success = await this.deactivateUseCase.deactivatePlan(id);
    if (success) {
      this.toastService.success(`Plan "${plan.name}" desactivado exitosamente`);
      this.deactivateUseCase.resetState();
      await this.loadPlans();
    } else {
      this.toastService.error(this.deactivateUseCase.error() || 'Error al desactivar plan');
    }
  }

  viewDetails(id: string): void {
    const plan = this.listUseCase.plans().find(p => p.id === id);
    if (plan) {
      this.selectedPlan = plan;
      this.cdr.detectChanges();
    }
  }

  closeModal(): void {
    this.selectedPlan = null;
    this.cdr.detectChanges();
  }
}
```

### `app/pages/platform-subscriptions-page/platform-subscriptions-page.component.ts`

```typescript
// pages/platform-subscriptions-page/platform-subscriptions-page.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LucideAngularModule, Plus, X } from 'lucide-angular';
import { ToastService } from '../../shared/ui/toast/toast.service';
import { PlatformSubscriptionListUseCase } from '../../features/platform/application/platform-subscription-list.usecase';
import { PlatformSubscriptionAssignUseCase } from '../../features/platform/application/platform-subscription-assign.usecase';
import { PlatformSubscriptionGetByIdUseCase } from '../../features/platform/application/platform-subscription-get-by-id.usecase';
import { PlatformSubscriptionTableComponent } from '../../features/platform/ui/platform-subscription-table/platform-subscription-table.component';
import { PlatformSubscriptionAssignFormComponent } from '../../features/platform/ui/platform-subscription-assign-form/platform-subscription-assign-form.component';

@Component({
  selector: 'app-platform-subscriptions-page',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule, 
    LucideAngularModule,
    PlatformSubscriptionTableComponent, 
    PlatformSubscriptionAssignFormComponent
  ],
  template: `
    <div class="p-6">
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold text-[#2E7D32]">Gestión de Suscripciones</h1>
        <button (click)="showAssignForm = !showAssignForm" 
                class="bg-[#2E7D32] text-white px-4 py-2 rounded-lg hover:bg-[#4CAF50] transition-colors flex items-center gap-2">
          <lucide-icon name="plus" class="h-5 w-5"></lucide-icon>
          Asignar Suscripción
        </button>
      </div>

      @if (showAssignForm) {
        <div class="mb-6">
          <app-platform-subscription-assign-form
            [isLoading]="assignUseCase.status() === 'loading'"
            [error]="assignUseCase.error()"
            (submitForm)="onAssignSubscription($event)"
            (cancel)="showAssignForm = false">
          </app-platform-subscription-assign-form>
        </div>
      }

      <app-platform-subscription-table
        [subscriptions]="listUseCase.subscriptions()"
        [isLoading]="listUseCase.status() === 'loading'"
        (viewDetails)="viewDetails($event)">
      </app-platform-subscription-table>

      <!-- Modal de detalles -->
      @if (selectedSubscription) {
        <div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" (click)="closeModal()">
          <div class="bg-white rounded-xl max-w-lg w-full mx-4 p-6" (click)="$event.stopPropagation()">
            <div class="flex justify-between items-center mb-4">
              <h2 class="text-xl font-bold text-[#2E7D32]">Detalle de Suscripción</h2>
              <button (click)="closeModal()" class="text-[#666666] hover:text-[#2E7D32]">
                <lucide-icon name="x" class="h-5 w-5"></lucide-icon>
              </button>
            </div>
            <div class="space-y-3">
              <div><span class="font-medium text-[#2E7D32]">ID:</span> {{ selectedSubscription.id }}</div>
              <div><span class="font-medium text-[#2E7D32]">Tenant:</span> {{ selectedSubscription.tenantName }} ({{ selectedSubscription.tenantSlug }})</div>
              <div><span class="font-medium text-[#2E7D32]">Plan:</span> {{ selectedSubscription.planName }} ({{ selectedSubscription.planCode }})</div>
              <div><span class="font-medium text-[#2E7D32]">Estado:</span> 
                <span [class]="selectedSubscription.status === 'ACTIVE' ? 'text-[#2E7D32]' : 'text-[#FF9800]'">{{ selectedSubscription.status }}</span>
              </div>
              <div><span class="font-medium text-[#2E7D32]">Trial:</span> {{ selectedSubscription.trial ? 'Sí' : 'No' }}</div>
              <div><span class="font-medium text-[#2E7D32]">Inicio:</span> {{ selectedSubscription.startedAt | date:'medium' }}</div>
              <div><span class="font-medium text-[#2E7D32]">Expira:</span> {{ selectedSubscription.expiresAt | date:'medium' }}</div>
              <div><span class="font-medium text-[#2E7D32]">Días restantes:</span> 
                <span [class]="selectedSubscription.remainingDays <= 7 ? 'text-[#FF9800] font-bold' : ''">{{ selectedSubscription.remainingDays }} días</span>
              </div>
              <div><span class="font-medium text-[#2E7D32]">Máx. usuarios:</span> {{ selectedSubscription.maxUsers }}</div>
              <div><span class="font-medium text-[#2E7D32]">Máx. roles:</span> {{ selectedSubscription.maxRoles }}</div>
            </div>
            <div class="flex justify-end mt-6">
              <button (click)="closeModal()" class="px-4 py-2 bg-[#2E7D32] text-white rounded-lg hover:bg-[#4CAF50]">Cerrar</button>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class PlatformSubscriptionsPageComponent implements OnInit {
  protected listUseCase = inject(PlatformSubscriptionListUseCase);
  protected assignUseCase = inject(PlatformSubscriptionAssignUseCase);
  protected detailUseCase = inject(PlatformSubscriptionGetByIdUseCase);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  showAssignForm = false;
  selectedSubscription: any = null;

  ngOnInit(): void {
    this.loadSubscriptions();
  }

  async loadSubscriptions(): Promise<void> {
    await this.listUseCase.loadSubscriptions();
    this.cdr.detectChanges();
  }

  async onAssignSubscription(data: { tenantId: string; planCode: string; overrideTrialDays?: number }): Promise<void> {
    const success = await this.assignUseCase.assignSubscription({
      tenantId: data.tenantId,
      planCode: data.planCode,
      overrideTrialDays: data.overrideTrialDays
    });
    
    if (success) {
      this.toastService.success('Suscripción asignada exitosamente');
      this.showAssignForm = false;
      this.assignUseCase.resetState();
      await this.loadSubscriptions();
    } else {
      this.toastService.error(this.assignUseCase.error() || 'Error al asignar suscripción');
    }
  }

  async viewDetails(id: string): Promise<void> {
    await this.detailUseCase.loadSubscription(id);
    this.selectedSubscription = this.detailUseCase.subscription();
    this.cdr.detectChanges();
  }

  closeModal(): void {
    this.selectedSubscription = null;
    this.detailUseCase.resetState();
    this.cdr.detectChanges();
  }
}
```

### `app/pages/platform-tenants-page/platform-tenants-page.component.ts`

```typescript
// pages/platform-tenants-page/platform-tenants-page.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LucideAngularModule, Plus, X } from 'lucide-angular';
import { ToastService } from '../../shared/ui/toast/toast.service';
import { PlatformTenantListUseCase } from '../../features/platform/application/platform-tenant-list.usecase';
import { PlatformTenantCreateUseCase } from '../../features/platform/application/platform-tenant-create.usecase';
import { PlatformTenantActivateUseCase } from '../../features/platform/application/platform-tenant-activate.usecase';
import { PlatformTenantDeactivateUseCase } from '../../features/platform/application/platform-tenant-deactivate.usecase';
import { PlatformTenantTableComponent } from '../../features/platform/ui/platform-tenant-table/platform-tenant-table.component';
import { PlatformTenantFormComponent } from '../../features/platform/ui/platform-tenant-form/platform-tenant-form.component';

@Component({
  selector: 'app-platform-tenants-page',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule, 
    LucideAngularModule,
    PlatformTenantTableComponent, 
    PlatformTenantFormComponent
  ],
  template: `
    <div class="p-6">
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold text-[#2E7D32]">Gestión de Tenants</h1>
        <button (click)="showCreateForm = !showCreateForm" 
                class="bg-[#2E7D32] text-white px-4 py-2 rounded-lg hover:bg-[#4CAF50] transition-colors flex items-center gap-2">
          <lucide-icon name="plus" class="h-5 w-5"></lucide-icon>
          Nuevo Tenant
        </button>
      </div>

      @if (showCreateForm) {
        <div class="mb-6">
          <app-platform-tenant-form
            [isLoading]="createUseCase.status() === 'loading'"
            [error]="createUseCase.error()"
            (submitForm)="onCreateTenant($event)"
            (cancel)="showCreateForm = false">
          </app-platform-tenant-form>
        </div>
      }

      <!-- Tabla con indicador de carga -->
      <app-platform-tenant-table
        [tenants]="listUseCase.tenants()"
        [isLoading]="listUseCase.status() === 'loading'"
        (viewDetails)="viewDetails($event)"
        (activate)="onActivate($event)"
        (deactivate)="onDeactivate($event)">
      </app-platform-tenant-table>

      <!-- Modal de detalles -->
      @if (selectedTenant) {
        <div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" (click)="closeModal()">
          <div class="bg-white rounded-xl max-w-lg w-full mx-4 p-6" (click)="$event.stopPropagation()">
            <div class="flex justify-between items-center mb-4">
              <h2 class="text-xl font-bold text-[#2E7D32]">Detalle del Tenant</h2>
              <button (click)="closeModal()" class="text-[#666666] hover:text-[#2E7D32]">
                <lucide-icon name="x" class="h-5 w-5"></lucide-icon>
              </button>
            </div>
            <div class="space-y-3">
              <div><span class="font-medium text-[#2E7D32]">ID:</span> {{ selectedTenant.id }}</div>
              <div><span class="font-medium text-[#2E7D32]">Nombre:</span> {{ selectedTenant.name }}</div>
              <div><span class="font-medium text-[#2E7D32]">Slug:</span> {{ selectedTenant.slug }}</div>
              <div><span class="font-medium text-[#2E7D32]">Schema:</span> {{ selectedTenant.schemaName }}</div>
              <div><span class="font-medium text-[#2E7D32]">Estado:</span> 
                <span [class]="selectedTenant.active ? 'text-[#2E7D32]' : 'text-[#C62828]'">{{ selectedTenant.status }}</span>
              </div>
              <div><span class="font-medium text-[#2E7D32]">Creado:</span> {{ selectedTenant.createdAt | date:'medium' }}</div>
              <div><span class="font-medium text-[#2E7D32]">Actualizado:</span> {{ selectedTenant.updatedAt | date:'medium' }}</div>
            </div>
            <div class="flex justify-end mt-6">
              <button (click)="closeModal()" class="px-4 py-2 bg-[#2E7D32] text-white rounded-lg hover:bg-[#4CAF50]">Cerrar</button>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class PlatformTenantsPageComponent implements OnInit {
  protected listUseCase = inject(PlatformTenantListUseCase);
  protected createUseCase = inject(PlatformTenantCreateUseCase);
  protected activateUseCase = inject(PlatformTenantActivateUseCase);
  protected deactivateUseCase = inject(PlatformTenantDeactivateUseCase);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  showCreateForm = false;
  selectedTenant: any = null;

  ngOnInit(): void {
    this.loadTenants();
  }

  async loadTenants(): Promise<void> {
    await this.listUseCase.loadTenants();
    this.cdr.detectChanges();
  }

  async onCreateTenant(data: { name: string; slug: string; planCode: string }): Promise<void> {
    const success = await this.createUseCase.createTenant(data);
    if (success) {
      this.toastService.success('Tenant creado exitosamente');
      this.showCreateForm = false;
      this.createUseCase.resetState();
      await this.loadTenants();
    } else {
      this.toastService.error(this.createUseCase.error() || 'Error al crear tenant');
    }
  }

  // ✅ Activar tenant
  async onActivate(id: string): Promise<void> {
    const tenant = this.listUseCase.tenants().find(t => t.id === id);
    if (!tenant) return;

    const confirmActivate = confirm(`¿Estás seguro de que deseas activar el tenant "${tenant.name}"?`);
    if (!confirmActivate) return;

    const success = await this.activateUseCase.activateTenant(id);
    if (success) {
      this.toastService.success(`Tenant "${tenant.name}" activado exitosamente`);
      this.activateUseCase.resetState();
      await this.loadTenants();
    } else {
      this.toastService.error(this.activateUseCase.error() || 'Error al activar tenant');
    }
  }

  // ✅ Desactivar tenant
  async onDeactivate(id: string): Promise<void> {
    const tenant = this.listUseCase.tenants().find(t => t.id === id);
    if (!tenant) return;

    const confirmDeactivate = confirm(`¿Estás seguro de que deseas desactivar el tenant "${tenant.name}"?\n\nLos usuarios no podrán acceder hasta que sea reactivado.`);
    if (!confirmDeactivate) return;

    const success = await this.deactivateUseCase.deactivateTenant(id);
    if (success) {
      this.toastService.success(`Tenant "${tenant.name}" desactivado exitosamente`);
      this.deactivateUseCase.resetState();
      await this.loadTenants();
    } else {
      this.toastService.error(this.deactivateUseCase.error() || 'Error al desactivar tenant');
    }
  }

  viewDetails(id: string): void {
    const tenant = this.listUseCase.tenants().find(t => t.id === id);
    if (tenant) {
      this.selectedTenant = tenant;
      this.cdr.detectChanges();
    }
  }

  closeModal(): void {
    this.selectedTenant = null;
    this.cdr.detectChanges();
  }
}
```

### `app/pages/roles-page/roles-page.component.ts`

```typescript
import { Component, effect, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PermissionListUseCase, RoleFormComponent, RoleFormUseCase, RoleListUseCase, RoleTableComponent } from '../../features/role-management';
import { CreateTenantRoleRequest, TenantRoleResponse, UpdateTenantRoleRequest } from '../../entities/access';

@Component({
  selector: 'app-roles-page',
  standalone: true,
  imports: [CommonModule, RoleTableComponent, RoleFormComponent],
  template: `
    <div class="space-y-6 relative">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold tracking-tight text-foreground">Roles y Permisos</h2>
          <p class="text-muted-foreground">Define los niveles de acceso para tu equipo.</p>
        </div>
        
        <button 
          type="button"
          (click)="openRoleModal()"
          class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2 shadow-sm">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" x2="12" y1="5" y2="19"/><line x1="5" x2="19" y1="12" y2="12"/></svg>
          Nuevo Rol
        </button>
      </div>

      <!-- Estado: Error Lista -->
      @if (roleListUseCase.status() === 'error') {
        <div class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-destructive">Error al cargar roles</h3>
            <p class="text-sm text-destructive/80 mt-1">{{ roleListUseCase.error() }}</p>
            <button (click)="retry()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      <!-- Estado: Cargando Inicial -->
      @if (roleListUseCase.status() === 'loading' && roleListUseCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center p-12 text-muted-foreground gap-4">
          <svg class="animate-spin h-8 w-8 text-primary" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando roles...</p>
        </div>
      }

      <!-- Estado: Éxito o Recargando -->
      @if (roleListUseCase.status() === 'success' || (roleListUseCase.status() === 'loading' && roleListUseCase.data().length > 0)) {
        @if (roleListUseCase.status() === 'loading') {
          <div class="absolute top-0 left-1/2 transform -translate-x-1/2 bg-primary text-primary-foreground px-3 py-1 rounded-b-md text-xs shadow-md animate-pulse z-10">Actualizando...</div>
        }
        
        <app-role-table 
          [roles]="roleListUseCase.data()"
          (edit)="openRoleModal($event)"
          (toggleStatus)="handleToggleStatus($event.id, $event.currentStatus)">
        </app-role-table>
      }

      <!-- Toast Éxito -->
      @if (showSuccessToast()) {
        <div class="fixed bottom-4 right-4 bg-primary text-primary-foreground px-4 py-3 rounded-md shadow-lg flex items-center gap-2 animate-in fade-in slide-in-from-bottom-4 z-50">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
          Operación exitosa
        </div>
      }
    </div>

    <!-- Modal Formulario de Rol -->
    @if (isModalOpen()) {
      <div class="fixed inset-0 z-50 flex items-center justify-end">
        <!-- Backdrop -->
        <div class="absolute inset-0 bg-background/80 backdrop-blur-sm animate-in fade-in" (click)="closeRoleModal()"></div>
        
        <!-- Slide-over panel -->
        <div class="relative bg-card w-full max-w-2xl h-full shadow-2xl border-l border-border flex flex-col animate-in slide-in-from-right duration-300">
          
          <!-- Header -->
          <div class="flex items-center justify-between px-6 py-4 border-b border-border bg-card">
            <h3 class="text-lg font-semibold text-foreground">
              {{ roleToEdit() ? 'Editar Rol' : 'Crear Nuevo Rol' }}
            </h3>
            <button (click)="closeRoleModal()" class="text-muted-foreground hover:text-foreground transition-colors p-1 rounded-md hover:bg-muted" [disabled]="roleFormUseCase.status() === 'loading'">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" x2="6" y1="6" y2="18"/><line x1="6" x2="18" y1="6" y2="18"/></svg>
            </button>
          </div>

          <!-- Body -->
          <div class="flex-1 overflow-hidden">
            @if (roleFormUseCase.error()) {
              <div class="m-6 p-4 bg-destructive/10 text-destructive border border-destructive/20 rounded-lg text-sm">
                {{ roleFormUseCase.error() }}
              </div>
            }

            @if (permissionListUseCase.status() === 'loading') {
              <div class="flex justify-center p-12 text-muted-foreground">Cargando permisos disponibles...</div>
            } @else if (permissionListUseCase.status() === 'error') {
              <div class="m-6 p-4 bg-destructive/10 text-destructive rounded-lg text-sm">
                No se pudieron cargar los permisos: {{ permissionListUseCase.error() }}
              </div>
            } @else {
              <app-role-form
                [status]="roleFormUseCase.status()"
                [roleToEdit]="roleToEdit()"
                [permissions]="permissionListUseCase.data()"
                (submitCreate)="handleCreateRole($event)"
                (submitUpdate)="handleUpdateRole($event.id, $event.request)"
                (cancel)="closeRoleModal()">
              </app-role-form>
            }
          </div>
        </div>
      </div>
    }
  `
})
export class RolesPageComponent implements OnInit {
  public readonly roleListUseCase = inject(RoleListUseCase);
  public readonly permissionListUseCase = inject(PermissionListUseCase);
  public readonly roleFormUseCase = inject(RoleFormUseCase);

  public readonly isModalOpen = signal(false);
  public readonly roleToEdit = signal<TenantRoleResponse | null>(null);
  public readonly showSuccessToast = signal(false);

  constructor() {
    effect(() => {
      if (this.roleFormUseCase.status() === 'success') {
        this.closeRoleModal();
        this.roleFormUseCase.resetState();
        this.roleListUseCase.reloadRoles();
        this.triggerSuccessToast();
      }
    }, { allowSignalWrites: true });
  }

  ngOnInit(): void {
    this.roleListUseCase.loadRoles();
    this.permissionListUseCase.loadPermissions(); // Pre-cargar permisos
  }

  retry(): void {
    this.roleListUseCase.loadRoles();
  }

  openRoleModal(role: TenantRoleResponse | null = null): void {
    this.roleFormUseCase.resetState();
    this.roleToEdit.set(role);
    this.permissionListUseCase.loadPermissions(); // Asegurar que estén cargados
    this.isModalOpen.set(true);
  }

  closeRoleModal(): void {
    if (this.roleFormUseCase.status() === 'loading') return;
    this.isModalOpen.set(false);
    setTimeout(() => this.roleToEdit.set(null), 300); // Esperar animación
  }

  handleCreateRole(request: CreateTenantRoleRequest): void {
    this.roleFormUseCase.createRole(request);
  }

  handleUpdateRole(id: string, request: UpdateTenantRoleRequest): void {
    this.roleFormUseCase.updateRole(id, request);
  }

  async handleToggleStatus(id: string, currentStatus: boolean): Promise<void> {
    const success = await this.roleListUseCase.toggleRoleStatus(id, currentStatus);
    if (success) {
      this.roleListUseCase.reloadRoles();
      this.triggerSuccessToast();
    }
  }

  private triggerSuccessToast(): void {
    this.showSuccessToast.set(true);
    setTimeout(() => this.showSuccessToast.set(false), 3000);
  }
}

```

### `app/pages/summary-page/summary-page.component.ts`

```typescript
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SummaryCardsComponent, SummaryUseCase } from '../../features/dashboard';

@Component({
  selector: 'app-summary-page',
  standalone: true,
  imports: [CommonModule, SummaryCardsComponent],
  template: `
    <div class="space-y-6">
      <div>
        <h2 class="text-2xl font-bold tracking-tight text-foreground">Resumen de la Organización</h2>
        <p class="text-muted-foreground">Métricas clave y estado actual de tu suscripción.</p>
      </div>

      <!-- Estado: Cargando -->
      <div *ngIf="summaryUseCase.status() === 'loading'" class="flex items-center justify-center p-12">
        <div class="flex flex-col items-center gap-4 text-muted-foreground">
          <svg class="animate-spin h-8 w-8 text-primary" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando información del panel...</p>
        </div>
      </div>

      <!-- Estado: Error -->
      <div *ngIf="summaryUseCase.status() === 'error'" class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
        <div>
          <h3 class="font-semibold text-destructive">Error al cargar datos</h3>
          <p class="text-sm text-destructive/80 mt-1">{{ summaryUseCase.error() }}</p>
          <button (click)="retry()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
        </div>
      </div>

      <!-- Estado: Éxito (Presentational Component) -->
      <div *ngIf="summaryUseCase.status() === 'success'">
        <app-summary-cards [data]="summaryUseCase.data()"></app-summary-cards>
      </div>
    </div>
  `
})
export class SummaryPageComponent implements OnInit {
  public readonly summaryUseCase = inject(SummaryUseCase);

  ngOnInit(): void {
    // Al inicializar la página, cargamos los datos
    this.summaryUseCase.loadSummary();
  }

  retry(): void {
    this.summaryUseCase.loadSummary();
  }
}

```

### `app/pages/transactions-page/transactions-page.component.ts`

```typescript
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TransactionsListUseCase, TransactionSlideOverComponent, TransactionActionType } from '../../features/transactions-management';
import { TransactionResponse } from '../../entities/transactions';
import { LucideAngularModule, Plus, ChevronDown, MoreHorizontal, RotateCcw, Reply, CheckCircle, XCircle, AlertTriangle, CornerDownLeft, X } from 'lucide-angular';
import { HasPermissionPipe, HasAnyPermissionPipe } from '../../shared/api';
import { ToastService } from '../../shared/ui/toast/toast.service';

@Component({
  selector: 'app-transactions-page',
  standalone: true,
  imports: [CommonModule, FormsModule, TransactionSlideOverComponent, LucideAngularModule, HasPermissionPipe, HasAnyPermissionPipe],
  providers: [DatePipe, CurrencyPipe],
  template: `
    <div class="space-y-6 relative">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold tracking-tight text-foreground">Transacciones</h2>
          <p class="text-muted-foreground">Historial completo de movimientos, transferencias, pagos y retiros en el tenant.</p>
        </div>
        
        <!-- Dropdown Nueva Transacción -->
        <div class="relative group">
          <button 
            *ngIf="['transactions.create.deposit', 'transactions.create.withdrawal', 'transactions.create.transfer', 'transactions.create.payment'] | hasAnyPermission"
            type="button"
            class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2 shadow-sm">
            <lucide-icon name="plus" [size]="16"></lucide-icon>
            Nueva Transacción
            <lucide-icon name="chevron-down" [size]="14" class="opacity-70 ml-1"></lucide-icon>
          </button>
          
          <div class="absolute right-0 top-full mt-2 w-48 bg-card border border-border rounded-lg shadow-lg opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all z-10 overflow-hidden">
            <div class="py-1">
              <button *ngIf="'transactions.create.deposit' | hasPermission" (click)="openSlideOver('deposit')" class="w-full text-left px-4 py-2 text-sm text-foreground hover:bg-muted transition-colors">Crear Depósito</button>
              <button *ngIf="'transactions.create.withdrawal' | hasPermission" (click)="openSlideOver('withdrawal')" class="w-full text-left px-4 py-2 text-sm text-foreground hover:bg-muted transition-colors">Crear Retiro</button>
              <button *ngIf="'transactions.create.transfer' | hasPermission" (click)="openSlideOver('transfer')" class="w-full text-left px-4 py-2 text-sm text-foreground hover:bg-muted transition-colors">Crear Transferencia</button>
              <button *ngIf="'transactions.create.payment' | hasPermission" (click)="openSlideOver('payment')" class="w-full text-left px-4 py-2 text-sm text-foreground hover:bg-muted transition-colors">Crear Pago</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Estado: Error Lista -->
      @if (useCase.status() === 'error') {
        <div class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-destructive">Error al cargar transacciones</h3>
            <p class="text-sm text-destructive/80 mt-1">{{ useCase.error() }}</p>
            <button (click)="loadTransactions()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      <!-- Estado: Cargando Inicial -->
      @if (useCase.status() === 'loading' && useCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center p-12 text-muted-foreground gap-4">
          <svg class="animate-spin h-8 w-8 text-primary" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando historial...</p>
        </div>
      }

      <!-- Estado: Éxito o Recargando -->
      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <div class="bg-card border border-border rounded-xl shadow-sm">
          <div class="overflow-x-auto min-h-[300px]">
            <table class="w-full text-sm text-left">
              <thead class="text-xs text-muted-foreground uppercase bg-muted/50 border-b border-border">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Tipo y Canal</th>
                  <th scope="col" class="px-6 py-4 font-medium">Cuentas Involucradas</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Monto</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Fecha</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center w-16">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-border">
                @for (tx of useCase.data(); track tx.id) {
                  <tr class="hover:bg-muted/30 transition-colors">
                    <td class="px-6 py-4">
                      <div class="font-bold text-foreground flex items-center gap-2">
                        <span>{{ tx.type }}</span>
                      </div>
                      <div class="text-xs text-muted-foreground mt-0.5">Canal: {{ tx.channel }}</div>
                    </td>
                    <td class="px-6 py-4">
                      <div class="flex flex-col gap-1">
                        @if (tx.sourceAccountId) {
                          <div class="text-xs flex items-center gap-1">
                            <span class="text-muted-foreground">Origen:</span> 
                            <span class="font-medium text-foreground" [title]="tx.sourceAccountId">{{ tx.sourceAccountDisplayName || tx.sourceAccountNumber }}</span>
                          </div>
                        }
                        @if (tx.targetAccountId) {
                          <div class="text-xs flex items-center gap-1">
                            <span class="text-muted-foreground">Destino:</span> 
                            <span class="font-medium text-foreground" [title]="tx.targetAccountId">{{ tx.targetAccountDisplayName || tx.targetAccountNumber }}</span>
                          </div>
                        }
                        @if (!tx.sourceAccountId && !tx.targetAccountId) {
                          <span class="text-xs text-muted-foreground italic">No aplica</span>
                        }
                      </div>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="font-bold text-foreground">
                        {{ tx.amount | number:'1.2-2' }} <span class="text-xs ml-0.5">{{ tx.currency }}</span>
                      </div>
                      @if (tx.fxDetail) {
                        <div class="text-[10px] text-muted-foreground mt-0.5" title="Exchange Rate">
                          Rate: {{ tx.fxDetail.exchangeRate | number:'1.6-6' }}
                        </div>
                      }
                    </td>
                    <td class="px-6 py-4 text-center">
                      <span class="px-2.5 py-1 text-[10px] font-bold rounded-full uppercase tracking-wider inline-flex items-center gap-1"
                            [ngClass]="{
                              'bg-green-500/10 text-green-600': tx.status === 'COMPLETED',
                              'bg-amber-500/10 text-amber-600': tx.status === 'PENDING' || tx.status === 'HELD',
                              'bg-red-500/10 text-red-600': tx.status === 'FAILED' || tx.status === 'REJECTED' || tx.status === 'REVERSED',
                              'bg-gray-500/10 text-gray-600': tx.status !== 'COMPLETED' && tx.status !== 'PENDING' && tx.status !== 'HELD' && tx.status !== 'FAILED' && tx.status !== 'REJECTED' && tx.status !== 'REVERSED'
                            }">
                        @if (tx.status === 'COMPLETED') {
                          <svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
                        } @else if (tx.status === 'FAILED') {
                          <svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                        }
                        {{ tx.status }}
                      </span>
                      @if (tx.failureReason) {
                        <div class="text-[9px] text-destructive mt-1 max-w-[120px] truncate mx-auto" [title]="tx.failureReason">
                          {{ tx.failureReason }}
                        </div>
                      }
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="text-sm text-foreground">{{ (tx.processedAt || tx.createdAt) | date:'MMM d, y' }}</div>
                      <div class="text-xs text-muted-foreground">{{ (tx.processedAt || tx.createdAt) | date:'shortTime' }}</div>
                    </td>
                    <td class="px-6 py-4 text-center">
                      <div class="relative group inline-block text-left">
                        <button class="p-2 rounded-md hover:bg-muted text-muted-foreground transition-colors focus:outline-none">
                          <lucide-icon name="more-horizontal" [size]="16"></lucide-icon>
                        </button>
                        <div class="absolute right-0 mt-1 w-48 bg-card border border-border rounded-lg shadow-lg opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all z-50 overflow-hidden">
                          <div class="py-1">
                            <!-- Acciones (ej. Reverse, Refund) -->
                            <button *ngIf="('transactions.reverse' | hasPermission) && tx.status === 'COMPLETED'" (click)="reverseTransaction(tx.id)" class="w-full text-left px-4 py-2 text-xs text-orange-600 hover:bg-orange-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="rotate-ccw" [size]="14"></lucide-icon> Revertir
                            </button>
                            <button *ngIf="('transactions.refund' | hasPermission) && tx.status === 'COMPLETED'" (click)="refundTransaction(tx.id)" class="w-full text-left px-4 py-2 text-xs text-blue-600 hover:bg-blue-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="reply" [size]="14"></lucide-icon> Reembolsar
                            </button>
                            <!-- Placeholder si no hay acciones -->
                            <div *ngIf="!('transactions.reverse' | hasPermission) && !('transactions.refund' | hasPermission)" class="px-4 py-2 text-xs text-muted-foreground italic text-center">
                              Sin acciones
                            </div>
                          </div>
                        </div>
                      </div>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="5" class="px-6 py-12 text-center text-muted-foreground">
                      No hay transacciones registradas.
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }
    </div>

    <!-- Formulario Lateral -->
    <app-transaction-slide-over
      [isOpen]="isSlideOverOpen"
      [transactionType]="selectedTransactionType"
      (closed)="isSlideOverOpen = false"
      (saved)="onTransactionSaved($event)">
    </app-transaction-slide-over>

    <!-- Modal de Acción (Revertir/Reembolsar) -->
    <div *ngIf="actionModalOpen" class="fixed inset-0 z-[100] flex items-center justify-center">
      <!-- Overlay blur -->
      <div 
        class="absolute inset-0 bg-background/80 backdrop-blur-sm transition-opacity"
        (click)="closeActionModal()">
      </div>
      
      <!-- Contenido del modal -->
      <div class="relative w-full max-w-md bg-card rounded-2xl shadow-2xl border border-border p-6 transform transition-all">
        <!-- Cerrar -->
        <button 
          (click)="closeActionModal()"
          class="absolute right-4 top-4 p-2 rounded-full text-muted-foreground hover:bg-muted hover:text-foreground transition-colors">
          <lucide-icon name="x" [size]="20"></lucide-icon>
        </button>

        <div class="flex flex-col items-center text-center mt-2">
          <div 
            class="h-16 w-16 rounded-full flex items-center justify-center mb-4"
            [ngClass]="actionModalType === 'reverse' ? 'bg-orange-500/10 text-orange-600' : 'bg-blue-500/10 text-blue-600'">
            <lucide-icon [name]="actionModalType === 'reverse' ? 'rotate-ccw' : 'corner-down-left'" [size]="32"></lucide-icon>
          </div>
          <h3 class="text-xl font-bold text-foreground">
            {{ actionModalType === 'reverse' ? 'Revertir Transacción' : 'Reembolsar Transacción' }}
          </h3>
          <p class="text-sm text-muted-foreground mt-1">
            Por favor, ingresa los detalles para procesar esta solicitud.
          </p>
        </div>

        <div class="mt-6 space-y-4">
          <!-- Motivo (Requerido para ambos) -->
          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground">Motivo</label>
            <input 
              type="text" 
              [(ngModel)]="actionReason"
              placeholder="Ej. Error de sistema, solicitud del cliente"
              class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2" />
          </div>

          <!-- Monto (Solo para Reembolso) -->
          <div *ngIf="actionModalType === 'refund'" class="space-y-2">
            <label class="text-sm font-medium text-foreground">Monto a reembolsar</label>
            <div class="relative">
              <span class="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground font-medium text-sm">
                {{ selectedTransactionForAction?.currency }}
              </span>
              <input 
                type="number" 
                [(ngModel)]="actionAmount"
                min="0.01" step="0.01"
                class="flex h-10 w-full rounded-md border border-input bg-background pl-12 pr-3 py-2 text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2" />
            </div>
            <p class="text-xs text-muted-foreground">Original: {{ selectedTransactionForAction?.amount | currency:selectedTransactionForAction?.currency }}</p>
          </div>
          
          <!-- Mensaje de Error (Si lo hay) -->
          <div *ngIf="actionError" class="p-3 bg-red-500/10 border border-red-500/20 text-red-600 rounded-md text-xs font-medium">
            {{ actionError }}
          </div>
        </div>

        <div class="mt-8 flex gap-3">
          <button 
            (click)="closeActionModal()"
            class="flex-1 inline-flex items-center justify-center rounded-xl text-sm font-medium border border-input bg-background hover:bg-muted h-10 transition-colors">
            Cancelar
          </button>
          <button 
            (click)="confirmAction()"
            [disabled]="!actionReason || (actionModalType === 'refund' && (!actionAmount || actionAmount <= 0)) || isProcessingAction"
            class="flex-1 inline-flex items-center justify-center rounded-xl text-sm font-medium h-10 transition-colors disabled:opacity-50"
            [ngClass]="actionModalType === 'reverse' ? 'bg-orange-600 hover:bg-orange-700 text-white' : 'bg-blue-600 hover:bg-blue-700 text-white'">
            <lucide-icon *ngIf="!isProcessingAction" [name]="actionModalType === 'reverse' ? 'rotate-ccw' : 'check'" [size]="16" class="mr-2"></lucide-icon>
            <svg *ngIf="isProcessingAction" class="animate-spin h-4 w-4 mr-2" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ actionModalType === 'reverse' ? 'Confirmar Reversión' : 'Confirmar Reembolso' }}
          </button>
        </div>
      </div>
    </div>
  `
})
export class TransactionsPageComponent implements OnInit {
  public readonly useCase = inject(TransactionsListUseCase);
  private readonly toastService = inject(ToastService);

  isSlideOverOpen = false;
  selectedTransactionType: TransactionActionType = 'deposit';

  // Action Modal State (Reverse / Refund)
  actionModalOpen = false;
  actionModalType: 'reverse' | 'refund' | null = null;
  selectedTransactionForAction: TransactionResponse | null = null;
  actionReason = '';
  actionAmount: number | null = null;
  actionError: string | null = null;
  isProcessingAction = false;

  ngOnInit() {
    this.loadTransactions();
  }

  loadTransactions() {
    this.useCase.loadTransactions();
  }

  openSlideOver(type: TransactionActionType) {
    this.selectedTransactionType = type;
    this.isSlideOverOpen = true;
  }

  async onTransactionSaved(event: { type: TransactionActionType, request: any }) {
    try {
      await this.useCase.executeTransaction(event.type, event.request);
      this.toastService.success('Transacción procesada con éxito.');
      this.isSlideOverOpen = false;
    } catch (error) {
      this.toastService.error('Error al procesar la transacción: ' + error);
    }
  }

  reverseTransaction(id: string) {
    const tx = this.useCase.data().find(t => t.id === id);
    if (!tx) return;
    this.openActionModal('reverse', tx);
  }

  refundTransaction(id: string) {
    const tx = this.useCase.data().find(t => t.id === id);
    if (!tx) return;
    this.openActionModal('refund', tx);
  }

  openActionModal(type: 'reverse' | 'refund', transaction: TransactionResponse) {
    this.actionModalType = type;
    this.selectedTransactionForAction = transaction;
    this.actionReason = '';
    this.actionAmount = transaction.amount; // default to full refund
    this.actionError = null;
    this.isProcessingAction = false;
    this.actionModalOpen = true;
  }

  closeActionModal() {
    this.actionModalOpen = false;
    this.selectedTransactionForAction = null;
    this.actionModalType = null;
  }

  async confirmAction() {
    if (!this.selectedTransactionForAction || !this.actionReason || !this.actionModalType) return;
    if (this.actionModalType === 'refund' && (!this.actionAmount || this.actionAmount <= 0)) return;

    this.isProcessingAction = true;
    this.actionError = null;

    try {
      if (this.actionModalType === 'reverse') {
        await this.useCase.reverseTransaction(this.selectedTransactionForAction.id, this.actionReason);
      } else if (this.actionModalType === 'refund') {
        await this.useCase.refundTransaction(this.selectedTransactionForAction.id, this.actionReason, this.actionAmount!);
      }
      this.closeActionModal();
    } catch (error: any) {
      this.actionError = error.message || 'Error al procesar la operación';
    } finally {
      this.isProcessingAction = false;
    }
  }
}

```

### `app/pages/users-page/users-page.component.ts`

```typescript
import { Component, inject, OnInit, effect, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserListUseCase, UserTableComponent, UserCreateUseCase, UserCreateFormComponent, UserUpdateUseCase, UserStatusUseCase, UserEditFormComponent } from '../../features/user-management';
import { CreateTenantUserRequest, TenantUserResponse, UpdateTenantUserRequest } from '../../entities/user';
import { UserRoleAssignmentComponent, UserRoleUseCase } from '../../features/user-role-management';
import { ToastService } from '../../shared/ui/toast/toast.service';

@Component({
  selector: 'app-users-page',
  standalone: true,
  imports: [CommonModule, UserTableComponent, UserCreateFormComponent, UserEditFormComponent, UserRoleAssignmentComponent],
  template: `
    <div class="space-y-6 relative">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold tracking-tight text-foreground">Usuarios</h2>
          <p class="text-muted-foreground">Gestiona los accesos y permisos de tu equipo.</p>
        </div>
        
        <button 
          (click)="openCreateModal()"
          class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" x2="12" y1="5" y2="19"/><line x1="5" x2="19" y1="12" y2="12"/></svg>
          Nuevo Usuario
        </button>
      </div>

      <!-- Estado: Cargando -->
      @if (userListUseCase.status() === 'loading' && userListUseCase.data().length === 0) {
        <div class="flex items-center justify-center p-12">
          <div class="flex flex-col items-center gap-4 text-muted-foreground">
            <svg class="animate-spin h-8 w-8 text-primary" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <p>Cargando lista de usuarios...</p>
          </div>
        </div>
      }

      <!-- Estado: Error -->
      @if (userListUseCase.status() === 'error') {
        <div class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-destructive">Error al cargar usuarios</h3>
            <p class="text-sm text-destructive/80 mt-1">{{ userListUseCase.error() }}</p>
            <button (click)="retry()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      <!-- Estado: Éxito (Tabla) -->
      @if (userListUseCase.status() === 'success' || (userListUseCase.status() === 'loading' && userListUseCase.data().length > 0)) {
        <div class="relative">
          @if (userListUseCase.status() === 'loading') {
            <div class="absolute -top-3 left-1/2 transform -translate-x-1/2 bg-primary text-primary-foreground px-3 py-1 rounded-full text-xs shadow-md animate-pulse z-10">Actualizando...</div>
          }
          <app-user-table 
            [users]="userListUseCase.data()"
            (manageRoles)="openRoleModal($event)"
            (editUser)="openEditModal($event)"
            (toggleStatus)="handleToggleStatus($event)">
          </app-user-table>
        </div>
      }
    </div>

    <!-- Modal de Creación -->
    @if (isCreateModalOpen()) {
      <div class="fixed inset-0 z-50 flex items-center justify-center">
        <!-- Backdrop -->
        <div class="absolute inset-0 bg-background/80 backdrop-blur-sm animate-in fade-in" (click)="closeCreateModal()"></div>
        
        <!-- Modal Panel -->
        <div class="relative bg-card w-full max-w-lg mx-4 rounded-xl shadow-lg border border-border flex flex-col max-h-[90vh] animate-in zoom-in-95 duration-200">
          <!-- Header -->
          <div class="flex items-center justify-between px-6 py-4 border-b border-border">
            <h3 class="text-lg font-semibold text-foreground">Crear Nuevo Usuario</h3>
            <button (click)="closeCreateModal()" class="text-muted-foreground hover:text-foreground transition-colors p-1" [disabled]="userCreateUseCase.status() === 'loading'">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" x2="6" y1="6" y2="18"/><line x1="6" x2="18" y1="6" y2="18"/></svg>
            </button>
          </div>
          <!-- Body -->
          <div class="p-6 overflow-y-auto">
            @if (userCreateUseCase.error()) {
              <div class="mb-4 p-3 bg-destructive/10 text-destructive border border-destructive/20 rounded-md text-sm">
                {{ userCreateUseCase.error() }}
              </div>
            }
            <app-user-create-form
              [status]="userCreateUseCase.status()"
              (formSubmit)="handleCreateUser($event)"
              (cancel)="closeCreateModal()"
            />
          </div>
        </div>
      </div>
    }

    <!-- Modal de Edición -->
    @if (isEditModalOpen() && selectedUserToEdit()) {
      <div class="fixed inset-0 z-50 flex items-center justify-center">
        <!-- Backdrop -->
        <div class="absolute inset-0 bg-background/80 backdrop-blur-sm animate-in fade-in" (click)="closeEditModal()"></div>
        
        <!-- Modal Panel -->
        <div class="relative bg-card w-full max-w-lg mx-4 rounded-xl shadow-lg border border-border flex flex-col max-h-[90vh] animate-in zoom-in-95 duration-200">
          <!-- Header -->
          <div class="flex items-center justify-between px-6 py-4 border-b border-border">
            <h3 class="text-lg font-semibold text-foreground">Editar Usuario</h3>
            <button (click)="closeEditModal()" class="text-muted-foreground hover:text-foreground transition-colors p-1" [disabled]="userUpdateUseCase.status() === 'loading'">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" x2="6" y1="6" y2="18"/><line x1="6" x2="18" y1="6" y2="18"/></svg>
            </button>
          </div>
          <!-- Body -->
          <div class="p-6 overflow-y-auto">
            @if (userUpdateUseCase.error()) {
              <div class="mb-4 p-3 bg-destructive/10 text-destructive border border-destructive/20 rounded-md text-sm">
                {{ userUpdateUseCase.error() }}
              </div>
            }
            <app-user-edit-form
              [status]="userUpdateUseCase.status()"
              [user]="selectedUserToEdit()!"
              (formSubmit)="handleUpdateUser($event)"
              (cancel)="closeEditModal()"
            />
          </div>
        </div>
      </div>
    }

    <!-- Modal Asignación de Roles -->
    @if (isRoleModalOpen()) {
      <div class="fixed inset-0 z-50 flex items-center justify-end">
        <!-- Backdrop -->
        <div class="absolute inset-0 bg-background/80 backdrop-blur-sm animate-in fade-in" (click)="closeRoleModal()"></div>
        
        <!-- Slide-over Panel -->
        <div class="relative bg-card w-full max-w-md h-full shadow-2xl border-l border-border flex flex-col animate-in slide-in-from-right duration-300">
          
          <div class="flex items-center justify-between px-6 py-4 border-b border-border bg-card">
            <h3 class="text-lg font-semibold text-foreground">Asignar Roles</h3>
            <button (click)="closeRoleModal()" class="text-muted-foreground hover:text-foreground transition-colors p-1 rounded-md hover:bg-muted" [disabled]="userRoleUseCase.status() === 'loading'">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" x2="6" y1="6" y2="18"/><line x1="6" x2="18" y1="6" y2="18"/></svg>
            </button>
          </div>

          <div class="flex-1 overflow-hidden">
            @if (userRoleUseCase.error()) {
              <div class="m-6 p-4 bg-destructive/10 text-destructive border border-destructive/20 rounded-lg text-sm">
                {{ userRoleUseCase.error() }}
              </div>
            }

            <app-user-role-assignment
              [status]="userRoleUseCase.status()"
              [user]="selectedUser()"
              [availableRoles]="userRoleUseCase.availableRoles()"
              [assignedRoles]="userRoleUseCase.userRoles()"
              (submitRoles)="handleAssignRoles($event)"
              (cancel)="closeRoleModal()">
            </app-user-role-assignment>
          </div>
        </div>
      </div>
    }
  `
})
export class UsersPageComponent implements OnInit {
  public readonly userListUseCase = inject(UserListUseCase);
  public readonly userCreateUseCase = inject(UserCreateUseCase);
  public readonly userUpdateUseCase = inject(UserUpdateUseCase);
  public readonly userStatusUseCase = inject(UserStatusUseCase);
  public readonly userRoleUseCase = inject(UserRoleUseCase);

  public readonly isCreateModalOpen = signal(false);
  public readonly isEditModalOpen = signal(false);
  public readonly isRoleModalOpen = signal(false);
  public readonly selectedUser = signal<TenantUserResponse | null>(null);
  public readonly selectedUserToEdit = signal<TenantUserResponse | null>(null);
  
  private readonly toastService = inject(ToastService);

  constructor() {
    // Éxito al crear usuario
    effect(() => {
      if (this.userCreateUseCase.status() === 'success') {
        this.closeCreateModal();
        this.userCreateUseCase.resetState();
        this.userListUseCase.reloadUsers();
        this.toastService.success('Usuario creado correctamente');
      }
    }, { allowSignalWrites: true });

    // Éxito al editar usuario
    effect(() => {
      if (this.userUpdateUseCase.status() === 'success') {
        this.closeEditModal();
        this.userUpdateUseCase.resetState();
        this.userListUseCase.reloadUsers();
        this.toastService.success('Usuario actualizado correctamente');
      }
    }, { allowSignalWrites: true });

    // Éxito al asignar roles
    effect(() => {
      if (this.userRoleUseCase.status() === 'success') {
        this.closeRoleModal();
        this.userRoleUseCase.resetState();
        this.toastService.success('Roles actualizados correctamente');
      }
    }, { allowSignalWrites: true });
  }

  ngOnInit(): void {
    this.userListUseCase.loadUsers();
  }

  retry(): void {
    this.userListUseCase.loadUsers();
  }

  openCreateModal(): void {
    this.userCreateUseCase.resetState();
    this.isCreateModalOpen.set(true);
  }

  closeCreateModal(): void {
    if (this.userCreateUseCase.status() === 'loading') return;
    this.isCreateModalOpen.set(false);
  }

  handleCreateUser(request: CreateTenantUserRequest): void {
    this.userCreateUseCase.createUser(request);
  }

  // --- Editar Usuario ---
  openEditModal(user: TenantUserResponse): void {
    this.userUpdateUseCase.resetState();
    this.selectedUserToEdit.set(user);
    this.isEditModalOpen.set(true);
  }

  closeEditModal(): void {
    if (this.userUpdateUseCase.status() === 'loading') return;
    this.isEditModalOpen.set(false);
    setTimeout(() => this.selectedUserToEdit.set(null), 300);
  }

  handleUpdateUser(request: UpdateTenantUserRequest): void {
    const user = this.selectedUserToEdit();
    if (user) {
      this.userUpdateUseCase.updateUser(user.id.toString(), request);
    }
  }

  // --- Estado de Usuario (Activar/Desactivar) ---
  async handleToggleStatus(user: TenantUserResponse): Promise<void> {
    const action = user.active ? 'desactivar' : 'activar';
    if (confirm(`¿Estás seguro que deseas ${action} a este usuario?`)) {
      const success = await this.userStatusUseCase.toggleStatus(user.id.toString(), user.active);
      if (success) {
        this.toastService.success(`Usuario ${user.active ? 'desactivado' : 'activado'} correctamente`);
        this.userListUseCase.reloadUsers();
      } else {
        this.toastService.error(this.userStatusUseCase.error() || 'Error al cambiar el estado del usuario');
      }
    }
  }

  // --- Roles Modal ---
  openRoleModal(user: TenantUserResponse): void {
    this.userRoleUseCase.resetState();
    this.selectedUser.set(user);
    // Cargamos los roles de este usuario
    this.userRoleUseCase.loadRolesForUser(user.id.toString());
    this.isRoleModalOpen.set(true);
  }

  closeRoleModal(): void {
    if (this.userRoleUseCase.status() === 'loading') return;
    this.isRoleModalOpen.set(false);
    setTimeout(() => this.selectedUser.set(null), 300); // limpiar despues de animación
  }

  handleAssignRoles(roleIds: string[]): void {
    const user = this.selectedUser();
    if (user) {
      this.userRoleUseCase.assignRoles(user.id.toString(), roleIds);
    }
  }
}

```

### `app/shared/api/guards/auth.guard.ts`

```typescript
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthStorageService } from '../../lib/storage/auth-storage.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authStorage = inject(AuthStorageService);
  const router = inject(Router);

  const token = authStorage.getToken();

  // Validación básica del token
  if (!token || token.trim() === '') {
    // Redirigir a login si no hay token
    return router.createUrlTree(['/login']);
  }

  // Permitir el acceso
  return true;
};

```

### `app/shared/api/guards/permission.guard.ts`

```typescript
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { PermissionService } from '../../lib/auth/permission.service';

/**
 * Genera un CanActivateFn Guard que verifica que el usuario tenga
 * AL MENOS UNO de los permisos requeridos para acceder a la ruta.
 *
 * Uso en app.routes.ts:
 *   { path: 'accounts', canActivate: [permissionGuard('accounts.admin.read', 'accounts.create')], ... }
 *   { path: 'transactions', canActivate: [permissionGuard('transactions.admin.read')], ... }
 */
export function permissionGuard(...requiredPermissions: string[]): CanActivateFn {
  return (route, state) => {
    const permissionService = inject(PermissionService);
    const router = inject(Router);

    const hasAccess = permissionService.hasAnyPermission(...requiredPermissions);

    if (!hasAccess) {
      // Redirigir al resumen del dashboard si no tiene permisos
      return router.createUrlTree(['/dashboard/summary']);
    }

    return true;
  };
}

```

### `app/shared/api/guards/platform-auth.guard.ts`

```typescript
import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { PlatformStorageService } from '../../../features/platform/lib/platform-storage.service';

@Injectable({ providedIn: 'root' })
export class platformAuthGuard {
  private router = inject(Router);
  private platformStorage = inject(PlatformStorageService);

  canActivate(): boolean {
    const token = this.platformStorage.getToken();
    if (token) {
      return true;
    }
    this.router.navigate(['/platform/login']);
    return false;
  }
}
```

### `app/shared/api/index.ts`

```typescript
// export guards
export * from './guards/auth.guard';
export * from './guards/permission.guard';

// export interceptors
export * from './interceptors/auth.interceptor';

// export models
export * from './models/api-response.model';

// export pipes
export * from './pipes/has-permission.pipe';

```

### `app/shared/api/interceptors/auth.interceptor.ts`

```typescript
// core/http/interceptors/auth-token.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthStorageService } from '../../../shared/lib/storage/auth-storage.service';
import { PlatformStorageService } from '../../../features/platform/lib/platform-storage.service';

export const authTokenInterceptor: HttpInterceptorFn = (req, next) => {
  const authStorage = inject(AuthStorageService);
  const platformStorage = inject(PlatformStorageService);
  
  let clonedRequest = req;
  
  // ✅ Detectar si es una petición de platform
  if (req.url.includes('/api/platform/')) {
    const platformToken = platformStorage.getToken();
    if (platformToken) {
      clonedRequest = req.clone({
        setHeaders: {
          'Authorization': `Bearer ${platformToken}`
        }
      });
      console.log('🔑 Platform token agregado a:', req.url);
    } else {
      console.warn('⚠️ No platform token found for:', req.url);
    }
  } 
  // ✅ Para peticiones de tenant (normal)
  else if (req.url.includes('/api/')) {
    const token = authStorage.getToken();
    const tenantSlug = authStorage.getTenantSlug();
    
    const headersConfig: Record<string, string> = {};
    
    if (token) {
      headersConfig['Authorization'] = `Bearer ${token}`;
    }
    
    if (tenantSlug) {
      headersConfig['X-Tenant-Slug'] = tenantSlug;
    }
    
    if (Object.keys(headersConfig).length > 0) {
      clonedRequest = req.clone({ setHeaders: headersConfig });
      console.log('🔑 Tenant token agregado a:', req.url);
    }
  }
  
  return next(clonedRequest);
};
```

### `app/shared/api/models/api-response.model.ts`

```typescript
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

```

### `app/shared/api/pipes/has-permission.pipe.ts`

```typescript
import { inject, Pipe, PipeTransform } from '@angular/core';
import { PermissionService } from '../../lib/auth/permission.service';

/**
 * Pipe puro para verificar permisos en el HTML sin afectar el rendimiento.
 * Angular cachea el resultado de un Pipe puro y solo lo re-evalúa cuando el input cambia,
 * evitando ejecuciones innecesarias en cada ciclo de detección de cambios.
 *
 * Uso:
 *   *ngIf="'accounts.create' | hasPermission"
 *   *ngIf="'transactions.create.deposit' | hasPermission"
 */
@Pipe({
  name: 'hasPermission',
  standalone: true,
  pure: true
})
export class HasPermissionPipe implements PipeTransform {
  private readonly permissionService = inject(PermissionService);

  transform(permissionCode: string): boolean {
    return this.permissionService.hasPermission(permissionCode);
  }
}

/**
 * Pipe puro para verificar si el usuario tiene AL MENOS UNO de los permisos dados.
 *
 * Uso:
 *   *ngIf="['transactions.create.deposit', 'transactions.create.withdrawal'] | hasAnyPermission"
 */
@Pipe({
  name: 'hasAnyPermission',
  standalone: true,
  pure: true
})
export class HasAnyPermissionPipe implements PipeTransform {
  private readonly permissionService = inject(PermissionService);

  transform(permissionCodes: string[]): boolean {
    return this.permissionService.hasAnyPermission(...permissionCodes);
  }
}

```

### `app/shared/lib/auth/auth.facade.ts`

```typescript
import { inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthStorageService } from '../storage/auth-storage.service';
import { AuthService } from '../../../entities/auth/api/auth.service';
import { AuthenticatedTenantUserResponse } from '../../../entities/auth/model/authenticated-tenant-user-response.model';

@Injectable({
  providedIn: 'root'
})
export class AuthFacade {
  private readonly authStorage = inject(AuthStorageService);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  readonly currentUser = signal<AuthenticatedTenantUserResponse | null>(null);

  /** Carga la información del usuario actual autenticado. */
  loadCurrentUser(): void {
    if (!this.authStorage.getToken()) return;
    
    this.authService.getMe().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.currentUser.set(res.data);
        }
      },
      error: (err) => {
        console.error('Error loading current user', err);
        // Si hay error 401/403, el interceptor probablemente ya se encargue, pero podemos setear null por seguridad.
        this.currentUser.set(null);
      }
    });
  }

  /** Cierra la sesión del usuario actual limpiando el almacenamiento y redirigiendo a la vista de login. */
  logout(): void {
    // Limpiar almacenamiento persistente
    this.authStorage.clear();
    
    // Limpiar el estado de usuario
    this.currentUser.set(null);
    
    // Redirigir al inicio de sesión
    this.router.navigate(['/login']);
  }
}

```

### `app/shared/lib/auth/permission.service.ts`

```typescript
import { inject, Injectable } from '@angular/core';
import { AuthStorageService } from '../storage/auth-storage.service';

interface JwtPayload {
  sub: string;
  tenant: string;
  roles: string[];
  permissions: string[];
  token_type: string;
  iat: number;
  exp: number;
}

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  private readonly authStorage = inject(AuthStorageService);

  // Cache
  private cachedToken: string | null = null;
  private cachedPermissions: Set<string> = new Set();
  private cachedRoles: Set<string> = new Set();

  /**
   * Verifica si el usuario autenticado tiene un permiso específico.
   * Decodifica el JWT del localStorage y busca en el claim "permissions".
   */
  hasPermission(permissionCode: string): boolean {
    this.refreshCacheIfNeeded();
    return this.cachedPermissions.has(permissionCode);
  }

  /**
   * Verifica si el usuario tiene AL MENOS UNO de los permisos dados.
   */
  hasAnyPermission(...permissionCodes: string[]): boolean {
    this.refreshCacheIfNeeded();
    return permissionCodes.some(code => this.cachedPermissions.has(code));
  }

  /**
   * Verifica si el usuario tiene un rol específico (ej. "ADMIN", "OWNER_ADMIN").
   */
  hasRole(roleName: string): boolean {
    this.refreshCacheIfNeeded();
    return this.cachedRoles.has(roleName.toUpperCase());
  }

  /**
   * Retorna todos los permisos del usuario autenticado.
   */
  getPermissions(): string[] {
    this.refreshCacheIfNeeded();
    return Array.from(this.cachedPermissions);
  }

  /**
   * Retorna todos los roles del usuario autenticado.
   */
  getRoles(): string[] {
    this.refreshCacheIfNeeded();
    return Array.from(this.cachedRoles);
  }

  /**
   * Fuerza la re-lectura del JWT (útil después de un login o refresh).
   */
  invalidateCache(): void {
    this.cachedToken = null;
    this.cachedPermissions.clear();
    this.cachedRoles.clear();
  }

  // --- Internos ---

  private refreshCacheIfNeeded(): void {
    const currentToken = this.authStorage.getToken();

    // Si no hay token, limpiar
    if (!currentToken) {
      if (this.cachedToken !== null) {
        this.invalidateCache();
      }
      return;
    }

    // Si el token no cambió, usar cache
    if (currentToken === this.cachedToken) {
      return;
    }

    // Token nuevo o diferente → parsear
    this.cachedToken = currentToken;
    this.cachedPermissions.clear();
    this.cachedRoles.clear();

    try {
      const payload = this.decodeJwtPayload(currentToken);

      if (Array.isArray(payload.permissions)) {
        payload.permissions.forEach(p => this.cachedPermissions.add(p));
      }

      if (Array.isArray(payload.roles)) {
        payload.roles.forEach(r => this.cachedRoles.add(r.toUpperCase()));
      }
    } catch (err) {
      console.error('[PermissionService] Error al decodificar el JWT:', err);
      this.invalidateCache();
    }
  }

  private decodeJwtPayload(token: string): JwtPayload {
    const parts = token.split('.');
    if (parts.length !== 3) {
      throw new Error('JWT inválido: no tiene 3 partes');
    }

    // El payload es la segunda parte (index 1)
    const payloadBase64Url = parts[1];

    // Base64URL → Base64 estándar
    const payloadBase64 = payloadBase64Url
      .replace(/-/g, '+')
      .replace(/_/g, '/');

    // Decodificar
    const payloadJson = atob(payloadBase64);
    return JSON.parse(payloadJson) as JwtPayload;
  }
}

```

### `app/shared/lib/storage/auth-storage.service.ts`

```typescript
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthStorageService {
  private readonly TOKEN_KEY = 'access_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private readonly TENANT_SLUG_KEY = 'tenant_slug';

  saveToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  saveRefreshToken(token: string): void {
    localStorage.setItem(this.REFRESH_TOKEN_KEY, token);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  saveTenantSlug(slug: string): void {
    localStorage.setItem(this.TENANT_SLUG_KEY, slug);
  }

  getTenantSlug(): string | null {
    return localStorage.getItem(this.TENANT_SLUG_KEY);
  }

  clear(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.TENANT_SLUG_KEY);
  }
}

```

### `app/shared/ui/icons/icons.module.ts`

```typescript
// shared/ui/icons/icons.module.ts
import { NgModule } from '@angular/core';
import { LucideAngularModule, 
  LayoutDashboard, Settings, Shield, Users, Key, Briefcase, 
  CreditCard, ArrowRightLeft, DollarSign, RefreshCcw, Percent, 
  Lock, ShieldAlert, BookOpen, Calendar, FileText, LogOut,
  Building, Receipt, Plus, X, Eye, PlayCircle, PauseCircle,
  EyeOff, Wand2, 
  ClipboardList  
} from 'lucide-angular';

@NgModule({
  imports: [
    LucideAngularModule.pick({
      LayoutDashboard, Settings, Shield, Users, Key, Briefcase,
      CreditCard, ArrowRightLeft, DollarSign, RefreshCcw, Percent,
      Lock, ShieldAlert, BookOpen, Calendar, FileText, LogOut,
      Building, Receipt, Plus, X, Eye, PlayCircle, PauseCircle,
      EyeOff, Wand2
    })
  ],
  exports: [LucideAngularModule]
})
export class IconsModule {}
```

### `app/shared/ui/layouts/platform-layout.component.ts`

```typescript
// shared/ui/layouts/platform-layout.component.ts
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../../../widgets/layoutAdmin/sidebar/sidebar.component';
import { HeaderComponent } from '../../../widgets/layoutAdmin/header/header.component';
import { PlatformStorageService } from '../../../features/platform/lib/platform-storage.service';

@Component({
  selector: 'app-platform-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, SidebarComponent, HeaderComponent],
  template: `
    <div class="flex h-screen overflow-hidden bg-[#F1F8E9]">
      <app-sidebar (logoutAction)="onLogout()"></app-sidebar>
      
      <div class="flex-1 flex flex-col overflow-hidden">
        <app-header (logoutAction)="onLogout()"></app-header>
        
        <main class="flex-1 overflow-y-auto p-6">
          <router-outlet></router-outlet>
        </main>
      </div>
    </div>
  `
})
export class PlatformLayoutComponent {
  private router = inject(Router);
  private platformStorage = inject(PlatformStorageService);

  onLogout(): void {
    this.platformStorage.clear();
    this.router.navigate(['/platform/login']);
  }
}
```

### `app/shared/ui/toast/toast.component.ts`

```typescript
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService, ToastMessage } from './toast.service';
import { LucideAngularModule, CheckCircle, AlertCircle, AlertTriangle, Info, X } from 'lucide-angular';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <div class="fixed top-4 right-4 z-[9999] flex flex-col gap-3 w-full max-w-sm pointer-events-none">
      @for (toast of toastService.toasts(); track toast.id) {
        <div 
          class="pointer-events-auto flex w-full flex-col overflow-hidden rounded-xl bg-card border shadow-lg transition-all duration-300 transform translate-y-0 opacity-100"
          [ngClass]="{
            'border-green-500/30': toast.type === 'success',
            'border-red-500/30': toast.type === 'error',
            'border-yellow-500/30': toast.type === 'warning',
            'border-blue-500/30': toast.type === 'info'
          }">
          
          <div class="p-4 flex items-start gap-3">
            <div class="flex-shrink-0 mt-0.5">
              @switch (toast.type) {
                @case ('success') { <lucide-icon name="check-circle" [size]="20" class="text-green-500"></lucide-icon> }
                @case ('error') { <lucide-icon name="alert-circle" [size]="20" class="text-red-500"></lucide-icon> }
                @case ('warning') { <lucide-icon name="alert-triangle" [size]="20" class="text-yellow-500"></lucide-icon> }
                @case ('info') { <lucide-icon name="info" [size]="20" class="text-blue-500"></lucide-icon> }
              }
            </div>
            
            <div class="flex-1 w-0">
              <p class="text-sm font-semibold text-foreground">
                {{ toast.title }}
              </p>
              <p class="mt-1 text-sm text-muted-foreground break-words">
                {{ toast.message }}
              </p>
            </div>
            
            <button 
              (click)="toastService.remove(toast.id)"
              class="flex-shrink-0 ml-4 p-1 rounded-md inline-flex text-muted-foreground hover:bg-muted focus:outline-none transition-colors">
              <lucide-icon name="x" [size]="16"></lucide-icon>
            </button>
          </div>
          
          <!-- Progress bar -->
          <div class="h-1 w-full bg-muted/50">
            <div class="h-full origin-left animate-progress"
              [ngClass]="{
                'bg-green-500': toast.type === 'success',
                'bg-red-500': toast.type === 'error',
                'bg-yellow-500': toast.type === 'warning',
                'bg-blue-500': toast.type === 'info'
              }"
              [style.animation-duration]="toast.duration + 'ms'">
            </div>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    @keyframes progress {
      0% { transform: scaleX(1); }
      100% { transform: scaleX(0); }
    }
    .animate-progress {
      animation-name: progress;
      animation-timing-function: linear;
      animation-fill-mode: forwards;
    }
  `]
})
export class ToastComponent {
  public toastService = inject(ToastService);
}

```

### `app/shared/ui/toast/toast.service.ts`

```typescript
import { Injectable, signal } from '@angular/core';

export type ToastType = 'success' | 'error' | 'warning' | 'info';

export interface ToastMessage {
  id: string;
  type: ToastType;
  title: string;
  message: string;
  duration?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  readonly toasts = signal<ToastMessage[]>([]);

  show(type: ToastType, title: string, message: string, duration: number = 5000): void {
    const id = Math.random().toString(36).substring(2, 9);
    const toast: ToastMessage = { id, type, title, message, duration };
    
    this.toasts.update(current => [...current, toast]);

    if (duration > 0) {
      setTimeout(() => this.remove(id), duration);
    }
  }

  success(message: string, title: string = 'Éxito'): void {
    this.show('success', title, message);
  }

  error(message: string, title: string = 'Error'): void {
    this.show('error', title, message, 7000); // Errors stay a bit longer
  }

  warning(message: string, title: string = 'Advertencia'): void {
    this.show('warning', title, message);
  }

  info(message: string, title: string = 'Información'): void {
    this.show('info', title, message);
  }

  remove(id: string): void {
    this.toasts.update(current => current.filter(t => t.id !== id));
  }
}

```

### `app/widgets/layoutAdmin/header/header.component.ts`

```typescript
// widgets/layoutAdmin/header/header.component.ts
import { Component, EventEmitter, Output, HostListener, ElementRef, inject, OnInit, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { trigger, transition, style, animate } from '@angular/animations';
import { AuthFacade } from '../../../shared/lib/auth/auth.facade';
import { PlatformFacade } from '../../../features/platform/lib/platform.facade';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule],
  animations: [
    trigger('dropdownAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.95) translateY(-10px)' }),
        animate('150ms cubic-bezier(0.4, 0, 0.2, 1)', style({ opacity: 1, transform: 'scale(1) translateY(0)' }))
      ]),
      transition(':leave', [
        animate('150ms cubic-bezier(0.4, 0, 0.2, 1)', style({ opacity: 0, transform: 'scale(0.95) translateY(-10px)' }))
      ])
    ])
  ],
  template: `
    <header class="h-16 flex items-center justify-between px-6 md:px-8 bg-white border-b border-[#C8E6C9] shadow-sm w-full sticky top-0 z-30">
      
      <div class="flex items-center gap-4">
        <h1 class="text-xl font-semibold text-[#2E7D32] ml-10 md:ml-0 tracking-tight">
          @if (isPlatformRoute) {
            Panel de Control - Plataforma
          } @else {
            Panel de Control
          }
        </h1>
      </div>

      <div class="flex items-center gap-2 md:gap-4 relative">
        
        <div class="h-6 w-px bg-[#C8E6C9] mx-1"></div>

        <!-- Perfil Usuario (adaptado para Platform o Tenant) -->
        <div class="relative">
          <button 
            (click)="toggleProfileMenu()"
            class="flex items-center gap-2 md:gap-3 p-1.5 rounded-full md:rounded-md hover:bg-[#F1F8E9] transition-colors focus:outline-none"
            [class.bg-[#F1F8E9]]="isProfileOpen"
          >
            <!-- Avatar -->
            <div class="h-8 w-8 rounded-full bg-[#2E7D32]/10 text-[#2E7D32] flex items-center justify-center font-bold text-sm border border-[#2E7D32]/20 uppercase">
              {{ getInitials() }}
            </div>
            <!-- Info Desktop -->
            <div class="hidden md:flex flex-col items-start text-left">
              <span class="text-sm font-semibold text-[#2E7D32] leading-none">
                {{ getDisplayName() }}
              </span>
              <span class="text-xs text-[#666666] mt-1 capitalize">
                {{ getRole() }}
              </span>
            </div>
            <!-- Chevron -->
            <lucide-icon 
              name="chevron-down" 
              class="hidden md:block h-4 w-4 text-[#666666] transition-transform duration-200"
              [class.rotate-180]="isProfileOpen"
            ></lucide-icon>
          </button>

          <!-- Dropdown Menu -->
          <div 
            *ngIf="isProfileOpen"
            @dropdownAnimation
            class="absolute right-0 mt-2 w-56 rounded-md shadow-lg bg-white border border-[#C8E6C9] ring-1 ring-black/5 focus:outline-none z-50 overflow-hidden"
          >
            <!-- Header Dropdown -->
            <div class="px-4 py-3 border-b border-[#C8E6C9] bg-[#F1F8E9]">
              <p class="text-sm font-medium text-[#2E7D32]">
                {{ getDisplayName() }}
              </p>
              <p class="text-xs font-medium text-[#666666] truncate">
                {{ getEmail() }}
              </p>
            </div>

            <div class="py-1">
              <a routerLink="/dashboard/settings" class="group flex items-center px-4 py-2 text-sm text-[#333333] hover:bg-[#F1F8E9] transition-colors">
                <lucide-icon name="user" class="mr-3 h-4 w-4 text-[#666666] group-hover:text-[#2E7D32]"></lucide-icon>
                Mi Perfil
              </a>
              <a routerLink="/dashboard/settings" class="group flex items-center px-4 py-2 text-sm text-[#333333] hover:bg-[#F1F8E9] transition-colors">
                <lucide-icon name="settings" class="mr-3 h-4 w-4 text-[#666666] group-hover:text-[#2E7D32]"></lucide-icon>
                Configuración
              </a>
            </div>

            <div class="border-t border-[#C8E6C9] py-1">
              <button 
                (click)="onLogout()"
                class="group flex w-full items-center px-4 py-2 text-sm text-[#C62828] hover:bg-red-50 transition-colors"
              >
                <lucide-icon name="log-out" class="mr-3 h-4 w-4 text-[#C62828]/70 group-hover:text-[#C62828]"></lucide-icon>
                Cerrar Sesión
              </button>
            </div>
          </div>
        </div>

      </div>
    </header>
  `
})
export class HeaderComponent implements OnInit {
  @Output() logoutAction = new EventEmitter<void>();
  isProfileOpen = false;

  private elementRef = inject(ElementRef);
  private router = inject(Router);
  private authFacade = inject(AuthFacade);
  private platformFacade = inject(PlatformFacade);

  readonly platformSuperadmin = this.platformFacade.currentSuperadmin;
  readonly tenantUser = this.authFacade.currentUser;

  ngOnInit(): void {
    this.loadUserData();
  }

  loadUserData(): void {
    if (this.isPlatformRoute) {
      // ✅ Cargar datos del superadmin de platform
      this.platformFacade.loadCurrentSuperadmin();
    } else {
      // ✅ Cargar datos del usuario tenant
      if (!this.tenantUser()) {
        this.authFacade.loadCurrentUser();
      }
    }
  }

  get isPlatformRoute(): boolean {
    return this.router.url.startsWith('/platform');
  }

  getDisplayName(): string {
    if (this.isPlatformRoute) {
      const user = this.platformSuperadmin();
      if (user) {
        return `${user.firstName} ${user.lastName}`;
      }
      return 'SuperAdmin';
    } else {
      const user = this.tenantUser();
      if (user) {
        return `${user.firstName} ${user.lastName}`;
      }
      return 'Cargando...';
    }
  }

  getEmail(): string {
    if (this.isPlatformRoute) {
      return this.platformSuperadmin()?.email || 'superadmin@finance.local';
    } else {
      return this.tenantUser()?.email || 'cargando...';
    }
  }

  getInitials(): string {
    if (this.isPlatformRoute) {
      const user = this.platformSuperadmin();
      if (user) {
        return (user.firstName.charAt(0) + user.lastName.charAt(0)).toUpperCase();
      }
      return 'SA';
    } else {
      const user = this.tenantUser();
      if (user) {
        return (user.firstName.charAt(0) + user.lastName.charAt(0)).toUpperCase();
      }
      return 'US';
    }
  }

  getRole(): string {
    if (this.isPlatformRoute) {
      return 'Super Administrador';
    } else {
      const roles = this.tenantUser()?.roles;
      if (!roles || roles.length === 0) return 'Usuario';
      const role = roles[0].replace('ROLE_', '').toLowerCase();
      return role;
    }
  }

  toggleProfileMenu(): void {
    this.isProfileOpen = !this.isProfileOpen;
  }

  @HostListener('document:click', ['$event.target'])
  onClickOutside(targetElement: EventTarget | null): void {
    if (!targetElement) return;
    const clickedInside = this.elementRef.nativeElement.contains(targetElement as Node);
    if (!clickedInside) {
      this.isProfileOpen = false;
    }
  }

  onLogout(): void {
    this.isProfileOpen = false;
    this.logoutAction.emit();
  }
}
```

### `app/widgets/layoutAdmin/index.ts`

```typescript
export * from './sidebar/sidebar.component';
export * from './header/header.component';

```

### `app/widgets/layoutAdmin/sidebar/sidebar.component.ts`

```typescript
import { Component, inject, Output, EventEmitter, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { LucideAngularModule } from 'lucide-angular';

interface MenuItem {
  label: string;
  route: string;
  icon: string;
}

interface MenuGroup {
  id: string;
  label: string;
  icon: string;
  items: MenuItem[];
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule, ],
  animations: [
    trigger('accordion', [
      state('open', style({ height: '*', opacity: 1, visibility: 'visible', margin: '4px 0' })),
      state('closed', style({ height: '0px', opacity: 0, visibility: 'hidden', margin: '0' })),
      transition('open <=> closed', animate('250ms cubic-bezier(0.4, 0.0, 0.2, 1)'))
    ]),
    trigger('rotateIcon', [
      state('open', style({ transform: 'rotate(180deg)' })),
      state('closed', style({ transform: 'rotate(0)' })),
      transition('open <=> closed', animate('250ms cubic-bezier(0.4, 0.0, 0.2, 1)'))
    ]),
    trigger('fadeOverlay', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('300ms ease-out', style({ opacity: 1 }))
      ]),
      transition(':leave', [
        animate('300ms ease-in', style({ opacity: 0 }))
      ])
    ])
  ],
  template: `
    <!-- Mobile Hamburger Button (Floating top-left when sidebar is closed) -->
    <button 
      *ngIf="!isMobileOpen"
      (click)="toggleMobileMenu()"
      class="md:hidden fixed top-3 left-4 z-40 p-2 bg-white border border-[#C8E6C9] rounded-md shadow-sm text-[#2E7D32] hover:bg-[#F1F8E9] transition-colors"
    >
      <lucide-icon name="menu" class="h-5 w-5"></lucide-icon>
    </button>

    <!-- Mobile Overlay Backdrop -->
    <div 
      *ngIf="isMobileOpen" 
      @fadeOverlay
      (click)="closeMobileMenu()"
      class="md:hidden fixed inset-0 bg-black/50 backdrop-blur-sm z-40"
    ></div>

    <!-- Sidebar Container -->
    <aside 
      class="fixed inset-y-0 left-0 z-50 w-64 h-full flex flex-col bg-white border-r border-[#C8E6C9] shadow-xl md:shadow-none transition-transform duration-300 ease-in-out md:relative md:translate-x-0"
      [class.-translate-x-full]="!isMobileOpen"
      [class.translate-x-0]="isMobileOpen"
    >
      <!-- Logo / Brand Area -->
      <div class="h-16 flex items-center justify-between px-6 border-b border-[#C8E6C9] shrink-0">
        <div class="flex items-center gap-3">
          <div class="h-8 w-8 rounded-md bg-[#2E7D32] flex items-center justify-center text-white font-bold shadow-sm">
            F
          </div>
          <span class="font-bold text-lg tracking-tight text-[#2E7D32]">Finance System</span>
        </div>
        
        <!-- Mobile Close Button -->
        <button (click)="closeMobileMenu()" class="md:hidden p-1.5 text-[#666666] hover:text-[#2E7D32] rounded-md hover:bg-[#F1F8E9] transition-colors">
          <lucide-icon name="x" class="h-5 w-5"></lucide-icon>
        </button>
      </div>

      <!-- Navigation -->
      <nav class="flex-1 overflow-y-auto py-4 px-3 space-y-1 custom-scrollbar">
        
        <!-- ============================================================ -->
        <!-- MENÚ PARA PLATFORM (SuperAdmin) -->
        <!-- ============================================================ -->
        <ng-container *ngIf="isPlatformRoute">
          <a *ngFor="let item of platformMenuItems" 
             [routerLink]="item.route" 
             routerLinkActive="bg-[#2E7D32] text-white font-semibold shadow-sm" 
             [routerLinkActiveOptions]="{exact: true}"
             (click)="closeMobileMenu()"
             class="flex items-center gap-3 px-3 py-2.5 rounded-md text-sm font-medium text-[#333333] hover:bg-[#F1F8E9] hover:text-[#2E7D32] transition-all duration-200 group">
            <lucide-icon [name]="item.icon" class="h-4 w-4 opacity-70 group-hover:opacity-100 transition-opacity"></lucide-icon>
            {{ item.label }}
          </a>
        </ng-container>

        <!-- ============================================================ -->
        <!-- MENÚ PARA TENANT (Usuario normal) -->
        <!-- ============================================================ -->
        <ng-container *ngIf="!isPlatformRoute">
          
          <!-- Elementos Generales (Sin Acordeón) -->
          <a *ngFor="let item of generalItems" 
             [routerLink]="item.route" 
             routerLinkActive="bg-[#2E7D32] text-white font-semibold shadow-sm" 
             [routerLinkActiveOptions]="{exact: item.route === '/dashboard'}"
             (click)="closeMobileMenu()"
             class="flex items-center gap-3 px-3 py-2.5 rounded-md text-sm font-medium text-[#333333] hover:bg-[#F1F8E9] hover:text-[#2E7D32] transition-all duration-200 group">
            <lucide-icon [name]="item.icon" class="h-4 w-4 opacity-70 group-hover:opacity-100 transition-opacity"></lucide-icon>
            {{ item.label }}
          </a>

          <div class="h-4"></div>

          <!-- Grupos Acordeón -->
          <div *ngFor="let group of menuGroups" class="mb-2">
            
            <!-- Header del Grupo -->
            <button 
              (click)="toggleGroup(group.id)"
              class="w-full flex items-center justify-between px-3 py-2 text-sm font-medium text-[#333333] hover:bg-[#F1F8E9] hover:text-[#2E7D32] rounded-md transition-colors group"
            >
              <div class="flex items-center gap-3">
                <lucide-icon [name]="group.icon" class="h-4 w-4 opacity-70 group-hover:opacity-100 transition-opacity"></lucide-icon>
                <span>{{ group.label }}</span>
              </div>
              <lucide-icon 
                name="chevron-down" 
                class="h-4 w-4 opacity-50"
                [@rotateIcon]="expandedGroups[group.id] ? 'open' : 'closed'"
              ></lucide-icon>
            </button>

            <!-- Contenido del Grupo -->
            <div [@accordion]="expandedGroups[group.id] ? 'open' : 'closed'" class="overflow-hidden px-2">
              <div class="flex flex-col space-y-1 pl-4 border-l border-[#C8E6C9] ml-2 py-1">
                <a *ngFor="let item of group.items" 
                   [routerLink]="item.route" 
                   routerLinkActive="text-[#2E7D32] font-semibold bg-[#E8F5E9]"
                   (click)="closeMobileMenu()"
                   class="flex items-center gap-3 px-3 py-2 rounded-md text-sm text-[#666666] hover:text-[#2E7D32] hover:bg-[#F1F8E9] transition-all duration-200">
                  <lucide-icon [name]="item.icon" class="h-3.5 w-3.5 opacity-60"></lucide-icon>
                  {{ item.label }}
                </a>
              </div>
            </div>
            
          </div>
        </ng-container>

      </nav>

      <!-- Sidebar Footer / Logout -->
      <div class="p-4 border-t border-[#C8E6C9] shrink-0">
        <button 
          (click)="onLogout()" 
          class="flex w-full items-center gap-3 px-3 py-2.5 rounded-md text-sm font-medium text-[#666666] hover:bg-red-50 hover:text-red-600 transition-all duration-200 group"
        >
          <lucide-icon name="log-out" class="h-4 w-4 opacity-70 group-hover:opacity-100 transition-opacity"></lucide-icon>
          Cerrar Sesión
        </button>
      </div>
    </aside>
  `,
  styles: [`
    .custom-scrollbar::-webkit-scrollbar {
      width: 4px;
    }
    .custom-scrollbar::-webkit-scrollbar-track {
      background: transparent;
    }
    .custom-scrollbar::-webkit-scrollbar-thumb {
      background: rgba(0,0,0,0.1);
      border-radius: 10px;
    }
  `]
})
export class SidebarComponent {
  @Output() logoutAction = new EventEmitter<void>();
  
  private router = inject(Router);

  isMobileOpen = false;

  // Estado de los acordeones (por defecto Operaciones abierto)
  expandedGroups: Record<string, boolean> = {
    accesos: false,
    operaciones: true,
    divisas: false,
    seguridad: false,
    contabilidad: false
  };

  // ============================================================
  // MENÚ PARA TENANT
  // ============================================================
  generalItems: MenuItem[] = [
    { label: 'Panel de Resumen', route: '/dashboard', icon: 'layout-dashboard' },
    { label: 'Configuración', route: '/dashboard/settings', icon: 'settings' }
  ];

  menuGroups: MenuGroup[] = [
    {
      id: 'accesos',
      label: 'Accesos y Control',
      icon: 'shield',
      items: [
        { label: 'Usuarios', route: '/dashboard/users', icon: 'users' },
        { label: 'Roles y Permisos', route: '/dashboard/roles', icon: 'key' }
      ]
    },
    {
      id: 'operaciones',
      label: 'Operaciones',
      icon: 'briefcase',
      items: [
        { label: 'Cuentas Bancarias', route: '/dashboard/accounts', icon: 'credit-card' },
        { label: 'Transacciones', route: '/dashboard/transactions', icon: 'arrow-right-left' }
      ]
    },
    {
      id: 'divisas',
      label: 'Divisas y Tarifas',
      icon: 'dollar-sign',
      items: [
        { label: 'Tipos de Cambio', route: '/dashboard/fx/rates', icon: 'refresh-ccw' },
        { label: 'Comisiones', route: '/dashboard/fx/fees', icon: 'percent' }
      ]
    },
    {
      id: 'seguridad',
      label: 'Seguridad',
      icon: 'lock',
      items: [
        { label: 'Límites Operativos', route: '/dashboard/limits/rules', icon: 'shield-alert' }
      ]
    },
    {
      id: 'contabilidad',
      label: 'Contabilidad',
      icon: 'book-open',
      items: [
        { label: 'Períodos', route: '/dashboard/accounting/periods', icon: 'calendar' },
        { label: 'Asientos', route: '/dashboard/accounting/journal-entries', icon: 'file-text' }
      ]
    }
  ];

  // ============================================================
  // MENÚ PARA PLATFORM (SuperAdmin)
  // ============================================================
  platformMenuItems: MenuItem[] = [
    { label: 'Dashboard', route: '/platform/dashboard', icon: 'layout-dashboard' },
    { label: 'Planes', route: '/platform/plans', icon: 'credit-card' },
    { label: 'Tenants', route: '/platform/tenants', icon: 'building-2' },
    { label: 'Suscripciones', route: '/platform/subscriptions', icon: 'dollar-sign' },
    { label: 'Auditoría', route: '/platform/audit', icon: 'clipboard-list' }
  ];

  // ============================================================
  // DETECCIÓN DE RUTA
  // ============================================================
  get isPlatformRoute(): boolean {
    return this.router.url.startsWith('/platform');
  }

  // ============================================================
  // MÉTODOS
  // ============================================================
  toggleGroup(id: string): void {
    this.expandedGroups[id] = !this.expandedGroups[id];
  }

  toggleMobileMenu(): void {
    this.isMobileOpen = !this.isMobileOpen;
  }

  closeMobileMenu(): void {
    this.isMobileOpen = false;
  }

  onLogout(): void {
    this.logoutAction.emit();
  }
}
```

### `environments/environment.ts`

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080' // en produccion cambiar al proxy dejar vacio ''
};

```

### `index.html`

```html
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Finance System</title>
  <base href="/">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="icon" type="image/x-icon" href="favicon.ico">
</head>
<body>
  <app-root></app-root>
</body>
</html>

```

### `main.ts`

```typescript
import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';

bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));

```

### `styles.css`

```css
@import "tailwindcss";

/* ── Registrar variables CSS como colores del tema de Tailwind v4 ── */
@theme {
  --color-background: var(--background);
  --color-foreground: var(--foreground);
  --color-card: var(--card);
  --color-card-foreground: var(--card-foreground);
  --color-popover: var(--popover);
  --color-popover-foreground: var(--popover-foreground);
  --color-primary: var(--primary);
  --color-primary-foreground: var(--primary-foreground);
  --color-secondary: var(--secondary);
  --color-secondary-foreground: var(--secondary-foreground);
  --color-muted: var(--muted);
  --color-muted-foreground: var(--muted-foreground);
  --color-accent: var(--accent);
  --color-accent-foreground: var(--accent-foreground);
  --color-destructive: var(--destructive);
  --color-destructive-foreground: var(--destructive-foreground);
  --color-border: var(--border);
  --color-input: var(--input);
  --color-ring: var(--ring);
  --color-sidebar: var(--sidebar);
  --color-sidebar-foreground: var(--sidebar-foreground);
  --color-sidebar-primary: var(--sidebar-primary);
  --color-sidebar-primary-foreground: var(--sidebar-primary-foreground);
  --color-sidebar-accent: var(--sidebar-accent);
  --color-sidebar-accent-foreground: var(--sidebar-accent-foreground);
  --color-sidebar-border: var(--sidebar-border);
  --color-sidebar-ring: var(--sidebar-ring);
  --radius-sm: calc(var(--radius) - 4px);
  --radius-md: calc(var(--radius) - 2px);
  --radius-lg: var(--radius);
  --radius-xl: calc(var(--radius) + 4px);
}

/* ── Variables CSS: Modo Claro ── */
:root {
  color-scheme: light;

  /* Constantes estructurales */
  --radius: 0.625rem;
  
  /* Fondos y Textos Base (Mantenemos limpieza B2B) */
  --background: #ffffff;
  --foreground: #1f2937; /* Gris muy oscuro, mejor legibilidad que el negro puro */
  --card: #ffffff;
  --card-foreground: #1f2937;
  --popover: #ffffff;
  --popover-foreground: #1f2937;
  
  /* Marca PROSPERA - Botones y acciones principales */
  --primary: #1B4D30; /* Verde Oscuro */
  --primary-foreground: #ffffff;
  
  /* Elementos Secundarios */
  --secondary: #3D8C7C; /* Teal / Aqua */
  --secondary-foreground: #ffffff;
  
  /* Elementos Desactivados o Muted */
  --muted: #f3f4f6; /* Gris muy suave */
  --muted-foreground: #6b7280;
  
  /* Hover states en tablas y menús */
  --accent: #E8F5E9; /* Verde súper suave basado en tu logo */
  --accent-foreground: #1B4D30;
  
  /* Alertas destructivas (Mantenemos rojo estándar) */
  --destructive: #ef4444;
  --destructive-foreground: #ffffff;
  
  /* Bordes e Inputs */
  --border: #e5e7eb; /* Gris limpio para dividir secciones */
  --input: #e5e7eb;
  --ring: #8BC34A; /* Verde Lima brillante para el focus de inputs */
  
  /* Sidebar (Panel Lateral) - Estilo Premium B2B */
  --sidebar: #1B4D30; /* Fondo verde oscuro corporativo */
  --sidebar-foreground: #ffffff; /* Texto blanco en el menú */
  --sidebar-primary: #8BC34A; /* Verde lima para la sección activa */
  --sidebar-primary-foreground: #1B4D30; /* Texto del ítem activo */
  --sidebar-accent: #2E7D32; /* Hover en los ítems del menú (Verde bosque) */
  --sidebar-accent-foreground: #ffffff;
  --sidebar-border: #1B4D30;
  --sidebar-ring: #8BC34A;
}

/* ── Variables CSS: Modo Oscuro ── */
:root.dark {
  color-scheme: dark;

  /* Modo Oscuro adaptado a PROSPERA */
  --background: #111827; /* Fondo oscuro tipo pizarra */
  --foreground: #f9fafb;
  --card: #1f2937;
  --card-foreground: #f9fafb;
  --popover: #1f2937;
  --popover-foreground: #f9fafb;
  
  /* En dark mode, los colores brillantes funcionan mejor para botones */
  --primary: #8BC34A; /* Verde Lima */
  --primary-foreground: #1B4D30; 
  
  --secondary: #3D8C7C;
  --secondary-foreground: #ffffff;
  
  --muted: #374151;
  --muted-foreground: #9ca3af;
  
  --accent: #1B4D30; /* Hover oscuro */
  --accent-foreground: #ffffff;
  
  --destructive: #ef4444;
  --destructive-foreground: #ffffff;
  
  --border: #374151;
  --input: #374151;
  --ring: #8BC34A;
  
  /* Sidebar Modo Oscuro */
  --sidebar: #1f2937; 
  --sidebar-foreground: #f9fafb;
  --sidebar-primary: #8BC34A;
  --sidebar-primary-foreground: #1B4D30;
  --sidebar-accent: #374151;
  --sidebar-accent-foreground: #f9fafb;
  --sidebar-border: #374151;
  --sidebar-ring: #8BC34A;
}

/* ── Estilos Base ── */
@layer base {
  * {
    border-color: var(--border);
  }
  body {
    background-color: var(--background);
    color: var(--foreground);
  }
}
```


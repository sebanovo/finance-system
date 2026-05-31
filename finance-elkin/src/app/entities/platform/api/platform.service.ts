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
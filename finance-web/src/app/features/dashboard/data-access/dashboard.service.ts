import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, forkJoin, map } from 'rxjs';
import { ApiResponse } from '../../../core/models/api-response.model';

/**Matches: platform.subscriptions.application.dto response
  from GET /api/subscription/current*/
export type SubscriptionInfo = {
  id: string;
  tenantId: string;
  planCode: string;
  planName: string;
  status: 'TRIAL' | 'ACTIVE' | 'EXPIRED' | 'CANCELLED';
  startDate: string;
  expiresAt: string;
  trialDays: number;
  remainingDays: number;
  maxUsers: number;
  maxRoles: number;
};

/**Matches: platform.tenantsettings.application.dto response
  from GET /api/settings/tenant*/
export type TenantSettings = {
  companyName: string;
  legalName: string;
  timezone: string;
  currency: string;
  contactEmail: string;
  contactPhone: string;
};

/**Composed summary used by the dashboard page
  Built from multiple endpoints since /api/dashboard/tenant/summary doesn't exist*/
export type TenantSummary = {
  tenant: {
    name: string;
  };
  plan: {
    code: string;
    name: string;
    maxUsers: number;
  };
  subscription: {
    status: 'TRIAL' | 'ACTIVE' | 'EXPIRED' | 'CANCELLED';
    expiresAt: string;
    trialDays?: number;
    remainingDays?: number;
  };
  usersCount: number;
  canCreateUsers: boolean;
  alerts: string[];
};

/**Matches: identity.users.application.dto.TenantUserResponse
  Minimal type used only for counting users*/
type UserSummary = {
  id: string;
  active: boolean;
};

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private http = inject(HttpClient);

  /**Compone el resumen del tenant a partir de los endpoints que SÍ existen:
   *   - GET /api/subscription/current → datos del plan/suscripción
   *   - GET /api/users → lista de usuarios (para contar)
   *   - GET /api/settings/tenant → nombre del tenant
   *
   * NOTA: El endpoint /api/dashboard/tenant/summary no existe en el backend.
   * Este servicio compone la misma información desde endpoints disponibles.
   */
  getTenantSummary(): Observable<TenantSummary> {
    return forkJoin({
      subscription: this.http
        .get<ApiResponse<SubscriptionInfo>>('/api/subscription/current')
        .pipe(map((r) => r.data)),
      users: this.http
        .get<ApiResponse<UserSummary[]>>('/api/users')
        .pipe(map((r) => r.data)),
      settings: this.http
        .get<ApiResponse<TenantSettings>>('/api/settings/tenant')
        .pipe(map((r) => r.data)),
    }).pipe(
      map(({ subscription, users, settings }) => {
        const activeUsers = users.filter((u) => u.active).length;
        const alerts: string[] = [];

        // Generar alertas basadas en datos reales
        if (subscription.status === 'TRIAL' && subscription.remainingDays <= 3) {
          alerts.push(
            `Tu periodo de prueba expira en ${subscription.remainingDays} días.`
          );
        }
        if (subscription.status === 'EXPIRED') {
          alerts.push('Tu suscripción ha expirado. Contacta al administrador.');
        }
        if (activeUsers >= subscription.maxUsers) {
          alerts.push('Has alcanzado el límite de usuarios de tu plan.');
        }

        return {
          tenant: {
            name: settings.companyName || 'Tu Empresa',
          },
          plan: {
            code: subscription.planCode,
            name: subscription.planName,
            maxUsers: subscription.maxUsers,
          },
          subscription: {
            status: subscription.status,
            expiresAt: subscription.expiresAt,
            trialDays: subscription.trialDays,
            remainingDays: subscription.remainingDays,
          },
          usersCount: activeUsers,
          canCreateUsers: activeUsers < subscription.maxUsers,
          alerts,
        } as TenantSummary;
      })
    );
  }
}

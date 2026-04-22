import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../../core/http/models/api-response.models';
import { CreateTenantRequest, PlatformTenantResponse } from '../models/platform-tenant.models';

@Injectable({
  providedIn: 'root'
})
export class PlatformTenantService {
  private readonly http = inject(HttpClient);
  private readonly API_URL = '/api/platform/tenants';

  // 1. Listar todas las empresas (Tenants)
  getTenants(): Observable<ApiResponse<PlatformTenantResponse[]>> {
    return this.http.get<ApiResponse<PlatformTenantResponse[]>>(this.API_URL);
  }

  // 2. Obtener el detalle de un Tenant específico
  getTenantById(id: string): Observable<ApiResponse<PlatformTenantResponse>> {
    return this.http.get<ApiResponse<PlatformTenantResponse>>(`${this.API_URL}/${id}`);
  }

  // 3. Crear un nuevo Tenant
  createTenant(tenant: CreateTenantRequest): Observable<ApiResponse<PlatformTenantResponse>> {
    return this.http.post<ApiResponse<PlatformTenantResponse>>(this.API_URL, tenant);
  }

  // 4. Activar un Tenant
  activateTenant(id: string): Observable<ApiResponse<void>> {
    return this.http.patch<ApiResponse<void>>(`${this.API_URL}/${id}/activate`, {});
  }

  // 5. Desactivar/Bloquear un Tenant
  deactivateTenant(id: string): Observable<ApiResponse<void>> {
    return this.http.patch<ApiResponse<void>>(`${this.API_URL}/${id}/deactivate`, {});
  }
}
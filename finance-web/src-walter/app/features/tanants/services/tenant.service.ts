import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import {
  CreateTenantRequest,
  TenantDto,
  TenantResponse,
} from "../models/tenant.type";

@Injectable({
  providedIn: "root",
})
export class TenantsApi {
  private readonly http = inject(HttpClient);

  getTenants(): Observable<TenantDto[]> {
    return this.http
      .get<TenantResponse<TenantDto[]>>("/api/platform/tenants")
      .pipe(map(response => response.data));
  }

  createTenant(payload: CreateTenantRequest): Observable<TenantDto> {
    return this.http
      .post<TenantResponse<TenantDto>>("/api/platform/tenants", payload)
      .pipe(map(response => response.data));
  }

  getTenantById(tenantId: string): Observable<TenantDto> {
    return this.http
      .get<TenantResponse<TenantDto>>(`/api/platform/tenants/${tenantId}`)
      .pipe(map(response => response.data));
  }

  activateTenant(tenantId: string): Observable<TenantDto> {
    return this.http
      .patch<
        TenantResponse<TenantDto>
      >(`/api/platform/tenants/${tenantId}/activate`, {})
      .pipe(map(response => response.data));
  }

  deactivateTenant(tenantId: string): Observable<TenantDto> {
    return this.http
      .patch<
        TenantResponse<TenantDto>
      >(`/api/platform/tenants/${tenantId}/deactivate`, {})
      .pipe(map(response => response.data));
  }
}

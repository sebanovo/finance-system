import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import {
  TenantSettingsDto,
  TenantSettingsResponse,
  UpdateTenantSettingsRequest,
} from "../models/setting.type";

@Injectable({
  providedIn: "root",
})
export class TenantSettingsApi {
  private readonly http = inject(HttpClient);

  getTenantSettings(): Observable<TenantSettingsDto> {
    return this.http
      .get<TenantSettingsResponse<TenantSettingsDto>>("/api/settings/tenant")
      .pipe(map(response => response.data));
  }

  updateTenantSettings(
    payload: UpdateTenantSettingsRequest
  ): Observable<TenantSettingsDto> {
    return this.http
      .put<
        TenantSettingsResponse<TenantSettingsDto>
      >("/api/settings/tenant", payload)
      .pipe(map(response => response.data));
  }
}

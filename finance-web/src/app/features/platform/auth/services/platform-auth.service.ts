import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../../core/http/models/api-response.models';
import { PlatformLoginRequest, AuthTokenResponse, PlatformUserResponse } from '../models/platform-tenant.models';

@Injectable({
  providedIn: 'root'
})
export class PlatformAuthService {
  private readonly http = inject(HttpClient);
  private readonly API_URL = '/api/platform/auth';

  login(credentials: PlatformLoginRequest): Observable<ApiResponse<AuthTokenResponse>> {
    return this.http.post<ApiResponse<AuthTokenResponse>>(`${this.API_URL}/login`, credentials);
  }

  refresh(refreshToken: string): Observable<ApiResponse<AuthTokenResponse>> {
    return this.http.post<ApiResponse<AuthTokenResponse>>(`${this.API_URL}/refresh`, { refreshToken });
  }

  getMe(): Observable<ApiResponse<PlatformUserResponse>> {
    return this.http.get<ApiResponse<PlatformUserResponse>>(`${this.API_URL}/me`);
  }
}
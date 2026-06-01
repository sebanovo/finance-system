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

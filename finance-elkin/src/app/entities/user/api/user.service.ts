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

  createUser(request: CreateTenantUserRequest): Observable<ApiResponse<TenantUserResponse>> {
    return this.http.post<ApiResponse<TenantUserResponse>>(this.API_URL, request);
  }
}

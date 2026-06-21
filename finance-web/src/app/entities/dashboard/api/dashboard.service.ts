import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../shared/api';
import { TenantSummaryResponse } from '../model/tenant-summary-response.model';
import { CustomerSummaryResponse } from '../model/customer-summary-response.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/api/dashboard`;

  getTenantSummary(): Observable<ApiResponse<TenantSummaryResponse>> {
    // El interceptor ya se encarga de inyectar el token y el X-Tenant-Slug
    return this.http.get<ApiResponse<TenantSummaryResponse>>(`${this.API_URL}/summary`);
  }

  getCustomerSummary(): Observable<ApiResponse<CustomerSummaryResponse>> {
    return this.http.get<ApiResponse<CustomerSummaryResponse>>(`${environment.apiUrl}/api/me/dashboard/summary`);
  }
}

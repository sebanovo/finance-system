import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/api';
import { PageResponse } from '../../accounts/model/accounts.model';
import {
  CreateLoanRequest,
  CreateMyLoanRequest,
  LoanDetailResponse,
  LoanInstallmentResponse,
  LoanResponse,
  RecordLoanPaymentRequest
} from '../model/loans.model';

@Injectable({ providedIn: 'root' })
export class LoansService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/api/loans`;
  private readonly MY_API_URL = `${environment.apiUrl}/api/me/loans`;

  private pageParams(page = 0, size = 50): HttpParams {
    return new HttpParams().set('page', page.toString()).set('size', size.toString());
  }

  // ---- Admin ----
  requestLoan(request: CreateLoanRequest): Observable<ApiResponse<LoanResponse>> {
    return this.http.post<ApiResponse<LoanResponse>>(this.API_URL, request);
  }

  listLoans(page = 0, size = 50): Observable<ApiResponse<PageResponse<LoanResponse>>> {
    return this.http.get<ApiResponse<PageResponse<LoanResponse>>>(this.API_URL, { params: this.pageParams(page, size) });
  }

  getLoan(id: string): Observable<ApiResponse<LoanDetailResponse>> {
    return this.http.get<ApiResponse<LoanDetailResponse>>(`${this.API_URL}/${id}`);
  }

  getSchedule(id: string): Observable<ApiResponse<LoanInstallmentResponse[]>> {
    return this.http.get<ApiResponse<LoanInstallmentResponse[]>>(`${this.API_URL}/${id}/schedule`);
  }

  approveLoan(id: string): Observable<ApiResponse<LoanResponse>> {
    return this.http.patch<ApiResponse<LoanResponse>>(`${this.API_URL}/${id}/approve`, {});
  }

  rejectLoan(id: string, reason?: string): Observable<ApiResponse<LoanResponse>> {
    const params = reason ? new HttpParams().set('reason', reason) : undefined;
    return this.http.patch<ApiResponse<LoanResponse>>(`${this.API_URL}/${id}/reject`, {}, { params });
  }

  disburseLoan(id: string): Observable<ApiResponse<LoanDetailResponse>> {
    return this.http.post<ApiResponse<LoanDetailResponse>>(`${this.API_URL}/${id}/disburse`, {});
  }

  payLoan(id: string, request: RecordLoanPaymentRequest): Observable<ApiResponse<LoanDetailResponse>> {
    return this.http.post<ApiResponse<LoanDetailResponse>>(`${this.API_URL}/${id}/payments`, request);
  }

  // ---- Self-service (me) ----
  requestMyLoan(request: CreateMyLoanRequest): Observable<ApiResponse<LoanResponse>> {
    return this.http.post<ApiResponse<LoanResponse>>(this.MY_API_URL, request);
  }

  listMyLoans(page = 0, size = 50): Observable<ApiResponse<PageResponse<LoanResponse>>> {
    return this.http.get<ApiResponse<PageResponse<LoanResponse>>>(this.MY_API_URL, { params: this.pageParams(page, size) });
  }

  getMyLoan(id: string): Observable<ApiResponse<LoanDetailResponse>> {
    return this.http.get<ApiResponse<LoanDetailResponse>>(`${this.MY_API_URL}/${id}`);
  }

  getMySchedule(id: string): Observable<ApiResponse<LoanInstallmentResponse[]>> {
    return this.http.get<ApiResponse<LoanInstallmentResponse[]>>(`${this.MY_API_URL}/${id}/schedule`);
  }

  payMyLoan(id: string, request: RecordLoanPaymentRequest): Observable<ApiResponse<LoanDetailResponse>> {
    return this.http.post<ApiResponse<LoanDetailResponse>>(`${this.MY_API_URL}/${id}/payments`, request);
  }
}

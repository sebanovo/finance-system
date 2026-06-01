import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/api';
import {
  AccountOwnerResponse,
  AccountBalanceResponse,
  CreateAccountRequest,
  UpdateAccountRequest
} from '../model/accounts.model';

@Injectable({
  providedIn: 'root'
})
export class AccountsService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/api/accounts`;

  createAccount(request: CreateAccountRequest): Observable<ApiResponse<AccountOwnerResponse>> {
    return this.http.post<ApiResponse<AccountOwnerResponse>>(this.API_URL, request);
  }

  listAccounts(): Observable<ApiResponse<AccountOwnerResponse[]>> {
    return this.http.get<ApiResponse<AccountOwnerResponse[]>>(this.API_URL);
  }

  getAccountById(id: string): Observable<ApiResponse<AccountOwnerResponse>> {
    return this.http.get<ApiResponse<AccountOwnerResponse>>(`${this.API_URL}/${id}`);
  }

  getAccountBalance(id: string): Observable<ApiResponse<AccountBalanceResponse>> {
    return this.http.get<ApiResponse<AccountBalanceResponse>>(`${this.API_URL}/${id}/balance`);
  }

  updateAccount(id: string, request: UpdateAccountRequest): Observable<ApiResponse<AccountOwnerResponse>> {
    return this.http.put<ApiResponse<AccountOwnerResponse>>(`${this.API_URL}/${id}`, request);
  }

  approveAccount(id: string): Observable<ApiResponse<AccountOwnerResponse>> {
    return this.http.patch<ApiResponse<AccountOwnerResponse>>(`${this.API_URL}/${id}/approve`, {});
  }

  activateAccount(id: string): Observable<ApiResponse<AccountOwnerResponse>> {
    return this.http.patch<ApiResponse<AccountOwnerResponse>>(`${this.API_URL}/${id}/activate`, {});
  }

  blockAccount(id: string, reason?: string): Observable<ApiResponse<AccountOwnerResponse>> {
    let params = new HttpParams();
    if (reason) params = params.set('reason', reason);
    return this.http.patch<ApiResponse<AccountOwnerResponse>>(`${this.API_URL}/${id}/block`, {}, { params });
  }

  freezeAccount(id: string, reason?: string): Observable<ApiResponse<AccountOwnerResponse>> {
    let params = new HttpParams();
    if (reason) params = params.set('reason', reason);
    return this.http.patch<ApiResponse<AccountOwnerResponse>>(`${this.API_URL}/${id}/freeze`, {}, { params });
  }

  closeAccount(id: string, reason?: string): Observable<ApiResponse<AccountOwnerResponse>> {
    let params = new HttpParams();
    if (reason) params = params.set('reason', reason);
    return this.http.patch<ApiResponse<AccountOwnerResponse>>(`${this.API_URL}/${id}/close`, {}, { params });
  }
}

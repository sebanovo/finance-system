import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/api';
import {
  PageResponse,
  AccountOwnerResponse,
  AccountBalanceResponse,
  AccountLookupResponse,
  CreateAccountRequest,
  UpdateAccountRequest
} from '../model/accounts.model';

@Injectable({
  providedIn: 'root'
})
export class AccountsService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/api/accounts`;
  private readonly MY_API_URL = `${environment.apiUrl}/api/me/accounts`;

  private buildPageParams(page = 0, size = 20): HttpParams {
    return new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
  }

  createAccount(request: CreateAccountRequest): Observable<ApiResponse<AccountOwnerResponse>> {
    return this.http.post<ApiResponse<AccountOwnerResponse>>(this.API_URL, request);
  }

  listAccounts(page = 0, size = 20): Observable<ApiResponse<PageResponse<AccountOwnerResponse>>> {
    return this.http.get<ApiResponse<PageResponse<AccountOwnerResponse>>>(
      this.API_URL,
      { params: this.buildPageParams(page, size) }
    );
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

  createMyAccount(request: { accountName: string; customAlias?: string; accountType: string; currency: string }): Observable<ApiResponse<AccountOwnerResponse>> {
    return this.http.post<ApiResponse<AccountOwnerResponse>>(this.MY_API_URL, request);
  }

  listMyAccounts(page = 0, size = 20): Observable<ApiResponse<PageResponse<AccountOwnerResponse>>> {
    return this.http.get<ApiResponse<PageResponse<AccountOwnerResponse>>>(
      this.MY_API_URL,
      { params: this.buildPageParams(page, size) }
    );
  }

  getMyAccountById(id: string): Observable<ApiResponse<AccountOwnerResponse>> {
    return this.http.get<ApiResponse<AccountOwnerResponse>>(`${this.MY_API_URL}/${id}`);
  }

  resolveMyAccountByNumber(accountNumber: string): Observable<ApiResponse<AccountLookupResponse>> {
    return this.http.get<ApiResponse<AccountLookupResponse>>(`${this.MY_API_URL}/lookup/${encodeURIComponent(accountNumber)}`);
  }

  getMyAccountBalance(id: string): Observable<ApiResponse<AccountBalanceResponse>> {
    return this.http.get<ApiResponse<AccountBalanceResponse>>(`${this.MY_API_URL}/${id}/balance`);
  }

  updateMyAccountAlias(id: string, request: { customAlias?: string }): Observable<ApiResponse<AccountOwnerResponse>> {
    return this.http.patch<ApiResponse<AccountOwnerResponse>>(`${this.MY_API_URL}/${id}/alias`, request);
  }
}

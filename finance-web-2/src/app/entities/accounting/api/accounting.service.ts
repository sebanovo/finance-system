import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/api';
import {
  AccountingPeriodResponse,
  CloseAccountingPeriodRequest,
  CreateAccountingPeriodRequest,
  JournalEntryResponse
} from '../model/accounting.model';

@Injectable({
  providedIn: 'root'
})
export class AccountingService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/api/accounting`;

  // --- Periods ---
  listPeriods(): Observable<ApiResponse<AccountingPeriodResponse[]>> {
    return this.http.get<ApiResponse<AccountingPeriodResponse[]>>(`${this.API_URL}/periods`);
  }

  createPeriod(request: CreateAccountingPeriodRequest): Observable<ApiResponse<AccountingPeriodResponse>> {
    return this.http.post<ApiResponse<AccountingPeriodResponse>>(`${this.API_URL}/periods`, request);
  }

  closePeriod(id: string, request: CloseAccountingPeriodRequest): Observable<ApiResponse<AccountingPeriodResponse>> {
    return this.http.patch<ApiResponse<AccountingPeriodResponse>>(`${this.API_URL}/periods/${id}/close`, request);
  }

  // --- Journal Entries ---
  listJournalEntries(): Observable<ApiResponse<JournalEntryResponse[]>> {
    return this.http.get<ApiResponse<JournalEntryResponse[]>>(`${this.API_URL}/journal-entries`);
  }

  getJournalEntryById(id: string): Observable<ApiResponse<JournalEntryResponse>> {
    return this.http.get<ApiResponse<JournalEntryResponse>>(`${this.API_URL}/journal-entries/${id}`);
  }
}

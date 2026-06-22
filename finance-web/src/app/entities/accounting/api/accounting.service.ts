import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/api';
import {
  PageResponse,
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
  listPeriods(page = 0, size = 20): Observable<ApiResponse<PageResponse<AccountingPeriodResponse>>> {
    return this.http.get<ApiResponse<PageResponse<AccountingPeriodResponse>>>(`${this.API_URL}/periods`, {
      params: { page, size }
    });
  }

  createPeriod(request: CreateAccountingPeriodRequest): Observable<ApiResponse<AccountingPeriodResponse>> {
    return this.http.post<ApiResponse<AccountingPeriodResponse>>(`${this.API_URL}/periods`, request);
  }

  closePeriod(id: string, request: CloseAccountingPeriodRequest): Observable<ApiResponse<AccountingPeriodResponse>> {
    return this.http.patch<ApiResponse<AccountingPeriodResponse>>(`${this.API_URL}/periods/${id}/close`, request);
  }

  // --- Journal Entries ---
  listJournalEntries(page = 0, size = 20): Observable<ApiResponse<PageResponse<JournalEntryResponse>>> {
    return this.http.get<ApiResponse<PageResponse<JournalEntryResponse>>>(`${this.API_URL}/journal-entries`, {
      params: { page, size }
    });
  }

  getJournalEntryById(id: string): Observable<ApiResponse<JournalEntryResponse>> {
    return this.http.get<ApiResponse<JournalEntryResponse>>(`${this.API_URL}/journal-entries/${id}`);
  }
}

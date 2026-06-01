import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../shared/api';
import {
  ReportCatalogResponse,
  PageResponse,
  ReportExecutionDetailResponse,
  ReportExecutionSummaryResponse,
  ReportMode,
  ReportResultResponse,
  ReportSchemaResponse
} from '../model/reports.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReportsService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/reports`;

  getAnalyticCatalog(): Observable<ApiResponse<ReportCatalogResponse>> {
    return this.http.get<ApiResponse<ReportCatalogResponse>>(`${this.baseUrl}/analytic`);
  }

  getManagerialCatalog(): Observable<ApiResponse<ReportCatalogResponse>> {
    return this.http.get<ApiResponse<ReportCatalogResponse>>(`${this.baseUrl}/managerial`);
  }

  getSchema(mode: ReportMode, reportType: string): Observable<ApiResponse<ReportSchemaResponse>> {
    const path = mode === 'ANALYTIC' ? 'analytic' : 'managerial';
    return this.http.get<ApiResponse<ReportSchemaResponse>>(`${this.baseUrl}/${path}/${reportType}/schema`);
  }

  getExecutions(params: { reportType?: string; mode?: ReportMode; page?: number; size?: number }): Observable<ApiResponse<PageResponse<ReportExecutionSummaryResponse>>> {
    const query = new URLSearchParams();
    if (params.reportType) query.set('reportType', params.reportType);
    if (params.mode) query.set('mode', params.mode);
    query.set('page', String(params.page ?? 0));
    query.set('size', String(params.size ?? 10));
    const suffix = query.toString() ? `?${query.toString()}` : '';
    return this.http.get<ApiResponse<PageResponse<ReportExecutionSummaryResponse>>>(`${this.baseUrl}/executions${suffix}`);
  }

  getExecutionById(executionId: string): Observable<ApiResponse<ReportExecutionDetailResponse>> {
    return this.http.get<ApiResponse<ReportExecutionDetailResponse>>(`${this.baseUrl}/executions/${executionId}`);
  }

  downloadExport(exportId: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/exports/${exportId}/download`, { responseType: 'blob' });
  }

  runAnalyticReport(reportType: string, payload: Record<string, unknown>): Observable<ApiResponse<ReportResultResponse>> {
    return this.http.post<ApiResponse<ReportResultResponse>>(`${this.baseUrl}/analytic/${reportType}/run`, payload);
  }

  runManagerialReport(reportType: string, payload: Record<string, unknown>): Observable<ApiResponse<ReportResultResponse>> {
    return this.http.post<ApiResponse<ReportResultResponse>>(`${this.baseUrl}/managerial/${reportType}/run`, payload);
  }
}

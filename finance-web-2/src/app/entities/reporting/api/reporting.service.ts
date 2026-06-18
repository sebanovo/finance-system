import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../shared/api';
import { environment } from '../../../../environments/environment';
import {
  PageResponse,
  ReportDefinition,
  ReportExecutionDetail,
  ReportExecutionSummary,
  ReportExport,
  ReportResult,
  ReportingScope
} from '../model/reporting.model';

/** Talks to the new reporting endpoints. The auth interceptor adds the
 *  Authorization token and (for tenant scope) the X-Tenant-Slug header. */
@Injectable({ providedIn: 'root' })
export class ReportingService {
  private readonly http = inject(HttpClient);

  private base(scope: ReportingScope): string {
    return scope === 'platform'
      ? `${environment.apiUrl}/api/platform/reports`
      : `${environment.apiUrl}/api/reports`;
  }

  listDefinitions(scope: ReportingScope): Observable<ApiResponse<ReportDefinition[]>> {
    return this.http.get<ApiResponse<ReportDefinition[]>>(`${this.base(scope)}/definitions`);
  }

  run(scope: ReportingScope, key: string, params: Record<string, unknown>): Observable<ApiResponse<ReportResult>> {
    return this.http.post<ApiResponse<ReportResult>>(`${this.base(scope)}/run/${key}`, { params });
  }

  aiText(scope: ReportingScope, prompt: string): Observable<ApiResponse<ReportResult>> {
    return this.http.post<ApiResponse<ReportResult>>(`${this.base(scope)}/ai/text`, { prompt });
  }

  aiVoice(scope: ReportingScope, audio: Blob): Observable<ApiResponse<ReportResult>> {
    const form = new FormData();
    form.append('audio', audio, 'audio.webm');
    return this.http.post<ApiResponse<ReportResult>>(`${this.base(scope)}/ai/voice`, form);
  }

  listExecutions(scope: ReportingScope, page: number, size = 20): Observable<ApiResponse<PageResponse<ReportExecutionSummary>>> {
    return this.http.get<ApiResponse<PageResponse<ReportExecutionSummary>>>(
      `${this.base(scope)}/executions?page=${page}&size=${size}`);
  }

  getExecution(scope: ReportingScope, id: string): Observable<ApiResponse<ReportExecutionDetail>> {
    return this.http.get<ApiResponse<ReportExecutionDetail>>(`${this.base(scope)}/executions/${id}`);
  }

  rerun(scope: ReportingScope, id: string): Observable<ApiResponse<ReportResult>> {
    return this.http.post<ApiResponse<ReportResult>>(`${this.base(scope)}/executions/${id}/rerun`, {});
  }

  createExport(scope: ReportingScope, id: string, format: 'PDF' | 'XLSX'): Observable<ApiResponse<ReportExport>> {
    return this.http.post<ApiResponse<ReportExport>>(`${this.base(scope)}/executions/${id}/exports`, { format });
  }

  download(scope: ReportingScope, exportId: string): Observable<Blob> {
    return this.http.get(`${this.base(scope)}/exports/${exportId}/download`, { responseType: 'blob' });
  }
}

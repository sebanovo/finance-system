import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/api';
import {
  LimitRuleResponse,
  CreateLimitRuleRequest,
  UpdateLimitRuleRequest,
  LimitEvaluationRequest,
  LimitEvaluationResponse
} from '../model/limits.model';

@Injectable({
  providedIn: 'root'
})
export class LimitsService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/api/limits`;

  listRules(): Observable<ApiResponse<LimitRuleResponse[]>> {
    return this.http.get<ApiResponse<LimitRuleResponse[]>>(`${this.API_URL}/rules`);
  }

  getRule(id: string): Observable<ApiResponse<LimitRuleResponse>> {
    return this.http.get<ApiResponse<LimitRuleResponse>>(`${this.API_URL}/rules/${id}`);
  }

  createRule(request: CreateLimitRuleRequest): Observable<ApiResponse<LimitRuleResponse>> {
    return this.http.post<ApiResponse<LimitRuleResponse>>(`${this.API_URL}/rules`, request);
  }

  updateRule(id: string, request: UpdateLimitRuleRequest): Observable<ApiResponse<LimitRuleResponse>> {
    return this.http.patch<ApiResponse<LimitRuleResponse>>(`${this.API_URL}/rules/${id}`, request);
  }

  deleteRule(id: string): Observable<ApiResponse<LimitRuleResponse>> {
    return this.http.delete<ApiResponse<LimitRuleResponse>>(`${this.API_URL}/rules/${id}`);
  }

  evaluate(request: LimitEvaluationRequest): Observable<ApiResponse<LimitEvaluationResponse>> {
    return this.http.post<ApiResponse<LimitEvaluationResponse>>(`${this.API_URL}/evaluate`, request);
  }
}

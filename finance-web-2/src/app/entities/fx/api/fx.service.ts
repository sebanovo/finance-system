import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/api';
import {
  FxExchangeRateResponse,
  OperationFeeResponse,
  CreateFxExchangeRateRequest,
  UpdateFxExchangeRateRequest,
  CreateOperationFeeRequest,
  UpdateOperationFeeRequest
} from '../model/fx.model';

@Injectable({
  providedIn: 'root'
})
export class FxService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/api/fx`;

  // Rates
  listRates(): Observable<ApiResponse<FxExchangeRateResponse[]>> {
    return this.http.get<ApiResponse<FxExchangeRateResponse[]>>(`${this.API_URL}/rates`);
  }

  getRate(id: string): Observable<ApiResponse<FxExchangeRateResponse>> {
    return this.http.get<ApiResponse<FxExchangeRateResponse>>(`${this.API_URL}/rates/${id}`);
  }

  createRate(request: CreateFxExchangeRateRequest): Observable<ApiResponse<FxExchangeRateResponse>> {
    return this.http.post<ApiResponse<FxExchangeRateResponse>>(`${this.API_URL}/rates`, request);
  }

  updateRate(id: string, request: UpdateFxExchangeRateRequest): Observable<ApiResponse<FxExchangeRateResponse>> {
    return this.http.put<ApiResponse<FxExchangeRateResponse>>(`${this.API_URL}/rates/${id}`, request);
  }

  deleteRate(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/rates/${id}`);
  }

  // Fees
  listFees(): Observable<ApiResponse<OperationFeeResponse[]>> {
    return this.http.get<ApiResponse<OperationFeeResponse[]>>(`${this.API_URL}/fees`);
  }

  getFee(id: string): Observable<ApiResponse<OperationFeeResponse>> {
    return this.http.get<ApiResponse<OperationFeeResponse>>(`${this.API_URL}/fees/${id}`);
  }

  createFee(request: CreateOperationFeeRequest): Observable<ApiResponse<OperationFeeResponse>> {
    return this.http.post<ApiResponse<OperationFeeResponse>>(`${this.API_URL}/fees`, request);
  }

  updateFee(id: string, request: UpdateOperationFeeRequest): Observable<ApiResponse<OperationFeeResponse>> {
    return this.http.put<ApiResponse<OperationFeeResponse>>(`${this.API_URL}/fees/${id}`, request);
  }

  deleteFee(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/fees/${id}`);
  }
}

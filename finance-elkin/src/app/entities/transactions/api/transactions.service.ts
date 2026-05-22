import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/api';
import {
  TransactionResponse,
  QrTransactionIntentResponse,
  CreateDepositTransactionRequest,
  CreateFeeTransactionRequest,
  CreateHoldTransactionRequest,
  CreateQrTransactionIntentRequest,
  CreatePaymentTransactionRequest,
  CreateAdjustmentTransactionRequest,
  CreateWithdrawalTransactionRequest,
  CreateReleaseTransactionRequest,
  CreateTransferTransactionRequest,
  CreateReversalTransactionRequest,
  CreateRefundTransactionRequest
} from '../model/transactions.model';

@Injectable({
  providedIn: 'root'
})
export class TransactionsService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/api/transactions`;

  // Consultas
  listTransactions(): Observable<ApiResponse<TransactionResponse[]>> {
    return this.http.get<ApiResponse<TransactionResponse[]>>(this.API_URL);
  }

  getTransactionById(id: string): Observable<ApiResponse<TransactionResponse>> {
    return this.http.get<ApiResponse<TransactionResponse>>(`${this.API_URL}/${id}`);
  }

  // Creación de Transacciones
  createDeposit(request: CreateDepositTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/deposits`, request);
  }

  createFee(request: CreateFeeTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/fees`, request);
  }

  createHold(request: CreateHoldTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/holds`, request);
  }

  createQrIntent(request: CreateQrTransactionIntentRequest): Observable<ApiResponse<QrTransactionIntentResponse>> {
    return this.http.post<ApiResponse<QrTransactionIntentResponse>>(`${this.API_URL}/qr/intents`, request);
  }

  createPayment(request: CreatePaymentTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/payments`, request);
  }

  createAdjustment(request: CreateAdjustmentTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/adjustments`, request);
  }

  createWithdrawal(request: CreateWithdrawalTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/withdrawals`, request);
  }

  createRelease(request: CreateReleaseTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/releases`, request);
  }

  createTransfer(request: CreateTransferTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/transfers`, request);
  }

  reverseTransaction(id: string, request: CreateReversalTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/${id}/reversal`, request);
  }

  refundTransaction(id: string, request: CreateRefundTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/${id}/refunds`, request);
  }
}

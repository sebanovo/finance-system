import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/api';
import {
  PageResponse,
  TransactionResponse,
  QrTransactionIntentResponse,
  CreateDepositTransactionRequest,
  CreateFeeTransactionRequest,
  CreateHoldTransactionRequest,
  CreateQrTransactionIntentRequest,
  ConfirmQrTransactionRequest,
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
  private readonly MY_API_URL = `${environment.apiUrl}/api/me/transactions`;

  private buildPageParams(page = 0, size = 20): HttpParams {
    return new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
  }

  // Consultas
  listTransactions(page = 0, size = 20): Observable<ApiResponse<PageResponse<TransactionResponse>>> {
    return this.http.get<ApiResponse<PageResponse<TransactionResponse>>>(
      this.API_URL,
      { params: this.buildPageParams(page, size) }
    );
  }

  listMyTransactions(page = 0, size = 20): Observable<ApiResponse<PageResponse<TransactionResponse>>> {
    return this.http.get<ApiResponse<PageResponse<TransactionResponse>>>(
      this.MY_API_URL,
      { params: this.buildPageParams(page, size) }
    );
  }

  getTransactionById(id: string): Observable<ApiResponse<TransactionResponse>> {
    return this.http.get<ApiResponse<TransactionResponse>>(`${this.API_URL}/${id}`);
  }

  getMyTransactionById(id: string): Observable<ApiResponse<TransactionResponse>> {
    return this.http.get<ApiResponse<TransactionResponse>>(`${this.MY_API_URL}/${id}`);
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

  createMyQrIntent(request: CreateQrTransactionIntentRequest): Observable<ApiResponse<QrTransactionIntentResponse>> {
    return this.http.post<ApiResponse<QrTransactionIntentResponse>>(`${this.MY_API_URL}/qr/intents`, request);
  }

  getMyQrTransactionIntent(id: string): Observable<ApiResponse<QrTransactionIntentResponse>> {
    return this.http.get<ApiResponse<QrTransactionIntentResponse>>(`${this.MY_API_URL}/qr/intents/${id}`);
  }

  cancelMyQrTransactionIntent(id: string): Observable<ApiResponse<QrTransactionIntentResponse>> {
    return this.http.post<ApiResponse<QrTransactionIntentResponse>>(`${this.MY_API_URL}/qr/intents/${id}/cancel`, {});
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

  createMyDeposit(request: CreateDepositTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.MY_API_URL}/deposits`, request);
  }

  createMyHold(request: CreateHoldTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.MY_API_URL}/holds`, request);
  }

  confirmMyQrTransaction(id: string, request: ConfirmQrTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.MY_API_URL}/qr/${id}/confirm`, request);
  }

  createMyPayment(request: CreatePaymentTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.MY_API_URL}/payments`, request);
  }

  createMyWithdrawal(request: CreateWithdrawalTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.MY_API_URL}/withdrawals`, request);
  }

  createMyRelease(request: CreateReleaseTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.MY_API_URL}/releases`, request);
  }

  createMyTransfer(request: CreateTransferTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.MY_API_URL}/transfers`, request);
  }

  reverseTransaction(id: string, request: CreateReversalTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/${id}/reversal`, request);
  }

  refundTransaction(id: string, request: CreateRefundTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.API_URL}/${id}/refunds`, request);
  }
}

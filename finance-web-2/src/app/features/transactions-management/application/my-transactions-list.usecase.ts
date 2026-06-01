import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { v4 as uuidv4 } from 'uuid';
import {
  TransactionsService,
  TransactionResponse,
  QrTransactionIntentResponse,
  PageResponse,
  CreateDepositTransactionRequest,
  CreateHoldTransactionRequest,
  CreateQrTransactionIntentRequest,
  ConfirmQrTransactionRequest,
  CreatePaymentTransactionRequest,
  CreateWithdrawalTransactionRequest,
  CreateReleaseTransactionRequest,
  CreateTransferTransactionRequest,
  TransactionOperationType
} from '../../../entities/transactions';

export interface MyTransactionsListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  page: PageResponse<TransactionResponse> | null;
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class MyTransactionsListUseCase {
  private readonly transactionsService = inject(TransactionsService);

  private readonly state = signal<MyTransactionsListState>({
    status: 'idle',
    page: null,
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly page = computed(() => this.state().page);
  readonly data = computed(() => this.state().page?.content ?? []);
  readonly error = computed(() => this.state().error);

  async loadTransactions(page = 0, size = 20): Promise<void> {
    this.state.set({ status: 'loading', page: this.state().page, error: null });

    try {
      const response = await firstValueFrom(this.transactionsService.listMyTransactions(page, size));

      if (response.success && response.data) {
        this.state.set({
          status: 'success',
          page: response.data,
          error: null
        });
      } else {
        this.state.set({
          status: 'error',
          page: null,
          error: response.message || 'No se pudieron cargar tus movimientos'
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', page: null, error: errorMsg });
    }
  }

  async createTransaction(type: TransactionOperationType, request: any, referenceId?: string): Promise<void> {
    try {
      if (!request.idempotencyKey) {
        request.idempotencyKey = uuidv4();
      }

      let response: any = null;
      switch (type) {
        case 'deposit':
          response = await firstValueFrom(this.transactionsService.createMyDeposit(request as CreateDepositTransactionRequest));
          break;
        case 'withdrawal':
          response = await firstValueFrom(this.transactionsService.createMyWithdrawal(request as CreateWithdrawalTransactionRequest));
          break;
        case 'transfer':
          response = await firstValueFrom(this.transactionsService.createMyTransfer(request as CreateTransferTransactionRequest));
          break;
        case 'payment':
          response = await firstValueFrom(this.transactionsService.createMyPayment(request as CreatePaymentTransactionRequest));
          break;
        case 'hold':
          response = await firstValueFrom(this.transactionsService.createMyHold(request as CreateHoldTransactionRequest));
          break;
        case 'release':
          response = await firstValueFrom(this.transactionsService.createMyRelease(request as CreateReleaseTransactionRequest));
          break;
        case 'qr-intent':
          response = await firstValueFrom(this.transactionsService.createMyQrIntent(request));
          break;
        case 'qr-confirm': {
          const qrId = referenceId || request.qrTransactionId;
          if (!qrId) {
            throw new Error('Falta el identificador de la intención QR');
          }
          const confirmRequest: ConfirmQrTransactionRequest = {
            sourceAccountId: request.sourceAccountId,
            idempotencyKey: request.idempotencyKey
          };
          response = await firstValueFrom(this.transactionsService.confirmMyQrTransaction(qrId, confirmRequest));
          break;
        }
        default:
          throw new Error(`Operación no soportada para cliente: ${type}`);
      }

      if (response.success) {
        await this.loadTransactions(this.page()?.number ?? 0, this.page()?.size ?? 20);
      } else {
        throw new Error(response.message || `Error al ejecutar la transacción de tipo ${type}`);
      }
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }

  async createQrIntent(request: CreateQrTransactionIntentRequest): Promise<QrTransactionIntentResponse> {
    try {
      if (!request.idempotencyKey) {
        request.idempotencyKey = uuidv4();
      }

      const response = await firstValueFrom(this.transactionsService.createMyQrIntent(request));
      if (response.success && response.data) {
        await this.loadTransactions(this.page()?.number ?? 0, this.page()?.size ?? 20);
        return response.data;
      }

      throw new Error(response.message || 'Error al crear la intención QR');
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }

  async getQrIntent(id: string): Promise<QrTransactionIntentResponse> {
    try {
      const response = await firstValueFrom(this.transactionsService.getMyQrTransactionIntent(id));
      if (response.success && response.data) {
        return response.data;
      }

      throw new Error(response.message || 'Error al recuperar la intención QR');
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }

  async cancelQrIntent(id: string): Promise<QrTransactionIntentResponse> {
    try {
      const response = await firstValueFrom(this.transactionsService.cancelMyQrTransactionIntent(id));
      if (response.success && response.data) {
        await this.loadTransactions(this.page()?.number ?? 0, this.page()?.size ?? 20);
        return response.data;
      }

      throw new Error(response.message || 'Error al cancelar la intención QR');
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }

}

import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { v4 as uuidv4 } from 'uuid';
import {
  TransactionsService,
  TransactionResponse,
  QrTransactionIntentResponse,
  PageResponse,
  CreateDepositTransactionRequest,
  CreateWithdrawalTransactionRequest,
  CreateTransferTransactionRequest,
  CreatePaymentTransactionRequest,
  CreateFeeTransactionRequest,
  CreateHoldTransactionRequest,
  CreateReleaseTransactionRequest,
  CreateAdjustmentTransactionRequest,
  CreateQrTransactionIntentRequest,
  CreateReversalTransactionRequest,
  CreateRefundTransactionRequest,
  TransactionOperationType
} from '../../../entities/transactions';
import { AuthStorageService } from '../../../shared/lib/storage/auth-storage.service';

export interface TransactionsListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  page: PageResponse<TransactionResponse> | null;
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class TransactionsListUseCase {
  private readonly transactionsService = inject(TransactionsService);
  private readonly authStorage = inject(AuthStorageService);
  private lastSessionKey: string | null = null;

  private readonly state = signal<TransactionsListState>({
    status: 'idle',
    page: null,
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly page = computed(() => this.state().page);
  readonly data = computed(() => this.state().page?.content ?? []);
  readonly error = computed(() => this.state().error);

  async loadTransactions(page = 0, size = 20): Promise<void> {
    this.syncTenantContext();

    if (!this.authStorage.hasValidTenantSession()) {
      this.resetState();
      return;
    }

    this.state.set({ status: 'loading', page: this.state().page, error: null });

    try {
      const response = await firstValueFrom(this.transactionsService.listTransactions(page, size));

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
          error: response.message || 'No se pudieron cargar las transacciones'
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', page: null, error: errorMsg });
    }
  }

  resetState(): void {
    this.state.set({
      status: 'idle',
      page: null,
      error: null
    });
  }

  private syncTenantContext(): void {
    const currentSessionKey = this.authStorage.getSessionKey();
    if (currentSessionKey !== this.lastSessionKey) {
      this.resetState();
      this.lastSessionKey = currentSessionKey;
    }
  }

  async executeTransaction(type: TransactionOperationType, request: any, referenceId?: string): Promise<void> {
    try {
      void referenceId;

      if (!request.idempotencyKey) {
        request.idempotencyKey = uuidv4();
      }

      let response: any = null;
      switch (type) {
        case 'deposit':
          response = await firstValueFrom(this.transactionsService.createDeposit(request as CreateDepositTransactionRequest));
          break;
        case 'withdrawal':
          response = await firstValueFrom(this.transactionsService.createWithdrawal(request as CreateWithdrawalTransactionRequest));
          break;
        case 'transfer':
          response = await firstValueFrom(this.transactionsService.createTransfer(request as CreateTransferTransactionRequest));
          break;
        case 'payment':
          response = await firstValueFrom(this.transactionsService.createPayment(request as CreatePaymentTransactionRequest));
          break;
        case 'fee':
          response = await firstValueFrom(this.transactionsService.createFee(request as CreateFeeTransactionRequest));
          break;
        case 'hold':
          response = await firstValueFrom(this.transactionsService.createHold(request as CreateHoldTransactionRequest));
          break;
        case 'release':
          response = await firstValueFrom(this.transactionsService.createRelease(request as CreateReleaseTransactionRequest));
          break;
        case 'adjustment':
          response = await firstValueFrom(this.transactionsService.createAdjustment(request as CreateAdjustmentTransactionRequest));
          break;
        case 'qr-intent':
          response = await firstValueFrom(this.transactionsService.createQrIntent(request as CreateQrTransactionIntentRequest));
          break;
        case 'qr-confirm':
          throw new Error('La confirmación QR se procesa desde la vista de cliente.');
        default:
          throw new Error(`Operación no soportada: ${type}`);
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

      const response = await firstValueFrom(this.transactionsService.createQrIntent(request));
      if (response.success && response.data) {
        await this.loadTransactions(this.page()?.number ?? 0, this.page()?.size ?? 20);
        return response.data;
      }

      throw new Error(response.message || 'Error al crear la intención QR');
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }

  async reverseTransaction(id: string, reason: string): Promise<void> {
    try {
      const request: CreateReversalTransactionRequest = {
        reason,
        idempotencyKey: uuidv4()
      };
      const response = await firstValueFrom(this.transactionsService.reverseTransaction(id, request));
      if (response.success) {
        await this.loadTransactions(this.page()?.number ?? 0, this.page()?.size ?? 20);
      } else {
        throw new Error(response.message || 'Error al revertir la transacción');
      }
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }

  async refundTransaction(id: string, reason: string, amount: number): Promise<void> {
    try {
      const request: CreateRefundTransactionRequest = {
        reason,
        amount,
        idempotencyKey: uuidv4()
      };
      const response = await firstValueFrom(this.transactionsService.refundTransaction(id, request));
      if (response.success) {
        await this.loadTransactions(this.page()?.number ?? 0, this.page()?.size ?? 20);
      } else {
        throw new Error(response.message || 'Error al reembolsar la transacción');
      }
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }
}

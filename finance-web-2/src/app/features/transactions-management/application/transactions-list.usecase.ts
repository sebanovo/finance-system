import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { v4 as uuidv4 } from 'uuid';
import { 
  TransactionsService, 
  TransactionResponse, 
  CreateDepositTransactionRequest,
  CreateWithdrawalTransactionRequest,
  CreateTransferTransactionRequest,
  CreatePaymentTransactionRequest
} from '../../../entities/transactions';

export interface TransactionsListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: TransactionResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class TransactionsListUseCase {
  private readonly transactionsService = inject(TransactionsService);

  private readonly state = signal<TransactionsListState>({
    status: 'idle',
    data: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadTransactions(): Promise<void> {
    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.transactionsService.listTransactions());

      if (response.success && response.data) {
        this.state.set({ 
          status: 'success', 
          data: response.data, 
          error: null 
        });
      } else {
        this.state.set({ 
          status: 'error', 
          data: [], 
          error: response.message || 'No se pudieron cargar las transacciones' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }

  async executeTransaction(
    type: 'deposit' | 'withdrawal' | 'transfer' | 'payment',
    request: any
  ): Promise<void> {
    try {
      if (!request.idempotencyKey) {
        request.idempotencyKey = uuidv4(); // ✅ Cambiado a uuidv4()
      }

      let response;
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
      }
      
      if (response.success) {
        await this.loadTransactions();
      } else {
        throw new Error(response.message || `Error al ejecutar la transacción de tipo ${type}`);
      }
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }

  async reverseTransaction(id: string, reason: string): Promise<void> {
    try {
      const request = { 
        description: `Reversión: ${reason}`, 
        reason,
        idempotencyKey: uuidv4() // ✅ Cambiado a uuidv4()
      };
      const response = await firstValueFrom(this.transactionsService.reverseTransaction(id, request));
      if (response.success) {
        await this.loadTransactions();
      } else {
        throw new Error(response.message || 'Error al revertir la transacción');
      }
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }

  async refundTransaction(id: string, reason: string, amount: number): Promise<void> {
    try {
      const request = { 
        description: `Reembolso: ${reason}`, 
        reason, 
        amount,
        idempotencyKey: uuidv4() // ✅ Cambiado a uuidv4()
      };
      const response = await firstValueFrom(this.transactionsService.refundTransaction(id, request));
      if (response.success) {
        await this.loadTransactions();
      } else {
        throw new Error(response.message || 'Error al reembolsar la transacción');
      }
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }
}
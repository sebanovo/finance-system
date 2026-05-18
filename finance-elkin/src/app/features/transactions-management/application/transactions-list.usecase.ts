import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { TransactionsService, TransactionResponse } from '../../../entities/transactions';

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
}

import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AccountsService, AccountOwnerResponse } from '../../../entities/accounts';

export interface AccountListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: AccountOwnerResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class AccountListUseCase {
  private readonly accountsService = inject(AccountsService);

  private readonly state = signal<AccountListState>({
    status: 'idle',
    data: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadAccounts(): Promise<void> {
    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.accountsService.listAccounts());

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
          error: response.message || 'No se pudieron cargar las cuentas bancarias' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }

  // Extra methods for account lifecycle (activate, block, freeze) can be added here
}

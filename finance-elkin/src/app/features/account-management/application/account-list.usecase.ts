import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AccountsService, AccountOwnerResponse, CreateAccountRequest, UpdateAccountRequest } from '../../../entities/accounts';

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
  async createAccount(request: CreateAccountRequest): Promise<void> {
    try {
      const response = await firstValueFrom(this.accountsService.createAccount(request));
      if (response.success) {
        await this.loadAccounts(); // reload the list
      } else {
        throw new Error(response.message || 'Error al crear la cuenta');
      }
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }

  async updateAccount(id: string, request: UpdateAccountRequest): Promise<void> {
    try {
      const response = await firstValueFrom(this.accountsService.updateAccount(id, request));
      if (response.success) {
        await this.loadAccounts();
      } else {
        throw new Error(response.message || 'Error al actualizar la cuenta');
      }
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }

  async changeAccountState(
    id: string, 
    action: 'approve' | 'activate' | 'block' | 'freeze' | 'close', 
    reason?: string
  ): Promise<void> {
    try {
      let response;
      switch (action) {
        case 'approve': response = await firstValueFrom(this.accountsService.approveAccount(id)); break;
        case 'activate': response = await firstValueFrom(this.accountsService.activateAccount(id)); break;
        case 'block': response = await firstValueFrom(this.accountsService.blockAccount(id, reason)); break;
        case 'freeze': response = await firstValueFrom(this.accountsService.freezeAccount(id, reason)); break;
        case 'close': response = await firstValueFrom(this.accountsService.closeAccount(id, reason)); break;
      }
      
      if (response.success) {
        await this.loadAccounts(); // Recargar la lista después de cambiar el estado
      } else {
        throw new Error(response.message || 'Error al cambiar el estado de la cuenta');
      }
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }

  async getAccountBalance(id: string) {
    try {
      return await firstValueFrom(this.accountsService.getAccountBalance(id));
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }
}

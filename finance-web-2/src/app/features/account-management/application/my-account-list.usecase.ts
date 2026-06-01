import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AccountsService, AccountOwnerResponse, PageResponse } from '../../../entities/accounts';

export interface MyAccountListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  page: PageResponse<AccountOwnerResponse> | null;
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class MyAccountListUseCase {
  private readonly accountsService = inject(AccountsService);

  private readonly state = signal<MyAccountListState>({
    status: 'idle',
    page: null,
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly page = computed(() => this.state().page);
  readonly data = computed(() => this.state().page?.content ?? []);
  readonly error = computed(() => this.state().error);

  async loadAccounts(page = 0, size = 20): Promise<void> {
    this.state.set({ status: 'loading', page: this.state().page, error: null });

    try {
      const response = await firstValueFrom(this.accountsService.listMyAccounts(page, size));

      if (response.success && response.data) {
        this.state.set({ status: 'success', page: response.data, error: null });
      } else {
        this.state.set({
          status: 'error',
          page: null,
          error: response.message || 'No se pudieron cargar tus cuentas'
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', page: null, error: errorMsg });
    }
  }

  async createAccount(request: { accountName: string; customAlias?: string; accountType: string; currency: string }): Promise<void> {
    try {
      const response = await firstValueFrom(this.accountsService.createMyAccount(request));
      if (response.success) {
        await this.loadAccounts(this.page()?.number ?? 0, this.page()?.size ?? 20);
      } else {
        throw new Error(response.message || 'Error al crear la cuenta');
      }
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }

  async updateAlias(id: string, request: { customAlias?: string }): Promise<void> {
    try {
      const response = await firstValueFrom(this.accountsService.updateMyAccountAlias(id, request));
      if (response.success) {
        await this.loadAccounts(this.page()?.number ?? 0, this.page()?.size ?? 20);
      } else {
        throw new Error(response.message || 'Error al actualizar el alias');
      }
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }

  async getAccountBalance(id: string) {
    try {
      return await firstValueFrom(this.accountsService.getMyAccountBalance(id));
    } catch (err: any) {
      throw new Error(err.error?.message || err.message || 'Error al conectar con el servidor');
    }
  }
}

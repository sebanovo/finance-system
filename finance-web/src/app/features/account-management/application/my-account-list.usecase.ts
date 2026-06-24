import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AccountsService, AccountOwnerResponse, PageResponse } from '../../../entities/accounts';
import { AuthStorageService } from '../../../shared/lib/storage/auth-storage.service';

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
  private readonly authStorage = inject(AuthStorageService);
  private lastSessionKey: string | null = null;

  private readonly state = signal<MyAccountListState>({
    status: 'idle',
    page: null,
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly page = computed(() => this.state().page);
  readonly data = computed(() => this.state().page?.content ?? []);
  readonly error = computed(() => this.state().error);

  needsTenantReload(): boolean {
    return this.authStorage.getSessionKey() !== this.lastSessionKey || this.state().page === null;
  }

  async loadAccounts(page = 0, size = 20): Promise<void> {
    this.syncTenantContext();

    if (!this.authStorage.hasValidTenantSession()) {
      this.resetState();
      return;
    }

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

import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AccountsService, AccountOwnerResponse, CreateAccountRequest, PageResponse, UpdateAccountRequest } from '../../../entities/accounts';
import { AuthStorageService } from '../../../shared/lib/storage/auth-storage.service';

export interface AccountListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  page: PageResponse<AccountOwnerResponse> | null;
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class AccountListUseCase {
  private readonly accountsService = inject(AccountsService);
  private readonly authStorage = inject(AuthStorageService);
  private lastSessionKey: string | null = null;

  private readonly state = signal<AccountListState>({
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
      const response = await firstValueFrom(this.accountsService.listAccounts(page, size));

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
          error: response.message || 'No se pudieron cargar las cuentas bancarias' 
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

  // Extra methods for account lifecycle (activate, block, freeze) can be added here
  async createAccount(request: CreateAccountRequest): Promise<void> {
    try {
      const response = await firstValueFrom(this.accountsService.createAccount(request));
      if (response.success) {
        await this.loadAccounts(this.page()?.number ?? 0, this.page()?.size ?? 20);
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
        await this.loadAccounts(this.page()?.number ?? 0, this.page()?.size ?? 20);
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
        await this.loadAccounts(this.page()?.number ?? 0, this.page()?.size ?? 20);
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

import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import {
  AccountingService,
  AccountingPeriodResponse,
  CloseAccountingPeriodRequest,
  CreateAccountingPeriodRequest,
  PageResponse
} from '../../../entities/accounting';

export interface PeriodListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  page: PageResponse<AccountingPeriodResponse> | null;
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class PeriodListUseCase {
  private readonly accountingService = inject(AccountingService);

  private readonly state = signal<PeriodListState>({
    status: 'idle',
    page: null,
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly page = computed(() => this.state().page);
  readonly data = computed(() => this.state().page?.content ?? []);
  readonly error = computed(() => this.state().error);

  async loadPeriods(page = 0, size = 20): Promise<void> {
    this.state.set({ status: 'loading', page: this.state().page, error: null });

    try {
      const response = await firstValueFrom(this.accountingService.listPeriods(page, size));

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
          error: response.message || 'No se pudieron cargar los períodos' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', page: null, error: errorMsg });
    }
  }

  async createPeriod(request: CreateAccountingPeriodRequest): Promise<boolean> {
    this.state.set({ status: 'loading', page: this.state().page, error: null });

    try {
      const response = await firstValueFrom(this.accountingService.createPeriod(request));

      if (!response.success || !response.data) {
        this.state.set({
          status: 'error',
          page: this.state().page,
          error: response.message || 'No se pudo crear el período contable'
        });
        return false;
      }

      await this.loadPeriods(this.page()?.number ?? 0, this.page()?.size ?? 20);
      return true;
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', page: this.state().page, error: errorMsg });
      return false;
    }
  }

  async closePeriod(id: string, request: CloseAccountingPeriodRequest): Promise<boolean> {
    this.state.set({ status: 'loading', page: this.state().page, error: null });

    try {
      const response = await firstValueFrom(this.accountingService.closePeriod(id, request));

      if (!response.success || !response.data) {
        this.state.set({
          status: 'error',
          page: this.state().page,
          error: response.message || 'No se pudo cerrar el período contable'
        });
        return false;
      }

      await this.loadPeriods(this.page()?.number ?? 0, this.page()?.size ?? 20);
      return true;
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', page: this.state().page, error: errorMsg });
      return false;
    }
  }
}

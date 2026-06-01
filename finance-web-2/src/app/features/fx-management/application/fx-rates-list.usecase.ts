import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { FxService, FxExchangeRateResponse, PageResponse } from '../../../entities/fx';

export interface FxRatesListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  page: PageResponse<FxExchangeRateResponse> | null;
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class FxRatesListUseCase {
  private readonly fxService = inject(FxService);

  private readonly state = signal<FxRatesListState>({
    status: 'idle',
    page: null,
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly page = computed(() => this.state().page);
  readonly data = computed(() => this.state().page?.content ?? []);
  readonly error = computed(() => this.state().error);
  private currentPage = 0;
  private currentSize = 20;

  async loadRates(page = this.currentPage, size = this.currentSize): Promise<void> {
    this.currentPage = page;
    this.currentSize = size;
    this.state.set({ status: 'loading', page: this.state().page, error: null });

    try {
      const response = await firstValueFrom(this.fxService.listRates(page, size));

      if (response.success && response.data) {
        this.state.set({ status: 'success', page: response.data, error: null });
      } else {
        this.state.set({
          status: 'error',
          page: null,
          error: response.message || 'No se pudieron cargar los tipos de cambio'
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', page: null, error: errorMsg });
    }
  }

  async deleteRate(id: string): Promise<boolean> {
    try {
      await firstValueFrom(this.fxService.deleteRate(id));
      await this.loadRates(this.currentPage, this.currentSize);
      return true;
    } catch (err) {
      return false;
    }
  }

  async createRate(request: import('../../../entities/fx').CreateFxExchangeRateRequest): Promise<void> {
    this.state.set({ ...this.state(), status: 'loading', error: null });
    try {
      await firstValueFrom(this.fxService.createRate(request));
      await this.loadRates(this.currentPage, this.currentSize);
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al crear la tasa';
      this.state.set({ ...this.state(), status: 'error', error: errorMsg });
      throw err;
    }
  }

  async updateRate(id: string, request: import('../../../entities/fx').UpdateFxExchangeRateRequest): Promise<void> {
    this.state.set({ ...this.state(), status: 'loading', error: null });
    try {
      await firstValueFrom(this.fxService.updateRate(id, request));
      await this.loadRates(this.currentPage, this.currentSize);
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al actualizar la tasa';
      this.state.set({ ...this.state(), status: 'error', error: errorMsg });
      throw err;
    }
  }
}

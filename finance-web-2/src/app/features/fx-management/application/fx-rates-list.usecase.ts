import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { FxService, FxExchangeRateResponse } from '../../../entities/fx';

export interface FxRatesListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: FxExchangeRateResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class FxRatesListUseCase {
  private readonly fxService = inject(FxService);

  private readonly state = signal<FxRatesListState>({
    status: 'idle',
    data: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadRates(): Promise<void> {
    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.fxService.listRates());

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
          error: response.message || 'No se pudieron cargar los tipos de cambio' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }

  async deleteRate(id: string): Promise<boolean> {
    try {
      await firstValueFrom(this.fxService.deleteRate(id));
      await this.loadRates();
      return true;
    } catch (err) {
      return false;
    }
  }

  async createRate(request: import('../../../entities/fx').CreateFxExchangeRateRequest): Promise<void> {
    this.state.set({ ...this.state(), status: 'loading', error: null });
    try {
      await firstValueFrom(this.fxService.createRate(request));
      await this.loadRates();
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
      await this.loadRates();
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al actualizar la tasa';
      this.state.set({ ...this.state(), status: 'error', error: errorMsg });
      throw err;
    }
  }
}

import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { FxService, OperationFeeResponse } from '../../../entities/fx';

export interface FxFeesListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: OperationFeeResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class FxFeesListUseCase {
  private readonly fxService = inject(FxService);

  private readonly state = signal<FxFeesListState>({
    status: 'idle',
    data: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadFees(): Promise<void> {
    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.fxService.listFees());

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
          error: response.message || 'No se pudieron cargar las comisiones operativas' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }

  async deleteFee(id: string): Promise<boolean> {
    try {
      await firstValueFrom(this.fxService.deleteFee(id));
      await this.loadFees();
      return true;
    } catch (err) {
      return false;
    }
  }

  async createFee(request: import('../../../entities/fx').CreateOperationFeeRequest): Promise<void> {
    this.state.set({ ...this.state(), status: 'loading', error: null });
    try {
      await firstValueFrom(this.fxService.createFee(request));
      await this.loadFees();
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al crear la comisión';
      this.state.set({ ...this.state(), status: 'error', error: errorMsg });
      throw err;
    }
  }

  async updateFee(id: string, request: import('../../../entities/fx').UpdateOperationFeeRequest): Promise<void> {
    this.state.set({ ...this.state(), status: 'loading', error: null });
    try {
      await firstValueFrom(this.fxService.updateFee(id, request));
      await this.loadFees();
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al actualizar la comisión';
      this.state.set({ ...this.state(), status: 'error', error: errorMsg });
      throw err;
    }
  }
}

import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { DashboardService, TenantSummaryResponse } from '../../../entities/dashboard';

export interface SummaryState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: TenantSummaryResponse | null;
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class SummaryUseCase {
  private readonly dashboardService = inject(DashboardService);

  private readonly state = signal<SummaryState>({
    status: 'idle',
    data: null,
    error: null
  });

  // Selectores públicos reactivos
  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadSummary(): Promise<void> {
    this.state.set({ status: 'loading', data: null, error: null });

    try {
      const response = await firstValueFrom(this.dashboardService.getTenantSummary());

      if (response.success && response.data) {
        this.state.set({ status: 'success', data: response.data, error: null });
      } else {
        this.state.set({ 
          status: 'error', 
          data: null, 
          error: response.message || 'No se pudo cargar el resumen' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: null, error: errorMsg });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', data: null, error: null });
  }
}

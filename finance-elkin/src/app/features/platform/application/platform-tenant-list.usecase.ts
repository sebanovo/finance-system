import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { PlatformService, PlatformTenant } from '../../../entities/platform/api/platform.service';

export interface PlatformTenantListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  tenants: PlatformTenant[];
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class PlatformTenantListUseCase {
  private platformService = inject(PlatformService);

  private state = signal<PlatformTenantListState>({
    status: 'idle',
    tenants: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly tenants = computed(() => this.state().tenants);
  readonly error = computed(() => this.state().error);

  async loadTenants(): Promise<void> {
    this.state.set({ status: 'loading', tenants: [], error: null });

    try {
      const response = await firstValueFrom(this.platformService.getTenants());
      if (response.success) {
        this.state.set({ status: 'success', tenants: response.data, error: null });
      } else {
        this.state.set({ status: 'error', tenants: [], error: response.message });
      }
    } catch (err: any) {
      this.state.set({ status: 'error', tenants: [], error: err.message || 'Error al cargar tenants' });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', tenants: [], error: null });
  }
}
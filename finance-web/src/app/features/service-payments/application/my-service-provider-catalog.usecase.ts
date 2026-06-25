import { computed, inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { ServicePaymentsService, ServiceProviderCatalogResponse } from '../../../entities/service-payments';

export interface MyServiceProviderCatalogState {
  status: 'idle' | 'loading' | 'success' | 'error';
  catalog: ServiceProviderCatalogResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class MyServiceProviderCatalogUseCase {
  private readonly servicePaymentsService = inject(ServicePaymentsService);

  private readonly state = signal<MyServiceProviderCatalogState>({
    status: 'idle',
    catalog: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly catalog = computed(() => this.state().catalog);
  readonly error = computed(() => this.state().error);

  async loadCatalog(): Promise<void> {
    this.state.set({ ...this.state(), status: 'loading', error: null });

    try {
      const response = await firstValueFrom(this.servicePaymentsService.listMyServiceProviderCatalog());
      if (response.success && response.data) {
        this.state.set({
          status: 'success',
          catalog: response.data,
          error: null
        });
        return;
      }

      this.state.set({
        status: 'error',
        catalog: [],
        error: response.message || 'No se pudo cargar el catálogo de servicios'
      });
    } catch (err: any) {
      this.state.set({
        status: 'error',
        catalog: [],
        error: this.resolveError(err, 'Error al conectar con el servidor')
      });
    }
  }

  resetState(): void {
    this.state.set({
      status: 'idle',
      catalog: [],
      error: null
    });
  }

  private resolveError(err: any, fallback: string): string {
    return err?.error?.message || err?.message || fallback;
  }
}

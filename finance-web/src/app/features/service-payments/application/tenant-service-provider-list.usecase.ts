import { computed, inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import {
  PageResponse,
  PlatformServiceProviderFilter,
  ServicePaymentsService,
  ServiceProviderResponse
} from '../../../entities/service-payments';

export interface TenantServiceProviderListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  page: PageResponse<ServiceProviderResponse> | null;
  error: string | null;
  filter: PlatformServiceProviderFilter;
}

@Injectable({
  providedIn: 'root'
})
export class TenantServiceProviderListUseCase {
  private readonly servicePaymentsService = inject(ServicePaymentsService);

  private readonly state = signal<TenantServiceProviderListState>({
    status: 'idle',
    page: null,
    error: null,
    filter: {}
  });

  private currentPage = 0;
  private currentSize = 50;

  readonly status = computed(() => this.state().status);
  readonly page = computed(() => this.state().page);
  readonly data = computed(() => this.state().page?.content ?? []);
  readonly error = computed(() => this.state().error);
  readonly filter = computed(() => this.state().filter);

  async loadProviders(
    page = this.currentPage,
    size = this.currentSize,
    filter = this.state().filter
  ): Promise<void> {
    this.currentPage = page;
    this.currentSize = size;

    this.state.set({
      ...this.state(),
      status: 'loading',
      error: null,
      filter
    });

    try {
      const response = await firstValueFrom(
        this.servicePaymentsService.listTenantServiceProviders(page, size, filter)
      );

      if (response.success && response.data) {
        this.state.set({
          status: 'success',
          page: response.data,
          error: null,
          filter
        });
        return;
      }

      this.state.set({
        status: 'error',
        page: null,
        error: response.message || 'No se pudieron cargar los proveedores de servicios',
        filter
      });
    } catch (err: any) {
      this.state.set({
        status: 'error',
        page: null,
        error: this.resolveError(err, 'Error al conectar con el servidor'),
        filter
      });
    }
  }

  async applyFilter(filter: PlatformServiceProviderFilter): Promise<void> {
    await this.loadProviders(0, this.currentSize, filter);
  }

  resetState(): void {
    this.currentPage = 0;
    this.currentSize = 50;
    this.state.set({
      status: 'idle',
      page: null,
      error: null,
      filter: {}
    });
  }

  private resolveError(err: any, fallback: string): string {
    return err?.error?.message || err?.message || fallback;
  }
}

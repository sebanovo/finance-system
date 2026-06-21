import { computed, inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import {
  CreateServiceCustomerRequest,
  PageResponse,
  PlatformServiceCustomerFilter,
  ServiceCustomerResponse,
  ServicePaymentsService,
  UpdateServiceCustomerRequest
} from '../../../entities/service-payments';

export interface PlatformServiceCustomerListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  page: PageResponse<ServiceCustomerResponse> | null;
  error: string | null;
  filter: PlatformServiceCustomerFilter;
}

@Injectable({
  providedIn: 'root'
})
export class PlatformServiceCustomerListUseCase {
  private readonly servicePaymentsService = inject(ServicePaymentsService);

  private readonly state = signal<PlatformServiceCustomerListState>({
    status: 'idle',
    page: null,
    error: null,
    filter: {}
  });

  private currentPage = 0;
  private currentSize = 20;

  readonly status = computed(() => this.state().status);
  readonly page = computed(() => this.state().page);
  readonly data = computed(() => this.state().page?.content ?? []);
  readonly error = computed(() => this.state().error);
  readonly filter = computed(() => this.state().filter);

  async loadCustomers(
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
        this.servicePaymentsService.listPlatformServiceCustomers(page, size, filter)
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
        error: response.message || 'No se pudieron cargar los clientes de servicio',
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

  async applyFilter(filter: PlatformServiceCustomerFilter): Promise<void> {
    await this.loadCustomers(0, this.currentSize, filter);
  }

  async clearFilter(): Promise<void> {
    await this.loadCustomers(0, this.currentSize, {});
  }

  async createCustomer(request: CreateServiceCustomerRequest): Promise<ServiceCustomerResponse> {
    try {
      const response = await firstValueFrom(
        this.servicePaymentsService.createPlatformServiceCustomer(request)
      );

      if (!response.success || !response.data) {
        throw new Error(response.message || 'No se pudo crear el cliente de servicio');
      }

      await this.loadCustomers(this.currentPage, this.currentSize, this.state().filter);
      return response.data;
    } catch (err: any) {
      throw new Error(this.resolveError(err, 'Error al crear el cliente de servicio'));
    }
  }

  async updateCustomer(id: string, request: UpdateServiceCustomerRequest): Promise<ServiceCustomerResponse> {
    try {
      const response = await firstValueFrom(this.servicePaymentsService.updatePlatformServiceCustomer(id, request));

      if (!response.success || !response.data) {
        throw new Error(response.message || 'No se pudo actualizar el cliente de servicio');
      }

      await this.loadCustomers(this.currentPage, this.currentSize, this.state().filter);
      return response.data;
    } catch (err: any) {
      throw new Error(this.resolveError(err, 'Error al actualizar el cliente de servicio'));
    }
  }

  resetState(): void {
    this.currentPage = 0;
    this.currentSize = 20;
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

import { computed, inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import {
  CancelServiceBillRequest,
  CreateServiceBillRequest,
  PageResponse,
  PlatformServiceBillFilter,
  ServiceBillResponse,
  ServicePaymentsService
} from '../../../entities/service-payments';

export interface PlatformServiceBillListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  page: PageResponse<ServiceBillResponse> | null;
  error: string | null;
  filter: PlatformServiceBillFilter;
}

@Injectable({
  providedIn: 'root'
})
export class PlatformServiceBillListUseCase {
  private readonly servicePaymentsService = inject(ServicePaymentsService);

  private readonly state = signal<PlatformServiceBillListState>({
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

  async loadBills(
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
        this.servicePaymentsService.listPlatformServiceBills(page, size, filter)
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
        error: response.message || 'No se pudieron cargar las deudas de servicios',
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

  async applyFilter(filter: PlatformServiceBillFilter): Promise<void> {
    await this.loadBills(0, this.currentSize, filter);
  }

  async clearFilter(): Promise<void> {
    await this.loadBills(0, this.currentSize, {});
  }

  async createBill(request: CreateServiceBillRequest): Promise<ServiceBillResponse> {
    try {
      const response = await firstValueFrom(this.servicePaymentsService.createPlatformServiceBill(request));

      if (!response.success || !response.data) {
        throw new Error(response.message || 'No se pudo crear la deuda de servicio');
      }

      await this.loadBills(this.currentPage, this.currentSize, this.state().filter);
      return response.data;
    } catch (err: any) {
      throw new Error(this.resolveError(err, 'Error al crear la deuda de servicio'));
    }
  }

  async cancelBill(id: string, request: CancelServiceBillRequest): Promise<ServiceBillResponse> {
    try {
      const response = await firstValueFrom(this.servicePaymentsService.cancelPlatformServiceBill(id, request));

      if (!response.success || !response.data) {
        throw new Error(response.message || 'No se pudo cancelar la deuda de servicio');
      }

      await this.loadBills(this.currentPage, this.currentSize, this.state().filter);
      return response.data;
    } catch (err: any) {
      throw new Error(this.resolveError(err, 'Error al cancelar la deuda de servicio'));
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

import { computed, inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import {
  PageResponse,
  ServicePaymentResponse,
  ServicePaymentsService,
  TenantServicePaymentFilter
} from '../../../entities/service-payments';

export interface TenantServicePaymentHistoryState {
  status: 'idle' | 'loading' | 'success' | 'error';
  detailStatus: 'idle' | 'loading' | 'success' | 'error';
  page: PageResponse<ServicePaymentResponse> | null;
  selectedPayment: ServicePaymentResponse | null;
  error: string | null;
  detailError: string | null;
  filter: TenantServicePaymentFilter;
}

@Injectable({
  providedIn: 'root'
})
export class TenantServicePaymentHistoryUseCase {
  private readonly servicePaymentsService = inject(ServicePaymentsService);

  private readonly state = signal<TenantServicePaymentHistoryState>({
    status: 'idle',
    detailStatus: 'idle',
    page: null,
    selectedPayment: null,
    error: null,
    detailError: null,
    filter: {}
  });

  private currentPage = 0;
  private currentSize = 20;

  readonly status = computed(() => this.state().status);
  readonly detailStatus = computed(() => this.state().detailStatus);
  readonly page = computed(() => this.state().page);
  readonly data = computed(() => this.state().page?.content ?? []);
  readonly selectedPayment = computed(() => this.state().selectedPayment);
  readonly error = computed(() => this.state().error);
  readonly detailError = computed(() => this.state().detailError);
  readonly filter = computed(() => this.state().filter);

  async loadPayments(
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
      const response = await firstValueFrom(this.servicePaymentsService.listTenantServicePayments(page, size, filter));

      if (response.success && response.data) {
        this.state.set({
          ...this.state(),
          status: 'success',
          page: response.data,
          error: null,
          filter
        });
        return;
      }

      this.state.set({
        ...this.state(),
        status: 'error',
        page: null,
        error: response.message || 'No se pudo cargar el historial de pagos del tenant',
        filter
      });
    } catch (err: any) {
      this.state.set({
        ...this.state(),
        status: 'error',
        page: null,
        error: this.resolveError(err, 'Error al conectar con el servidor'),
        filter
      });
    }
  }

  async applyFilter(filter: TenantServicePaymentFilter): Promise<void> {
    await this.loadPayments(0, this.currentSize, filter);
  }

  async clearFilter(): Promise<void> {
    await this.loadPayments(0, this.currentSize, {});
  }

  async loadPaymentDetail(id: string): Promise<void> {
    this.state.set({
      ...this.state(),
      detailStatus: 'loading',
      detailError: null,
      selectedPayment: null
    });

    try {
      const response = await firstValueFrom(this.servicePaymentsService.getTenantServicePayment(id));

      if (response.success && response.data) {
        this.state.set({
          ...this.state(),
          detailStatus: 'success',
          selectedPayment: response.data,
          detailError: null
        });
        return;
      }

      this.state.set({
        ...this.state(),
        detailStatus: 'error',
        selectedPayment: null,
        detailError: response.message || 'No se pudo cargar el detalle del pago'
      });
    } catch (err: any) {
      this.state.set({
        ...this.state(),
        detailStatus: 'error',
        selectedPayment: null,
        detailError: this.resolveError(err, 'Error al cargar el detalle del pago')
      });
    }
  }

  clearSelectedPayment(): void {
    this.state.set({
      ...this.state(),
      selectedPayment: null,
      detailStatus: 'idle',
      detailError: null
    });
  }

  resetState(): void {
    this.currentPage = 0;
    this.currentSize = 20;
    this.state.set({
      status: 'idle',
      detailStatus: 'idle',
      page: null,
      selectedPayment: null,
      error: null,
      detailError: null,
      filter: {}
    });
  }

  private resolveError(err: any, fallback: string): string {
    return err?.error?.message || err?.message || fallback;
  }
}

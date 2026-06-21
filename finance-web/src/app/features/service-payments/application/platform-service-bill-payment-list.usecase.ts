import { computed, inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import {
  PageResponse,
  PlatformServiceBillPaymentFilter,
  ServiceBillPaymentResponse,
  ServicePaymentsService
} from '../../../entities/service-payments';

export interface PlatformServiceBillPaymentListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  page: PageResponse<ServiceBillPaymentResponse> | null;
  selectedPayment: ServiceBillPaymentResponse | null;
  detailStatus: 'idle' | 'loading' | 'success' | 'error';
  error: string | null;
  detailError: string | null;
  filter: PlatformServiceBillPaymentFilter;
}

@Injectable({
  providedIn: 'root'
})
export class PlatformServiceBillPaymentListUseCase {
  private readonly servicePaymentsService = inject(ServicePaymentsService);

  private readonly state = signal<PlatformServiceBillPaymentListState>({
    status: 'idle',
    page: null,
    selectedPayment: null,
    detailStatus: 'idle',
    error: null,
    detailError: null,
    filter: {}
  });

  private currentPage = 0;
  private currentSize = 20;

  readonly status = computed(() => this.state().status);
  readonly page = computed(() => this.state().page);
  readonly data = computed(() => this.state().page?.content ?? []);
  readonly selectedPayment = computed(() => this.state().selectedPayment);
  readonly detailStatus = computed(() => this.state().detailStatus);
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
      const response = await firstValueFrom(this.servicePaymentsService.listPlatformServiceBillPayments(page, size, filter));

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
        error: response.message || 'No se pudieron cargar los pagos globales de servicios',
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

  async applyFilter(filter: PlatformServiceBillPaymentFilter): Promise<void> {
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
      const response = await firstValueFrom(this.servicePaymentsService.getPlatformServiceBillPayment(id));

      if (response.success && response.data) {
        this.state.set({
          ...this.state(),
          detailStatus: 'success',
          detailError: null,
          selectedPayment: response.data
        });
        return;
      }

      this.state.set({
        ...this.state(),
        detailStatus: 'error',
        detailError: response.message || 'No se pudo cargar el detalle del pago global',
        selectedPayment: null
      });
    } catch (err: any) {
      this.state.set({
        ...this.state(),
        detailStatus: 'error',
        detailError: this.resolveError(err, 'Error al cargar el detalle del pago'),
        selectedPayment: null
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
      page: null,
      selectedPayment: null,
      detailStatus: 'idle',
      error: null,
      detailError: null,
      filter: {}
    });
  }

  private resolveError(err: any, fallback: string): string {
    return err?.error?.message || err?.message || fallback;
  }
}

import { computed, inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import {
  QueryServiceBillsRequest,
  QueryServiceBillsResponse,
  ServiceBillQueryItemResponse,
  ServicePaymentsService
} from '../../../entities/service-payments';

export interface TenantServiceBillQueryState {
  status: 'idle' | 'loading' | 'success' | 'error';
  result: QueryServiceBillsResponse | null;
  selectedBill: ServiceBillQueryItemResponse | null;
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class TenantServiceBillQueryUseCase {
  private readonly servicePaymentsService = inject(ServicePaymentsService);

  private readonly state = signal<TenantServiceBillQueryState>({
    status: 'idle',
    result: null,
    selectedBill: null,
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly result = computed(() => this.state().result);
  readonly bills = computed(() => this.state().result?.bills ?? []);
  readonly selectedBill = computed(() => this.state().selectedBill);
  readonly error = computed(() => this.state().error);
  readonly hasBills = computed(() => (this.state().result?.bills?.length ?? 0) > 0);

  async queryBills(request: QueryServiceBillsRequest): Promise<QueryServiceBillsResponse> {
    this.state.set({
      status: 'loading',
      result: null,
      selectedBill: null,
      error: null
    });

    try {
      const response = await firstValueFrom(this.servicePaymentsService.queryTenantServiceBills(request));

      if (!response.success || !response.data) {
        const message = response.message || 'No se pudieron consultar las deudas del servicio';
        this.state.set({
          status: 'error',
          result: null,
          selectedBill: null,
          error: message
        });
        throw new Error(message);
      }

      this.state.set({
        status: 'success',
        result: response.data,
        selectedBill: response.data.bills[0] ?? null,
        error: null
      });

      return response.data;
    } catch (err: any) {
      const message = this.resolveError(err, 'Error al consultar las deudas del servicio');
      this.state.set({
        status: 'error',
        result: null,
        selectedBill: null,
        error: message
      });
      throw new Error(message);
    }
  }

  selectBill(bill: ServiceBillQueryItemResponse | null): void {
    this.state.set({
      ...this.state(),
      selectedBill: bill
    });
  }

  clearResult(): void {
    this.state.set({
      status: 'idle',
      result: null,
      selectedBill: null,
      error: null
    });
  }

  private resolveError(err: any, fallback: string): string {
    return err?.error?.message || err?.message || fallback;
  }
}

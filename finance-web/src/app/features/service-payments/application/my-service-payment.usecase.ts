import { computed, inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import {
  CreateMyServicePaymentRequest,
  ServicePaymentResponse,
  ServicePaymentsService
} from '../../../entities/service-payments';

export interface MyServicePaymentState {
  status: 'idle' | 'loading' | 'success' | 'error';
  payment: ServicePaymentResponse | null;
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class MyServicePaymentUseCase {
  private readonly servicePaymentsService = inject(ServicePaymentsService);

  private readonly state = signal<MyServicePaymentState>({
    status: 'idle',
    payment: null,
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly payment = computed(() => this.state().payment);
  readonly error = computed(() => this.state().error);

  async createPayment(
    request: Omit<CreateMyServicePaymentRequest, 'idempotencyKey'> & { idempotencyKey?: string }
  ): Promise<ServicePaymentResponse> {
    const requestWithKey: CreateMyServicePaymentRequest = {
      ...request,
      idempotencyKey: request.idempotencyKey || this.generateIdempotencyKey()
    };

    this.state.set({
      status: 'loading',
      payment: null,
      error: null
    });

    try {
      const response = await firstValueFrom(this.servicePaymentsService.createMyServicePayment(requestWithKey));

      if (!response.success || !response.data) {
        const message = response.message || 'No se pudo completar el pago del servicio';
        this.state.set({
          status: 'error',
          payment: null,
          error: message
        });
        throw new Error(message);
      }

      this.state.set({
        status: 'success',
        payment: response.data,
        error: null
      });

      return response.data;
    } catch (err: any) {
      const message = this.resolveError(err, 'Error al pagar el servicio');
      this.state.set({
        status: 'error',
        payment: null,
        error: message
      });
      throw new Error(message);
    }
  }

  clearPayment(): void {
    this.state.set({
      status: 'idle',
      payment: null,
      error: null
    });
  }

  generateIdempotencyKey(): string {
    const random = Math.random().toString(36).slice(2, 10);
    return `svc-pay-${Date.now()}-${random}`;
  }

  private resolveError(err: any, fallback: string): string {
    return err?.error?.message || err?.message || fallback;
  }
}

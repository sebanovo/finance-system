import { computed, inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import {
  CreateServiceEnrollmentRequest,
  MyServiceEnrollmentFilter,
  PageResponse,
  ServiceEnrollmentResponse,
  ServicePaymentsService
} from '../../../entities/service-payments';

export interface MyServiceEnrollmentListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  page: PageResponse<ServiceEnrollmentResponse> | null;
  error: string | null;
  filter: MyServiceEnrollmentFilter;
}

@Injectable({
  providedIn: 'root'
})
export class MyServiceEnrollmentListUseCase {
  private readonly servicePaymentsService = inject(ServicePaymentsService);

  private readonly state = signal<MyServiceEnrollmentListState>({
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

  async loadEnrollments(
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
        this.servicePaymentsService.listMyServiceEnrollments(page, size, filter)
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
        error: response.message || 'No se pudieron cargar tus servicios afiliados',
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

  async applyFilter(filter: MyServiceEnrollmentFilter): Promise<void> {
    await this.loadEnrollments(0, this.currentSize, filter);
  }

  async clearFilter(): Promise<void> {
    await this.loadEnrollments(0, this.currentSize, {});
  }

  async createEnrollment(request: CreateServiceEnrollmentRequest): Promise<ServiceEnrollmentResponse> {
    try {
      const response = await firstValueFrom(this.servicePaymentsService.createMyServiceEnrollment(request));

      if (!response.success || !response.data) {
        throw new Error(response.message || 'No se pudo afiliar el servicio');
      }

      await this.loadEnrollments(this.currentPage, this.currentSize, this.state().filter);
      return response.data;
    } catch (err: any) {
      throw new Error(this.resolveError(err, 'Error al afiliar el servicio'));
    }
  }

  async deleteEnrollment(id: string): Promise<ServiceEnrollmentResponse> {
    try {
      const response = await firstValueFrom(this.servicePaymentsService.deleteMyServiceEnrollment(id));

      if (!response.success || !response.data) {
        throw new Error(response.message || 'No se pudo eliminar la afiliación');
      }

      await this.loadEnrollments(this.currentPage, this.currentSize, this.state().filter);
      return response.data;
    } catch (err: any) {
      throw new Error(this.resolveError(err, 'Error al eliminar la afiliación'));
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

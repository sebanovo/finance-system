import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import {
  PlatformServiceBillPaymentListUseCase,
  PlatformServiceBillPaymentTableComponent,
  PlatformServiceProviderListUseCase,
  ServicePaymentDetailComponent
} from '../../features/service-payments';
import { PlatformPaginationComponent } from '../../features/platform/ui/platform-pagination/platform-pagination.component';
import { ServiceBillPaymentResponse } from '../../entities/service-payments';
import { ToastService } from '../../shared/ui/toast/toast.service';

@Component({
  selector: 'app-platform-service-bill-payments-page',
  standalone: true,
  imports: [
    CommonModule,
    LucideAngularModule,
    PlatformPaginationComponent,
    PlatformServiceBillPaymentTableComponent,
    ServicePaymentDetailComponent
  ],
  template: `
    <section class="space-y-6">
      <div class="rounded-[28px] border border-[#C8E6C9] bg-white p-6 shadow-sm">
        <div class="flex items-start gap-4">
          <div class="rounded-2xl bg-[#F1F8E9] p-3 text-[#2E7D32]">
            <lucide-icon name="dollar-sign" class="h-6 w-6"></lucide-icon>
          </div>
          <div>
            <h1 class="text-2xl font-black tracking-tight text-[#1B5E20]">Pagos globales de servicios</h1>
            <p class="mt-1 text-sm text-[#6B7D6C]">
              Consulta todos los pagos de servicios realizados desde cualquier tenant.
            </p>
          </div>
        </div>
      </div>

      <section class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 shadow-sm sm:p-6">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <h2 class="text-lg font-bold text-[#1B5E20]">Filtros</h2>
            <p class="text-sm text-[#6B7D6C]">Busca por proveedor, tenant, recibo, cuenta o fechas.</p>
          </div>

          <div class="grid w-full gap-3 md:grid-cols-2 xl:grid-cols-5">
            <input
              type="text"
              [value]="receiptNumber()"
              (input)="receiptNumber.set($any($event.target).value)"
              placeholder="Recibo"
              class="h-11 w-full min-w-0 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none placeholder:text-[#9AA99A] focus:border-[#2E7D32] focus:bg-white" />

            <input
              type="text"
              [value]="tenantSlug()"
              (input)="tenantSlug.set($any($event.target).value)"
              placeholder="Tenant"
              class="h-11 w-full min-w-0 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none placeholder:text-[#9AA99A] focus:border-[#2E7D32] focus:bg-white" />

            <input
              type="text"
              [value]="accountNumber()"
              (input)="accountNumber.set($any($event.target).value)"
              placeholder="Cuenta"
              class="h-11 w-full min-w-0 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none placeholder:text-[#9AA99A] focus:border-[#2E7D32] focus:bg-white" />

            <select
              [value]="providerId()"
              (change)="providerId.set($any($event.target).value)"
              class="h-11 w-full min-w-0 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none focus:border-[#2E7D32] focus:bg-white">
              <option value="">Todos los proveedores</option>
              <option *ngFor="let provider of providerUseCase.data()" [value]="provider.id">
                {{ provider.name }}
              </option>
            </select>

            <button
              type="button"
              (click)="applyFilters()"
              class="cursor-pointer rounded-full bg-[#2E7D32] px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#256428] md:col-span-2 xl:col-span-1">
              Aplicar
            </button>
          </div>
        </div>
      </section>

      <app-platform-service-bill-payment-table
        [payments]="useCase.data()"
        (detail)="openDetail($event)">
      </app-platform-service-bill-payment-table>

      @if (useCase.page(); as page) {
        <app-platform-pagination
          [currentPage]="page.number"
          [totalPages]="page.totalPages"
          [totalElements]="page.totalElements"
          [isLoading]="useCase.status() === 'loading'"
          (pageChange)="changePage($event)">
        </app-platform-pagination>
      }

      <app-service-payment-detail
        [isOpen]="detailOpen()"
        [payment]="useCase.selectedPayment()"
        (close)="closeDetail()">
      </app-service-payment-detail>
    </section>
  `
})
export class PlatformServiceBillPaymentsPageComponent implements OnInit {
  readonly useCase = inject(PlatformServiceBillPaymentListUseCase);
  readonly providerUseCase = inject(PlatformServiceProviderListUseCase);
  private readonly toast = inject(ToastService);

  readonly detailOpen = signal(false);

  readonly providerId = signal('');
  readonly tenantSlug = signal('');
  readonly accountNumber = signal('');
  readonly receiptNumber = signal('');

  ngOnInit(): void {
    void this.providerUseCase.loadProviders(0, 200, {});
    void this.useCase.loadPayments();
  }

  changePage(page: number): void {
    void this.useCase.loadPayments(page);
  }

  applyFilters(): void {
    void this.useCase.applyFilter({
      providerId: this.providerId() || undefined,
      tenantSlug: this.tenantSlug().trim() || undefined,
      accountNumber: this.accountNumber().trim() || undefined,
      receiptNumber: this.receiptNumber().trim() || undefined
    });
  }

  async openDetail(payment: ServiceBillPaymentResponse): Promise<void> {
    this.detailOpen.set(true);
    try {
      await this.useCase.loadPaymentDetail(payment.id);
    } catch (error: any) {
      this.toast.error(error.message || 'No se pudo cargar el detalle');
    }
  }

  closeDetail(): void {
    this.detailOpen.set(false);
    this.useCase.clearSelectedPayment();
  }
}

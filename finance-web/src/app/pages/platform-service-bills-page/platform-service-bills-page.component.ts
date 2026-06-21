import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import {
  PlatformServiceBillFormComponent,
  PlatformServiceBillListUseCase,
  PlatformServiceBillTableComponent,
  PlatformServiceProviderListUseCase
} from '../../features/service-payments';
import { PlatformPaginationComponent } from '../../features/platform/ui/platform-pagination/platform-pagination.component';
import { ServiceBillResponse } from '../../entities/service-payments';
import { ToastService } from '../../shared/ui/toast/toast.service';

@Component({
  selector: 'app-platform-service-bills-page',
  standalone: true,
  imports: [
    CommonModule,
    LucideAngularModule,
    PlatformPaginationComponent,
    PlatformServiceBillFormComponent,
    PlatformServiceBillTableComponent
  ],
  template: `
    <section class="space-y-6">
      <div class="rounded-[28px] border border-[#C8E6C9] bg-white p-6 shadow-sm">
        <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div class="flex items-start gap-4">
            <div class="rounded-2xl bg-[#F1F8E9] p-3 text-[#2E7D32]">
              <lucide-icon name="file-text" class="h-6 w-6"></lucide-icon>
            </div>
            <div>
              <h1 class="text-2xl font-black tracking-tight text-[#1B5E20]">Deudas de servicios</h1>
              <p class="mt-1 text-sm text-[#6B7D6C]">
                Genera deudas de prueba para que clientes puedan consultarlas y pagarlas.
              </p>
            </div>
          </div>

          <button
            type="button"
            (click)="openCreate()"
            class="inline-flex cursor-pointer items-center justify-center gap-2 rounded-full bg-[#2E7D32] px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#256428]">
            <lucide-icon name="plus" class="h-4 w-4"></lucide-icon>
            Nueva deuda
          </button>
        </div>
      </div>

      <section class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 shadow-sm sm:p-6">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <h2 class="text-lg font-bold text-[#1B5E20]">Filtros</h2>
            <p class="text-sm text-[#6B7D6C]">Filtra deudas por proveedor, código, periodo o estado.</p>
          </div>

          <div class="grid w-full gap-3 md:grid-cols-2 xl:grid-cols-5">
            <input
              type="text"
              [value]="serviceCustomerCode()"
              (input)="serviceCustomerCode.set($any($event.target).value)"
              placeholder="Código"
              class="h-11 w-full min-w-0 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none placeholder:text-[#9AA99A] focus:border-[#2E7D32] focus:bg-white" />

            <input
              type="month"
              [value]="billingPeriod()"
              (input)="billingPeriod.set($any($event.target).value)"
              class="h-11 w-full min-w-0 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none focus:border-[#2E7D32] focus:bg-white" />

            <select
              [value]="providerId()"
              (change)="providerId.set($any($event.target).value)"
              class="h-11 w-full min-w-0 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none focus:border-[#2E7D32] focus:bg-white">
              <option value="">Todos los proveedores</option>
              <option *ngFor="let provider of providerUseCase.data()" [value]="provider.id">
                {{ provider.name }}
              </option>
            </select>

            <select
              [value]="status()"
              (change)="status.set($any($event.target).value)"
              class="h-11 w-full min-w-0 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none focus:border-[#2E7D32] focus:bg-white">
              <option value="">Todos</option>
              <option value="PENDING">Pendientes</option>
              <option value="PAID">Pagadas</option>
              <option value="CANCELLED">Canceladas</option>
              <option value="EXPIRED">Vencidas</option>
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

      <app-platform-service-bill-table
        [bills]="useCase.data()"
        (cancel)="confirmCancel($event)">
      </app-platform-service-bill-table>

      @if (useCase.page(); as page) {
        <app-platform-pagination
          [currentPage]="page.number"
          [totalPages]="page.totalPages"
          [totalElements]="page.totalElements"
          [isLoading]="useCase.status() === 'loading'"
          (pageChange)="changePage($event)">
        </app-platform-pagination>
      }

      <app-platform-service-bill-form
        [isOpen]="formOpen()"
        [providers]="providerUseCase.data()"
        [isSubmitting]="submitting()"
        (closed)="closeForm()"
        (saved)="createBill($event)">
      </app-platform-service-bill-form>
    </section>
  `
})
export class PlatformServiceBillsPageComponent implements OnInit {
  readonly useCase = inject(PlatformServiceBillListUseCase);
  readonly providerUseCase = inject(PlatformServiceProviderListUseCase);
  private readonly toast = inject(ToastService);

  readonly formOpen = signal(false);
  readonly submitting = signal(false);

  readonly providerId = signal('');
  readonly status = signal('');
  readonly serviceCustomerCode = signal('');
  readonly billingPeriod = signal('');

  ngOnInit(): void {
    void this.providerUseCase.loadProviders(0, 200, { status: 'ACTIVE' });
    void this.useCase.loadBills();
  }

  changePage(page: number): void {
    void this.useCase.loadBills(page);
  }

  applyFilters(): void {
    void this.useCase.applyFilter({
      providerId: this.providerId() || undefined,
      status: this.status() || undefined,
      serviceCustomerCode: this.serviceCustomerCode().trim() || undefined,
      billingPeriod: this.billingPeriod() || undefined
    });
  }

  openCreate(): void {
    this.formOpen.set(true);
  }

  closeForm(): void {
    this.formOpen.set(false);
    this.submitting.set(false);
  }

  async createBill(request: any): Promise<void> {
    this.submitting.set(true);
    try {
      await this.useCase.createBill(request);
      this.toast.success('Deuda creada correctamente');
      this.closeForm();
    } catch (error: any) {
      this.toast.error(error.message || 'No se pudo crear la deuda');
      this.submitting.set(false);
    }
  }

  async confirmCancel(bill: ServiceBillResponse): Promise<void> {
    const confirmed = window.confirm(`¿Cancelar la deuda ${bill.billingPeriod} de ${bill.customerName}?`);
    if (!confirmed) return;

    try {
      await this.useCase.cancelBill(bill.id, { reason: 'Cancelado desde plataforma' });
      this.toast.success('Deuda cancelada correctamente');
    } catch (error: any) {
      this.toast.error(error.message || 'No se pudo cancelar la deuda');
    }
  }
}

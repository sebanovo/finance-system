import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import {
  PlatformServiceCustomerFormComponent,
  PlatformServiceCustomerListUseCase,
  PlatformServiceCustomerTableComponent,
  PlatformServiceProviderCatalogUseCase,
  PlatformServiceProviderListUseCase
} from '../../features/service-payments';
import { PlatformPaginationComponent } from '../../features/platform/ui/platform-pagination/platform-pagination.component';
import { ServiceCustomerResponse } from '../../entities/service-payments';
import { ToastService } from '../../shared/ui/toast/toast.service';

@Component({
  selector: 'app-platform-service-customers-page',
  standalone: true,
  imports: [
    CommonModule,
    LucideAngularModule,
    PlatformPaginationComponent,
    PlatformServiceCustomerFormComponent,
    PlatformServiceCustomerTableComponent
  ],
  template: `
    <section class="space-y-6">
      <div class="rounded-[28px] border border-[#C8E6C9] bg-white p-6 shadow-sm">
        <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div class="flex items-start gap-4">
            <div class="rounded-2xl bg-[#F1F8E9] p-3 text-[#2E7D32]">
              <lucide-icon name="users" class="h-6 w-6"></lucide-icon>
            </div>
            <div>
              <h1 class="text-2xl font-black tracking-tight text-[#1B5E20]">Clientes de servicio</h1>
              <p class="mt-1 text-sm text-[#6B7D6C]">
                Códigos válidos por proveedor para generar y consultar deudas.
              </p>
            </div>
          </div>

          <button
            type="button"
            (click)="openCreate()"
            class="inline-flex cursor-pointer items-center justify-center gap-2 rounded-full bg-[#2E7D32] px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#256428]">
            <lucide-icon name="plus" class="h-4 w-4"></lucide-icon>
            Nuevo código
          </button>
        </div>
      </div>

      <section class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 shadow-sm sm:p-6">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <h2 class="text-lg font-bold text-[#1B5E20]">Filtros</h2>
            <p class="text-sm text-[#6B7D6C]">Busca por proveedor, código, titular o estado.</p>
          </div>

          <div class="grid w-full gap-3 md:grid-cols-2 xl:grid-cols-5">
            <input
              type="text"
              [value]="search()"
              (input)="search.set($any($event.target).value)"
              placeholder="Buscar titular"
              class="h-11 w-full min-w-0 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none placeholder:text-[#9AA99A] focus:border-[#2E7D32] focus:bg-white" />

            <input
              type="text"
              [value]="serviceCustomerCode()"
              (input)="serviceCustomerCode.set($any($event.target).value)"
              placeholder="Código"
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

            <select
              [value]="status()"
              (change)="status.set($any($event.target).value)"
              class="h-11 w-full min-w-0 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none focus:border-[#2E7D32] focus:bg-white">
              <option value="">Todos</option>
              <option value="ACTIVE">Activos</option>
              <option value="INACTIVE">Inactivos</option>
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

      <app-platform-service-customer-table
        [customers]="useCase.data()"
        (edit)="openEdit($event)">
      </app-platform-service-customer-table>

      @if (useCase.page(); as page) {
        <app-platform-pagination
          [currentPage]="page.number"
          [totalPages]="page.totalPages"
          [totalElements]="page.totalElements"
          [isLoading]="useCase.status() === 'loading'"
          (pageChange)="changePage($event)">
        </app-platform-pagination>
      }

      <app-platform-service-customer-form
        [isOpen]="formOpen()"
        [customer]="selectedCustomer()"
        [providers]="providerUseCase.data()"
        [serviceCustomerCodesByProvider]="serviceCustomerCodesByProvider"
        [isSubmitting]="submitting()"
        (closed)="closeForm()"
        (saved)="saveCustomer($event)">
      </app-platform-service-customer-form>
    </section>
  `
})
export class PlatformServiceCustomersPageComponent implements OnInit {
  readonly useCase = inject(PlatformServiceCustomerListUseCase);
  readonly providerUseCase = inject(PlatformServiceProviderListUseCase);
  readonly providerCatalogUseCase = inject(PlatformServiceProviderCatalogUseCase);
  private readonly toast = inject(ToastService);

  readonly formOpen = signal(false);
  readonly submitting = signal(false);
  readonly selectedCustomer = signal<ServiceCustomerResponse | null>(null);

  readonly providerId = signal('');
  readonly status = signal('');
  readonly search = signal('');
  readonly serviceCustomerCode = signal('');

  ngOnInit(): void {
    void this.providerUseCase.loadProviders(0, 200, { status: 'ACTIVE' });
    void this.providerCatalogUseCase.loadCatalog();
    void this.useCase.loadCustomers();
  }

  get serviceCustomerCodesByProvider(): Record<string, string[]> {
    return this.providerCatalogUseCase.catalog().reduce<Record<string, string[]>>((acc, provider) => {
      acc[provider.id] = provider.serviceCustomers.map(item => item.serviceCustomerCode);
      return acc;
    }, {});
  }

  changePage(page: number): void {
    void this.useCase.loadCustomers(page);
  }

  applyFilters(): void {
    void this.useCase.applyFilter({
      providerId: this.providerId() || undefined,
      status: this.status() || undefined,
      serviceCustomerCode: this.serviceCustomerCode().trim() || undefined,
      search: this.search().trim() || undefined
    });
  }

  openCreate(): void {
    this.selectedCustomer.set(null);
    this.formOpen.set(true);
  }

  openEdit(customer: ServiceCustomerResponse): void {
    this.selectedCustomer.set(customer);
    this.formOpen.set(true);
  }

  closeForm(): void {
    this.formOpen.set(false);
    this.selectedCustomer.set(null);
    this.submitting.set(false);
  }

  async saveCustomer(event: any): Promise<void> {
    this.submitting.set(true);
    try {
      if (event.isEditing && event.id) {
        await this.useCase.updateCustomer(event.id, event.request);
        this.toast.success('Cliente de servicio actualizado');
      } else {
        await this.useCase.createCustomer(event.request);
        this.toast.success('Cliente de servicio creado');
      }
      this.closeForm();
    } catch (error: any) {
      this.toast.error(error.message || 'No se pudo guardar el cliente de servicio');
      this.submitting.set(false);
    }
  }
}

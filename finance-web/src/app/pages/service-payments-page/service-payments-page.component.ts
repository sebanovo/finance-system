import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { AccountListUseCase } from '../../features/account-management';
import { PlatformPaginationComponent } from '../../features/platform/ui/platform-pagination/platform-pagination.component';
import {
  ServicePaymentDetailComponent,
  TenantBankServicePaymentUseCase,
  TenantBankServicePaymentDrawerComponent,
  TenantServiceBillQueryUseCase,
  TenantServicePaymentHistoryUseCase,
  TenantServicePaymentTableComponent,
  TenantServiceProviderListUseCase,
  TenantServiceProviderCatalogUseCase,
  ServicePaymentsErrorCardComponent,
  ServicePaymentsLoadingCardComponent,
  ServicePaymentsStateCardComponent,
  resolveServicePaymentError
} from '../../features/service-payments';
import { QueryServiceBillsRequest, ServicePaymentResponse } from '../../entities/service-payments';
import { ToastService } from '../../shared/ui/toast/toast.service';

@Component({
  selector: 'app-service-payments-page',
  standalone: true,
  imports: [
    CommonModule,
    LucideAngularModule,
    PlatformPaginationComponent,
    TenantBankServicePaymentDrawerComponent,
    TenantServicePaymentTableComponent,
    ServicePaymentDetailComponent,
    ServicePaymentsLoadingCardComponent,
    ServicePaymentsErrorCardComponent,
    ServicePaymentsStateCardComponent
  ],
  template: `
    <section class="space-y-6">
      <div class="rounded-[28px] border border-[#C8E6C9] bg-white p-6 shadow-sm">
        <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div class="flex items-start gap-4">
            <div class="rounded-2xl bg-[#F1F8E9] p-3 text-[#2E7D32]">
              <lucide-icon name="dollar-sign" class="h-6 w-6"></lucide-icon>
            </div>
            <div>
              <h1 class="text-2xl font-black tracking-tight text-[#1B5E20]">Pagos de servicios</h1>
            <p class="mt-1 text-sm text-[#6B7D6C]">
              Ventanilla para pagos de caja del tenant.
            </p>
            </div>
          </div>

          <button
            type="button"
            (click)="openPaymentDrawer()"
            class="inline-flex cursor-pointer items-center justify-center gap-2 rounded-full bg-[#2E7D32] px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#256428]">
            <lucide-icon name="plus" class="h-4 w-4"></lucide-icon>
            Nuevo pago
          </button>
        </div>
      </div>

      <section class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 shadow-sm sm:p-6">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <h2 class="text-lg font-bold text-[#1B5E20]">Filtros</h2>
            <p class="text-sm text-[#6B7D6C]">Busca por proveedor, cliente, cuenta, recibo o fechas.</p>
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
              [value]="payerUserId()"
              (input)="payerUserId.set($any($event.target).value)"
              placeholder="ID del pagador"
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

      @if (historyUseCase.status() === 'loading') {
        <app-service-payments-loading-card message="Cargando pagos de servicios..."></app-service-payments-loading-card>
      } @else if (historyUseCase.status() === 'error') {
        <app-service-payments-error-card
          title="No se pudieron cargar los pagos"
          [message]="historyUseCase.error() || 'Intenta nuevamente.'"
          (retry)="reloadHistory()">
        </app-service-payments-error-card>
      } @else if (historyUseCase.data().length === 0) {
        <app-service-payments-state-card
          icon="dollar-sign"
          title="Sin pagos registrados"
          message="Los pagos asistidos y los pagos por app aparecerán aquí cuando el tenant comience a operar."
          actionLabel="Nuevo pago"
          (action)="openPaymentDrawer()">
        </app-service-payments-state-card>
      } @else {
        <app-tenant-service-payment-table
          [payments]="historyUseCase.data()"
          (detail)="openDetail($event)">
        </app-tenant-service-payment-table>
      }

      @if (historyUseCase.page(); as page) {
        <app-platform-pagination
          [currentPage]="page.number"
          [totalPages]="page.totalPages"
          [totalElements]="page.totalElements"
          [isLoading]="historyUseCase.status() === 'loading'"
          (pageChange)="changePage($event)">
        </app-platform-pagination>
      }

      <app-tenant-bank-service-payment-drawer
        [isOpen]="paymentDrawerOpen()"
        [providers]="providerUseCase.data()"
        [accounts]="accountUseCase.data()"
        [serviceCustomerCodesByProvider]="serviceCustomerCodesByProvider"
        [queryResult]="billQueryUseCase.result()"
        [queryLoading]="billQueryUseCase.status() === 'loading'"
        [paymentLoading]="paymentUseCase.status() === 'loading'"
        [payment]="paymentUseCase.payment()"
        (closed)="closePaymentDrawer()"
        (queryDebt)="queryDebt($event)"
        (pay)="payService($event)"
        (paymentClosed)="afterPaymentClosed()">
      </app-tenant-bank-service-payment-drawer>

      <app-service-payment-detail
        [isOpen]="detailOpen()"
        [payment]="historyUseCase.selectedPayment()"
        (close)="closeDetail()">
      </app-service-payment-detail>
    </section>
  `
})
export class ServicePaymentsPageComponent implements OnInit {
  readonly providerUseCase = inject(TenantServiceProviderListUseCase);
  readonly providerCatalogUseCase = inject(TenantServiceProviderCatalogUseCase);
  readonly accountUseCase = inject(AccountListUseCase);
  readonly historyUseCase = inject(TenantServicePaymentHistoryUseCase);
  readonly billQueryUseCase = inject(TenantServiceBillQueryUseCase);
  readonly paymentUseCase = inject(TenantBankServicePaymentUseCase);

  private readonly toast = inject(ToastService);

  readonly paymentDrawerOpen = signal(false);
  readonly detailOpen = signal(false);

  readonly providerId = signal('');
  readonly payerUserId = signal('');
  readonly accountNumber = signal('');
  readonly receiptNumber = signal('');

  ngOnInit(): void {
    void this.providerUseCase.loadProviders(0, 100, {});
    void this.providerCatalogUseCase.loadCatalog();
    void this.accountUseCase.loadAccounts(0, 100);
    void this.historyUseCase.loadPayments();
  }

  get serviceCustomerCodesByProvider(): Record<string, string[]> {
    const grouped: Record<string, Set<string>> = {};

    for (const provider of this.providerCatalogUseCase.catalog()) {
      if (!grouped[provider.id]) {
        grouped[provider.id] = new Set<string>();
      }

      for (const serviceCustomer of provider.serviceCustomers ?? []) {
        const code = serviceCustomer.serviceCustomerCode?.trim();
        if (code) {
          grouped[provider.id].add(code);
        }
      }
    }

    return Object.fromEntries(
      Object.entries(grouped).map(([providerId, codes]) => [providerId, Array.from(codes).sort()])
    );
  }

  reloadHistory(): void {
    void this.historyUseCase.loadPayments(
      this.historyUseCase.page()?.number ?? 0,
      this.historyUseCase.page()?.size ?? 20,
      this.historyUseCase.filter()
    );
  }

  changePage(page: number): void {
    void this.historyUseCase.loadPayments(page);
  }

  applyFilters(): void {
    void this.historyUseCase.applyFilter({
      providerId: this.providerId() || undefined,
      payerUserId: this.payerUserId().trim() || undefined,
      accountNumber: this.accountNumber().trim() || undefined,
      receiptNumber: this.receiptNumber().trim() || undefined
    });
  }

  openPaymentDrawer(): void {
    this.billQueryUseCase.clearResult();
    this.paymentUseCase.clearPayment();
    this.paymentDrawerOpen.set(true);
  }

  closePaymentDrawer(): void {
    this.paymentDrawerOpen.set(false);
    this.billQueryUseCase.clearResult();
    this.paymentUseCase.clearPayment();
  }

  async queryDebt(request: QueryServiceBillsRequest): Promise<void> {
    try {
      await this.billQueryUseCase.queryBills(request);
      if (!this.billQueryUseCase.hasBills()) {
        this.toast.info('No hay deudas pendientes para este servicio');
      }
    } catch (error: any) {
      this.toast.error(resolveServicePaymentError(error, 'No se pudo consultar la deuda'));
    }
  }

  async payService(request: {
    sourceAccountNumber: string;
    providerId: string;
    serviceCustomerCode: string;
    billId: string;
  }): Promise<void> {
    try {
      await this.paymentUseCase.createPayment(request);
      this.toast.success('Pago de caja completado');
      await this.historyUseCase.loadPayments(
        this.historyUseCase.page()?.number ?? 0,
        this.historyUseCase.page()?.size ?? 20
      );
    } catch (error: any) {
      this.toast.error(resolveServicePaymentError(error, 'No se pudo completar el pago de caja'));
    }
  }

  afterPaymentClosed(): void {
    this.closePaymentDrawer();
    void this.historyUseCase.loadPayments();
  }

  async openDetail(payment: ServicePaymentResponse): Promise<void> {
    this.detailOpen.set(true);
    try {
      await this.historyUseCase.loadPaymentDetail(payment.paymentId);
    } catch (error: any) {
      this.toast.error(resolveServicePaymentError(error, 'No se pudo cargar el comprobante'));
    }
  }

  closeDetail(): void {
    this.detailOpen.set(false);
    this.historyUseCase.clearSelectedPayment();
  }
}

import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { MyAccountListUseCase } from '../../features/account-management';
import { PlatformPaginationComponent } from '../../features/platform/ui/platform-pagination/platform-pagination.component';
import {
  MyServicePaymentDrawerComponent,
  MyServiceBillQueryUseCase,
  MyServiceEnrollmentCardsComponent,
  MyServiceEnrollmentFormComponent,
  MyServiceEnrollmentListUseCase,
  MyServicePaymentUseCase,
  MyServiceProviderListUseCase,
  ServicePaymentsConfirmDialogComponent,
  ServicePaymentsErrorCardComponent,
  ServicePaymentsLoadingCardComponent,
  ServicePaymentsStateCardComponent,
  resolveServicePaymentError
} from '../../features/service-payments';
import { QueryServiceBillsRequest, ServiceEnrollmentResponse } from '../../entities/service-payments';
import { ToastService } from '../../shared/ui/toast/toast.service';

@Component({
  selector: 'app-my-service-enrollments-page',
  standalone: true,
  imports: [
    CommonModule,
    LucideAngularModule,
    PlatformPaginationComponent,
    MyServiceEnrollmentFormComponent,
    MyServiceEnrollmentCardsComponent,
    MyServicePaymentDrawerComponent,
    ServicePaymentsLoadingCardComponent,
    ServicePaymentsErrorCardComponent,
    ServicePaymentsStateCardComponent,
    ServicePaymentsConfirmDialogComponent
  ],
  template: `
    <section class="space-y-6">
      <div class="rounded-[28px] border border-[#C8E6C9] bg-white p-6 shadow-sm">
        <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div class="flex items-start gap-4">
            <div class="rounded-2xl bg-[#F1F8E9] p-3 text-[#2E7D32]">
              <lucide-icon name="clipboard-list" class="h-6 w-6"></lucide-icon>
            </div>
            <div>
              <h1 class="text-2xl font-black tracking-tight text-[#1B5E20]">Servicios afiliados</h1>
              <p class="mt-1 text-sm text-[#6B7D6C]">
                Guarda tus servicios frecuentes para consultar y pagar más rápido.
              </p>
            </div>
          </div>

          <div class="flex flex-wrap gap-3">
            <button
              type="button"
              (click)="openManualPayment()"
              class="inline-flex cursor-pointer items-center justify-center gap-2 rounded-full border border-[#DDEED8] bg-white px-5 py-2.5 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
              <lucide-icon name="dollar-sign" class="h-4 w-4"></lucide-icon>
              Pagar servicio
            </button>

            <button
              type="button"
              (click)="openEnrollmentForm()"
              class="inline-flex cursor-pointer items-center justify-center gap-2 rounded-full bg-[#2E7D32] px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#256428]">
              <lucide-icon name="plus" class="h-4 w-4"></lucide-icon>
              Afiliar servicio
            </button>
          </div>
        </div>
      </div>

      <section class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 shadow-sm sm:p-6">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <h2 class="text-lg font-bold text-[#1B5E20]">Filtros</h2>
            <p class="text-sm text-[#6B7D6C]">Filtra tus servicios por proveedor, categoría, estado o alias.</p>
          </div>

          <div class="grid w-full gap-3 md:grid-cols-2 xl:grid-cols-4">
            <input
              type="text"
              [value]="search()"
              (input)="search.set($any($event.target).value)"
              placeholder="Buscar servicio"
              class="h-11 w-full min-w-0 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none placeholder:text-[#9AA99A] focus:border-[#2E7D32] focus:bg-white" />

            <select
              [value]="category()"
              (change)="category.set($any($event.target).value)"
              class="h-11 w-full min-w-0 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none focus:border-[#2E7D32] focus:bg-white">
              <option value="">Todas las categorías</option>
              <option value="ELECTRICITY">Luz</option>
              <option value="WATER">Agua</option>
              <option value="INTERNET">Internet</option>
              <option value="TV_CABLE">Cable TV</option>
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

      @if (enrollmentUseCase.status() === 'loading') {
        <app-service-payments-loading-card message="Cargando servicios afiliados..."></app-service-payments-loading-card>
      } @else if (enrollmentUseCase.status() === 'error') {
        <app-service-payments-error-card
          title="No se pudieron cargar tus servicios"
          [message]="enrollmentUseCase.error() || 'Intenta nuevamente.'"
          (retry)="reloadEnrollments()">
        </app-service-payments-error-card>
      } @else if (enrollmentUseCase.data().length === 0) {
        <app-service-payments-state-card
          icon="clipboard-list"
          title="Sin servicios afiliados"
          message="Aún no tienes servicios guardados para consultar o pagar más rápido."
          actionLabel="Afiliar servicio"
          (action)="openEnrollmentForm()">
        </app-service-payments-state-card>
      } @else {
        <app-my-service-enrollment-cards
          [enrollments]="enrollmentUseCase.data()"
          (viewDebts)="openDebtViewer($event)"
          (pay)="openPaymentDrawer($event)"
          (delete)="deleteEnrollment($event)">
        </app-my-service-enrollment-cards>
      }

      @if (enrollmentUseCase.page(); as page) {
        <app-platform-pagination
          [currentPage]="page.number"
          [totalPages]="page.totalPages"
          [totalElements]="page.totalElements"
          [isLoading]="enrollmentUseCase.status() === 'loading'"
          (pageChange)="changePage($event)">
        </app-platform-pagination>
      }

      <app-my-service-enrollment-form
        [isOpen]="enrollmentFormOpen()"
        [providers]="providerUseCase.data()"
        [isSubmitting]="submittingEnrollment()"
        (closed)="closeEnrollmentForm()"
        (saved)="createEnrollment($event)">
      </app-my-service-enrollment-form>

      <app-my-service-payment-drawer
        [isOpen]="paymentDrawerOpen()"
        [mode]="paymentDrawerMode()"
        [initialStep]="paymentDrawerInitialStep()"
        [enrollment]="selectedEnrollment()"
        [providers]="providerUseCase.data()"
        [accounts]="accountUseCase.data()"
        [queryResult]="billQueryUseCase.result()"
        [queryLoading]="billQueryUseCase.status() === 'loading'"
        [paymentLoading]="paymentUseCase.status() === 'loading'"
        [payment]="paymentUseCase.payment()"
        (closed)="closePaymentDrawer()"
        (queryDebt)="queryDebt($event)"
        (pay)="payService($event)"
        (paymentClosed)="afterPaymentClosed()">
      </app-my-service-payment-drawer>

      <app-service-payments-confirm-dialog
        [isOpen]="deleteConfirmOpen()"
        title="Eliminar afiliación"
        [message]="deleteConfirmMessage"
        confirmLabel="Eliminar"
        (confirm)="confirmDeleteEnrollment()"
        (cancel)="cancelDeleteEnrollment()">
      </app-service-payments-confirm-dialog>
    </section>
  `
})
export class MyServiceEnrollmentsPageComponent implements OnInit {
  readonly enrollmentUseCase = inject(MyServiceEnrollmentListUseCase);
  readonly providerUseCase = inject(MyServiceProviderListUseCase);
  readonly billQueryUseCase = inject(MyServiceBillQueryUseCase);
  readonly paymentUseCase = inject(MyServicePaymentUseCase);
  readonly accountUseCase = inject(MyAccountListUseCase);

  private readonly toast = inject(ToastService);

  readonly enrollmentFormOpen = signal(false);
  readonly paymentDrawerOpen = signal(false);
  readonly paymentDrawerMode = signal<'payment' | 'debt-only'>('payment');
  readonly paymentDrawerInitialStep = signal(1);
  readonly deleteConfirmOpen = signal(false);
  readonly submittingEnrollment = signal(false);
  readonly selectedEnrollment = signal<ServiceEnrollmentResponse | null>(null);
  readonly enrollmentToDelete = signal<ServiceEnrollmentResponse | null>(null);

  readonly search = signal('');
  readonly category = signal('');
  readonly status = signal('ACTIVE');

  ngOnInit(): void {
    void this.providerUseCase.loadProviders(0, 100, { status: 'ACTIVE' });
    void this.accountUseCase.loadAccounts(0, 100);
    void this.enrollmentUseCase.loadEnrollments(0, 20, { status: 'ACTIVE' });
  }

  reloadEnrollments(): void {
    void this.enrollmentUseCase.loadEnrollments(
      this.enrollmentUseCase.page()?.number ?? 0,
      this.enrollmentUseCase.page()?.size ?? 20,
      this.enrollmentUseCase.filter()
    );
  }

  changePage(page: number): void {
    void this.enrollmentUseCase.loadEnrollments(page);
  }

  applyFilters(): void {
    void this.enrollmentUseCase.applyFilter({
      search: this.search().trim() || undefined,
      category: this.category() || undefined,
      status: this.status() || undefined
    });
  }

  openEnrollmentForm(): void {
    this.enrollmentFormOpen.set(true);
  }

  openManualPayment(): void {
    this.selectedEnrollment.set(null);
    this.paymentDrawerMode.set('payment');
    this.paymentDrawerInitialStep.set(1);
    this.billQueryUseCase.clearResult();
    this.paymentUseCase.clearPayment();
    this.paymentDrawerOpen.set(true);
  }

  closeEnrollmentForm(): void {
    this.enrollmentFormOpen.set(false);
    this.submittingEnrollment.set(false);
  }

  async createEnrollment(request: any): Promise<void> {
    this.submittingEnrollment.set(true);
    try {
      await this.enrollmentUseCase.createEnrollment(request);
      this.toast.success('Servicio afiliado correctamente');
      this.closeEnrollmentForm();
    } catch (error: any) {
      this.toast.error(resolveServicePaymentError(error, 'No se pudo afiliar el servicio'));
      this.submittingEnrollment.set(false);
    }
  }

  async deleteEnrollment(enrollment: ServiceEnrollmentResponse): Promise<void> {
    this.enrollmentToDelete.set(enrollment);
    this.deleteConfirmOpen.set(true);
  }

  get deleteConfirmMessage(): string {
    const enrollment = this.enrollmentToDelete();
    return enrollment
      ? `¿Eliminar la afiliación ${enrollment.alias || enrollment.provider.name}?`
      : '¿Eliminar esta afiliación?';
  }

  cancelDeleteEnrollment(): void {
    this.deleteConfirmOpen.set(false);
    this.enrollmentToDelete.set(null);
  }

  async confirmDeleteEnrollment(): Promise<void> {
    const enrollment = this.enrollmentToDelete();
    if (!enrollment) {
      return;
    }

    try {
      await this.enrollmentUseCase.deleteEnrollment(enrollment.id);
      this.toast.success('Afiliación eliminada correctamente');
      this.cancelDeleteEnrollment();
    } catch (error: any) {
      this.toast.error(resolveServicePaymentError(error, 'No se pudo eliminar la afiliación'));
    }
  }

  async openDebtViewer(enrollment: ServiceEnrollmentResponse): Promise<void> {
    await this.openEnrollmentBills(enrollment, 'debt-only');
  }

  async openPaymentDrawer(enrollment: ServiceEnrollmentResponse): Promise<void> {
    await this.openEnrollmentBills(enrollment, 'payment');
  }

  closePaymentDrawer(): void {
    this.paymentDrawerOpen.set(false);
    this.selectedEnrollment.set(null);
    this.paymentDrawerMode.set('payment');
    this.paymentDrawerInitialStep.set(1);
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

  async payService(request: any): Promise<void> {
    try {
      await this.paymentUseCase.createPayment(request);
      this.toast.success('Pago de servicio completado');
      await this.enrollmentUseCase.loadEnrollments(
        this.enrollmentUseCase.page()?.number ?? 0,
        this.enrollmentUseCase.page()?.size ?? 20
      );
    } catch (error: any) {
      this.toast.error(resolveServicePaymentError(error, 'No se pudo completar el pago'));
    }
  }

  afterPaymentClosed(): void {
    this.closePaymentDrawer();
  }

  private async openEnrollmentBills(
    enrollment: ServiceEnrollmentResponse,
    mode: 'payment' | 'debt-only'
  ): Promise<void> {
    this.selectedEnrollment.set(enrollment);
    this.paymentDrawerMode.set(mode);
    this.paymentDrawerInitialStep.set(1);
    this.billQueryUseCase.clearResult();
    this.paymentUseCase.clearPayment();

    try {
      await this.billQueryUseCase.queryBills({
        providerId: enrollment.provider.id,
        serviceCustomerCode: enrollment.serviceCustomerCode.trim()
      });

      if (mode === 'payment' && !this.billQueryUseCase.hasBills()) {
        this.toast.info('No hay deudas pendientes para este servicio');
      }

      this.paymentDrawerOpen.set(true);
    } catch (error: any) {
      this.toast.error(resolveServicePaymentError(error, 'No se pudo consultar la deuda'));
      this.selectedEnrollment.set(null);
      this.paymentDrawerMode.set('payment');
      this.paymentDrawerInitialStep.set(1);
    }
  }
}

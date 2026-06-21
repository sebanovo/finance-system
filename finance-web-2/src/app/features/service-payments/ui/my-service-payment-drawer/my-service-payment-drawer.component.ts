import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';
import { AccountOwnerResponse } from '../../../../entities/accounts';
import {
  QueryServiceBillsRequest,
  QueryServiceBillsResponse,
  ServiceBillQueryItemResponse,
  ServiceEnrollmentResponse,
  ServicePaymentResponse,
  ServiceProviderResponse
} from '../../../../entities/service-payments';
import { ServicePaymentDetailComponent } from '../service-payment-detail/service-payment-detail.component';

@Component({
  selector: 'app-my-service-payment-drawer',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule, ServicePaymentDetailComponent],
  template: `
    <div *ngIf="isOpen" class="fixed inset-0 z-50 flex items-center justify-end">
      <div class="absolute inset-0 bg-black/20 backdrop-blur-sm" (click)="close()"></div>

      <div class="relative flex h-full w-full max-w-xl flex-col overflow-hidden border-l border-[#C8E6C9] bg-white shadow-2xl">
        <div class="border-b border-[#E8F2E2] p-6">
          <div class="flex items-start justify-between gap-4">
            <div>
              <h2 class="text-2xl font-black tracking-tight text-[#1B5E20]">
                {{ mode === 'debt-only' ? 'Deudas del servicio' : 'Pagar servicio' }}
              </h2>
              <p class="mt-1 text-sm text-[#6B7D6C]">
                {{
                  mode === 'debt-only'
                    ? 'Revisa las deudas pendientes sin iniciar el pago.'
                    : 'Consulta deuda, selecciona cuenta y confirma el pago.'
                }}
              </p>
            </div>

            <button
              type="button"
              (click)="close()"
              class="cursor-pointer rounded-full border border-[#DDEED8] p-2 text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
              <lucide-icon name="x" [size]="20"></lucide-icon>
            </button>
          </div>

          @if (mode !== 'debt-only') {
            <div class="mt-6 grid grid-cols-2 gap-2">
              <div class="h-2 rounded-full" [ngClass]="step() >= 1 ? 'bg-[#2E7D32]' : 'bg-[#E8F2E2]'"></div>
              <div class="h-2 rounded-full" [ngClass]="step() >= 2 ? 'bg-[#2E7D32]' : 'bg-[#E8F2E2]'"></div>
            </div>
          }
        </div>

        <div class="flex-1 overflow-y-auto p-6">
          @if (mode === 'debt-only' || (mode === 'payment' && enrollment && step() === 1)) {
            <section class="space-y-5">
              <div class="rounded-[24px] border border-[#C8E6C9] bg-[#FAFCF8] p-4">
                <h3 class="text-lg font-bold text-[#1B5E20]">
                  {{ mode === 'debt-only' ? 'Deudas pendientes' : '1. Deudas pendientes' }}
                </h3>
                <p class="mt-1 text-sm text-[#6B7D6C]">
                  {{
                    mode === 'debt-only'
                      ? 'Listado de deudas registradas para este servicio.'
                      : 'Selecciona la deuda que quieres pagar.'
                  }}
                </p>
              </div>

              @if (queryResult && queryResult.bills.length > 0) {
                <div class="space-y-3">
                  @for (bill of queryResult.bills; track bill.billId) {
                    <button
                      type="button"
                      (click)="selectBill(bill)"
                      class="w-full cursor-pointer rounded-[20px] border p-4 text-left transition-colors"
                      [ngClass]="mode === 'debt-only'
                        ? 'border-[#DDEED8] bg-white'
                        : selectedBill()?.billId === bill.billId
                          ? 'border-[#2E7D32] bg-[#F1F8E9]'
                          : 'border-[#DDEED8] bg-white hover:bg-[#FAFCF8]'">
                      <div class="flex items-start justify-between gap-4">
                        <div>
                          <p class="text-sm font-bold text-[#1B5E20]">Periodo {{ bill.billingPeriod }}</p>
                          <p class="mt-1 text-xs text-[#6B7D6C]">Vence: {{ bill.dueDate | date:'mediumDate' }}</p>
                        </div>
                        <p class="text-lg font-black text-[#1B5E20]">
                          {{ bill.amount | number:'1.2-2' }} {{ bill.currency }}
                        </p>
                      </div>
                    </button>
                  }
                </div>
              } @else {
                <div class="rounded-[24px] border border-dashed border-[#C8E6C9] bg-[#FAFCF8] p-8 text-center">
                  <p class="text-sm font-semibold text-[#1B5E20]">No hay deudas pendientes</p>
                  <p class="mt-1 text-sm text-[#6B7D6C]">Este servicio no tiene deudas disponibles para pagar.</p>
                </div>
              }
            </section>
          }

          @if (mode === 'payment' && !enrollment && step() === 1) {
            <section class="space-y-5">
              <div class="rounded-[24px] border border-[#C8E6C9] bg-[#FAFCF8] p-4">
                <h3 class="text-lg font-bold text-[#1B5E20]">1. Servicio</h3>
                <p class="mt-1 text-sm text-[#6B7D6C]">Ingresa proveedor y código para consultar la deuda.</p>
              </div>

              <form [formGroup]="queryForm" class="space-y-5">
                <div class="space-y-2">
                  <label class="text-sm font-semibold text-[#567157]">Proveedor</label>
                  <select
                    formControlName="providerId"
                    class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none focus:border-[#2E7D32] focus:bg-white">
                    <option value="" disabled>Selecciona proveedor</option>
                    @for (provider of providers; track provider.id) {
                      <option [value]="provider.id">
                        {{ provider.name }}
                      </option>
                    }
                  </select>
                </div>

                <div class="space-y-2">
                  <label class="text-sm font-semibold text-[#567157]">Código de servicio</label>
                  <input
                    type="text"
                    formControlName="serviceCustomerCode"
                    placeholder="Ej. 100001"
                    class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none placeholder:text-[#9AA99A] focus:border-[#2E7D32] focus:bg-white" />
                </div>
              </form>

              <button
                type="button"
                (click)="emitQuery()"
                [disabled]="queryLoading || queryForm.invalid"
                class="inline-flex w-full cursor-pointer items-center justify-center gap-2 rounded-full bg-[#2E7D32] px-5 py-3 text-sm font-semibold text-white transition-colors hover:bg-[#256428] disabled:cursor-not-allowed disabled:opacity-50">
                <lucide-icon *ngIf="!queryLoading" name="search" [size]="16"></lucide-icon>
                {{ queryLoading ? 'Consultando...' : 'Consultar' }}
              </button>
            </section>
          }

          @if (mode !== 'debt-only' && ((enrollment && step() === 2) || (!enrollment && step() === 2))) {
            <section class="space-y-5">
              <div class="rounded-[24px] border border-[#C8E6C9] bg-[#FAFCF8] p-4">
                <h3 class="text-lg font-bold text-[#1B5E20]">
                  {{ enrollment ? '2. Pago' : '2. Deudas pendientes' }}
                </h3>
                <p class="mt-1 text-sm text-[#6B7D6C]">
                  {{ enrollment ? 'Selecciona la cuenta y confirma el pago.' : 'Selecciona la deuda que quieres pagar.' }}
                </p>
              </div>

              @if (!enrollment) {
                <div class="space-y-3">
                  @for (bill of queryResult?.bills ?? []; track bill.billId) {
                    <button
                      type="button"
                      (click)="selectBill(bill)"
                      class="w-full cursor-pointer rounded-[20px] border p-4 text-left transition-colors"
                      [ngClass]="selectedBill()?.billId === bill.billId ? 'border-[#2E7D32] bg-[#F1F8E9]' : 'border-[#DDEED8] bg-white hover:bg-[#FAFCF8]'">
                      <div class="flex items-start justify-between gap-4">
                        <div>
                          <p class="text-sm font-bold text-[#1B5E20]">Periodo {{ bill.billingPeriod }}</p>
                          <p class="mt-1 text-xs text-[#6B7D6C]">Vence: {{ bill.dueDate | date:'mediumDate' }}</p>
                        </div>
                        <p class="text-lg font-black text-[#1B5E20]">
                          {{ bill.amount | number:'1.2-2' }} {{ bill.currency }}
                        </p>
                      </div>
                    </button>
                  }
                </div>
              } @else {
                <div class="space-y-2">
                  <label class="text-sm font-semibold text-[#567157]">Cuenta origen</label>
                  <select
                    [value]="selectedAccount()?.accountNumber || ''"
                    (change)="selectAccountByNumber($any($event.target).value)"
                    class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none focus:border-[#2E7D32] focus:bg-white">
                    <option value="" disabled>Selecciona una cuenta</option>
                    @for (account of accounts; track account.accountNumber) {
                      <option [value]="account.accountNumber">
                        {{ account.displayName || account.accountNameLabel }} - {{ account.accountNumber }} ({{ account.currency }})
                      </option>
                    }
                  </select>
                </div>

                <div class="space-y-3 rounded-[24px] border border-[#C8E6C9] bg-white p-5 shadow-sm">
                  @if (selectedAccount(); as account) {
                    <div class="space-y-3 text-sm">
                      <div class="flex justify-between gap-4">
                        <span class="text-[#6B7D6C]">Cuenta</span>
                        <span class="font-bold text-[#1B5E20]">{{ account.displayName || account.accountNameLabel }}</span>
                      </div>
                      <div class="flex justify-between gap-4">
                        <span class="text-[#6B7D6C]">Disponible</span>
                        <span class="font-bold text-[#1B5E20]">
                          {{ account.availableBalance | number:'1.2-2' }} {{ account.currency }}
                        </span>
                      </div>
                    </div>
                  } @else {
                    <div class="rounded-2xl border border-dashed border-[#C8E6C9] bg-[#FAFCF8] px-4 py-3 text-sm text-[#6B7D6C]">
                      Selecciona una cuenta para ver sus datos antes de confirmar el pago.
                    </div>
                  }
                </div>

                <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-5 shadow-sm">
                  <div class="space-y-3 text-sm">
                    <div class="flex justify-between gap-4">
                      <span class="text-[#6B7D6C]">Proveedor</span>
                      <span class="font-bold text-[#1B5E20]">{{ queryResult?.provider?.name }}</span>
                    </div>

                    <div class="flex justify-between gap-4">
                      <span class="text-[#6B7D6C]">Código</span>
                      <span class="font-bold text-[#1B5E20]">{{ queryResult?.serviceCustomerCode }}</span>
                    </div>

                    <div class="flex justify-between gap-4">
                      <span class="text-[#6B7D6C]">Periodo</span>
                      <span class="font-bold text-[#1B5E20]">{{ selectedBill()?.billingPeriod }}</span>
                    </div>

                    <div class="flex justify-between gap-4">
                      <span class="text-[#6B7D6C]">Cuenta</span>
                      <span class="font-bold text-[#1B5E20]">{{ selectedAccount()?.accountNumber }}</span>
                    </div>

                    <div class="border-t border-[#E8F2E2] pt-3">
                      <div class="flex justify-between gap-4">
                        <span class="text-[#6B7D6C]">Total</span>
                        <span class="text-xl font-black text-[#1B5E20]">
                          {{ selectedBill()?.amount | number:'1.2-2' }} {{ selectedBill()?.currency }}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              }
            </section>
          }

          @if (mode === 'payment' && !enrollment && step() === 3) {
            <section class="space-y-5">
              <div class="rounded-[24px] border border-[#C8E6C9] bg-[#FAFCF8] p-4">
                <h3 class="text-lg font-bold text-[#1B5E20]">3. Pago</h3>
                <p class="mt-1 text-sm text-[#6B7D6C]">Selecciona la cuenta y confirma el pago.</p>
              </div>

              <div class="space-y-2">
                <label class="text-sm font-semibold text-[#567157]">Cuenta origen</label>
                <select
                  [value]="selectedAccount()?.accountNumber || ''"
                  (change)="selectAccountByNumber($any($event.target).value)"
                  class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none focus:border-[#2E7D32] focus:bg-white">
                  <option value="" disabled>Selecciona una cuenta</option>
                  @for (account of accounts; track account.accountNumber) {
                    <option [value]="account.accountNumber">
                      {{ account.displayName || account.accountNameLabel }} - {{ account.accountNumber }} ({{ account.currency }})
                    </option>
                  }
                </select>
              </div>

              <div class="space-y-3 rounded-[24px] border border-[#C8E6C9] bg-white p-5 shadow-sm">
                @if (selectedAccount(); as account) {
                  <div class="space-y-3 text-sm">
                    <div class="flex justify-between gap-4">
                      <span class="text-[#6B7D6C]">Cuenta</span>
                      <span class="font-bold text-[#1B5E20]">{{ account.displayName || account.accountNameLabel }}</span>
                    </div>
                    <div class="flex justify-between gap-4">
                      <span class="text-[#6B7D6C]">Disponible</span>
                      <span class="font-bold text-[#1B5E20]">
                        {{ account.availableBalance | number:'1.2-2' }} {{ account.currency }}
                      </span>
                    </div>
                  </div>
                } @else {
                  <div class="rounded-2xl border border-dashed border-[#C8E6C9] bg-[#FAFCF8] px-4 py-3 text-sm text-[#6B7D6C]">
                    Selecciona una cuenta para ver sus datos antes de confirmar el pago.
                  </div>
                }
              </div>

              <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-5 shadow-sm">
                <div class="space-y-3 text-sm">
                  <div class="flex justify-between gap-4">
                    <span class="text-[#6B7D6C]">Proveedor</span>
                    <span class="font-bold text-[#1B5E20]">{{ queryResult?.provider?.name }}</span>
                  </div>

                  <div class="flex justify-between gap-4">
                    <span class="text-[#6B7D6C]">Código</span>
                    <span class="font-bold text-[#1B5E20]">{{ queryResult?.serviceCustomerCode }}</span>
                  </div>

                  <div class="flex justify-between gap-4">
                    <span class="text-[#6B7D6C]">Periodo</span>
                    <span class="font-bold text-[#1B5E20]">{{ selectedBill()?.billingPeriod }}</span>
                  </div>

                  <div class="flex justify-between gap-4">
                    <span class="text-[#6B7D6C]">Cuenta</span>
                    <span class="font-bold text-[#1B5E20]">{{ selectedAccount()?.accountNumber }}</span>
                  </div>

                  <div class="border-t border-[#E8F2E2] pt-3">
                    <div class="flex justify-between gap-4">
                      <span class="text-[#6B7D6C]">Total</span>
                      <span class="text-xl font-black text-[#1B5E20]">
                        {{ selectedBill()?.amount | number:'1.2-2' }} {{ selectedBill()?.currency }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </section>
          }

          @if (payment) {
            <section class="space-y-5">
              <app-service-payment-detail
                [isOpen]="true"
                [payment]="payment"
                (close)="paymentClosed.emit()">
              </app-service-payment-detail>
            </section>
          }
        </div>

        <div class="border-t border-[#E8F2E2] p-5">
          @if (mode === 'debt-only') {
            <div class="flex justify-end gap-3">
              <button
                type="button"
                (click)="close()"
                class="cursor-pointer rounded-full bg-[#2E7D32] px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#256428]">
                Cerrar
              </button>
            </div>
          } @else {
            @if (mode === 'payment' && !enrollment && step() === 1) {
              <div class="flex justify-end gap-3">
                <button
                  type="button"
                  (click)="close()"
                  class="cursor-pointer rounded-full border border-[#DDEED8] bg-white px-5 py-2.5 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                  Cerrar
                </button>
              </div>
            } @else if (mode === 'payment' && !enrollment && step() === 2) {
              <div class="flex justify-between gap-3">
                <button
                  type="button"
                  (click)="back()"
                  [disabled]="paymentLoading"
                  class="cursor-pointer rounded-full border border-[#DDEED8] bg-white px-5 py-2.5 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9] disabled:cursor-not-allowed disabled:opacity-50">
                  Atrás
                </button>

                <button
                  type="button"
                  (click)="advanceFromManualQuery()"
                  [disabled]="!selectedBill()"
                  class="cursor-pointer rounded-full bg-[#2E7D32] px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#256428] disabled:cursor-not-allowed disabled:opacity-50">
                  Continuar
                </button>
              </div>
            } @else {
              <div class="flex justify-between gap-3">
                <button
                  type="button"
                  (click)="back()"
                  [disabled]="paymentLoading"
                  class="cursor-pointer rounded-full border border-[#DDEED8] bg-white px-5 py-2.5 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9] disabled:cursor-not-allowed disabled:opacity-50">
                  Atrás
                </button>

                <button
                  type="button"
                  (click)="emitPayment()"
                  [disabled]="paymentLoading || !selectedAccount()"
                  class="cursor-pointer rounded-full bg-[#2E7D32] px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#256428] disabled:cursor-not-allowed disabled:opacity-50">
                  {{ paymentLoading ? 'Procesando...' : 'Confirmar pago' }}
                </button>
              </div>
            }
          }
        </div>
      </div>
    </div>
  `
})
export class MyServicePaymentDrawerComponent implements OnChanges {
  @Input() isOpen = false;
  @Input() mode: 'payment' | 'debt-only' = 'payment';
  @Input() initialStep = 1;
  @Input() enrollment: ServiceEnrollmentResponse | null = null;
  @Input() providers: ServiceProviderResponse[] = [];
  @Input() accounts: AccountOwnerResponse[] = [];
  @Input() queryResult: QueryServiceBillsResponse | null = null;
  @Input() queryLoading = false;
  @Input() paymentLoading = false;
  @Input() payment: ServicePaymentResponse | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() queryDebt = new EventEmitter<QueryServiceBillsRequest>();
  @Output() pay = new EventEmitter<{
    sourceAccountNumber: string;
    providerId: string;
    serviceCustomerCode: string;
    billId: string;
    enrollmentId?: string | null;
  }>();
  @Output() paymentClosed = new EventEmitter<void>();

  private readonly fb = inject(FormBuilder);

  readonly step = signal(1);
  readonly selectedBill = signal<ServiceBillQueryItemResponse | null>(null);
  readonly selectedAccount = signal<AccountOwnerResponse | null>(null);

  queryForm = this.fb.group({
    providerId: ['', Validators.required],
    serviceCustomerCode: ['', Validators.required]
  });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] && this.isOpen) {
      this.reset();
    }

    if (changes['queryResult'] && this.queryResult) {
      if (this.mode === 'debt-only') {
        this.step.set(2);
      } else if (this.enrollment) {
        this.step.set(1);
      } else {
        this.step.set(2);
        this.selectedBill.set(this.queryResult.bills[0] ?? null);
      }
    }
  }

  close(): void {
    if (!this.paymentLoading && !this.queryLoading) {
      this.closed.emit();
    }
  }

  emitQuery(): void {
    if (this.enrollment?.provider?.id && this.enrollment?.serviceCustomerCode) {
      this.queryDebt.emit({
        providerId: this.enrollment.provider.id,
        serviceCustomerCode: this.enrollment.serviceCustomerCode.trim()
      });
      return;
    }

    if (this.queryForm.invalid) {
      this.queryForm.markAllAsTouched();
      return;
    }

    const raw = this.queryForm.getRawValue();

    this.queryDebt.emit({
      providerId: raw.providerId ?? '',
      serviceCustomerCode: (raw.serviceCustomerCode ?? '').trim()
    });
  }

  selectBill(bill: ServiceBillQueryItemResponse): void {
    if (this.mode === 'debt-only') {
      return;
    }

    this.selectedBill.set(bill);
    this.step.set(this.enrollment ? 2 : 3);
  }

  selectAccount(account: AccountOwnerResponse): void {
    this.selectedAccount.set(account);
  }

  selectAccountByNumber(accountNumber: string): void {
    const account = this.accounts.find(item => item.accountNumber === accountNumber) ?? null;
    this.selectedAccount.set(account);
  }

  back(): void {
    if (this.mode === 'debt-only') {
      return;
    }

    this.step.set(Math.max(1, this.step() - 1));
    if (!this.enrollment && this.step() === 1) {
      this.selectedBill.set(null);
    }
  }

  advanceFromManualQuery(): void {
    if (!this.selectedBill()) {
      return;
    }

    this.step.set(3);
  }

  emitPayment(): void {
    const bill = this.selectedBill();
    const account = this.selectedAccount();

    if (!bill || !account || !this.queryResult) {
      return;
    }

    this.pay.emit({
      sourceAccountNumber: account.accountNumber,
      providerId: this.queryResult.provider.id,
      serviceCustomerCode: this.queryResult.serviceCustomerCode,
      billId: bill.billId,
      enrollmentId: null
    });
  }

  private reset(): void {
    this.step.set(this.initialStep);
    this.selectedBill.set(null);
    this.selectedAccount.set(null);
    this.queryForm.reset({
      providerId: '',
      serviceCustomerCode: ''
    });
  }
}

import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';
import { AccountOwnerResponse } from '../../../../entities/accounts';
import {
  QueryServiceBillsRequest,
  QueryServiceBillsResponse,
  ServiceBillQueryItemResponse,
  ServicePaymentResponse,
  ServiceProviderResponse
} from '../../../../entities/service-payments';
import { ServicePaymentDetailComponent } from '../service-payment-detail/service-payment-detail.component';

@Component({
  selector: 'app-tenant-bank-service-payment-drawer',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule, ServicePaymentDetailComponent],
  template: `
    <div *ngIf="isOpen" class="fixed inset-0 z-50 flex items-center justify-end">
      <div class="absolute inset-0 bg-black/20 backdrop-blur-sm" (click)="close()"></div>

      <div class="relative flex h-full w-full max-w-xl flex-col overflow-hidden border-l border-[#C8E6C9] bg-white shadow-2xl">
        <div class="border-b border-[#E8F2E2] p-6">
          <div class="flex items-start justify-between gap-4">
            <div>
              <h2 class="text-2xl font-black tracking-tight text-[#1B5E20]">Pago de ventanilla</h2>
              <p class="mt-1 text-sm text-[#6B7D6C]">
                Ingresa la cuenta origen, proveedor y código del servicio.
              </p>
            </div>

            <button
              type="button"
              (click)="close()"
              class="cursor-pointer rounded-full border border-[#DDEED8] p-2 text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
              <lucide-icon name="x" [size]="20"></lucide-icon>
            </button>
          </div>

          <div class="mt-6 grid grid-cols-4 gap-2">
            <div class="h-2 rounded-full" [ngClass]="step() >= 1 ? 'bg-[#2E7D32]' : 'bg-[#E8F2E2]'"></div>
            <div class="h-2 rounded-full" [ngClass]="step() >= 2 ? 'bg-[#2E7D32]' : 'bg-[#E8F2E2]'"></div>
            <div class="h-2 rounded-full" [ngClass]="step() >= 3 ? 'bg-[#2E7D32]' : 'bg-[#E8F2E2]'"></div>
            <div class="h-2 rounded-full" [ngClass]="step() >= 4 ? 'bg-[#2E7D32]' : 'bg-[#E8F2E2]'"></div>
          </div>
        </div>

        <div class="flex-1 overflow-y-auto p-6">
          @if (step() === 1) {
            <section class="space-y-5">
              <div class="rounded-[24px] border border-[#C8E6C9] bg-[#FAFCF8] p-4">
                <h3 class="text-lg font-bold text-[#1B5E20]">1. Cuenta y servicio</h3>
                <p class="mt-1 text-sm text-[#6B7D6C]">
                  La cuenta define quién paga. El código define el titular del servicio.
                </p>
              </div>

              <form [formGroup]="form" class="space-y-5">
                <div class="space-y-2">
                  <label class="text-sm font-semibold text-[#567157]">Cuenta origen</label>
                  <select
                    formControlName="sourceAccountNumber"
                    class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white">
                    <option value="" disabled>Selecciona una cuenta</option>
                    @for (account of accounts; track account.accountNumber) {
                      <option [value]="account.accountNumber">
                        {{ account.displayName || account.accountNameLabel }} - {{ account.accountNumber }} ({{ account.currency }})
                      </option>
                    }
                  </select>
                  <p *ngIf="form.get('sourceAccountNumber')?.invalid && form.get('sourceAccountNumber')?.touched" class="text-xs text-red-600">
                    La cuenta origen es obligatoria.
                  </p>
                </div>

                <div class="space-y-2">
                  <label class="text-sm font-semibold text-[#567157]">Proveedor</label>
                  <select
                    formControlName="providerId"
                    (change)="onProviderChange()"
                    class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white">
                    <option value="" disabled>Selecciona proveedor</option>
                    <option *ngFor="let provider of providers" [value]="provider.id">
                      {{ provider.name }} - {{ categoryLabel(provider.category) }}
                    </option>
                  </select>
                  <p *ngIf="form.get('providerId')?.invalid && form.get('providerId')?.touched" class="text-xs text-red-600">
                    Selecciona un proveedor.
                  </p>
                </div>

                <div class="space-y-2">
                  <label class="text-sm font-semibold text-[#567157]">Código de servicio</label>
                  <select
                    formControlName="serviceCustomerCode"
                    class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white">
                    <option value="" disabled>Selecciona un código</option>
                    <option *ngFor="let code of serviceCustomerCodeOptions" [value]="code">
                      {{ code }}
                    </option>
                  </select>

                  <div *ngIf="serviceCustomerCodeOptions.length === 0" class="rounded-2xl border border-dashed border-[#C8E6C9] bg-[#FAFCF8] px-4 py-3 text-sm text-[#6B7D6C]">
                    No hay códigos sugeridos disponibles para este proveedor.
                  </div>
                  <p *ngIf="form.get('serviceCustomerCode')?.invalid && form.get('serviceCustomerCode')?.touched" class="text-xs text-red-600">
                    El código de servicio es obligatorio.
                  </p>
                </div>
              </form>

              <button
                type="button"
                (click)="emitQuery()"
                [disabled]="queryLoading || form.invalid"
                class="inline-flex w-full cursor-pointer items-center justify-center gap-2 rounded-full bg-[#2E7D32] px-5 py-3 text-sm font-semibold text-white transition-colors hover:bg-[#256428] disabled:cursor-not-allowed disabled:opacity-50">
                <lucide-icon *ngIf="!queryLoading" name="search" [size]="16"></lucide-icon>
                <lucide-icon *ngIf="queryLoading" name="loader-circle" [size]="16" class="animate-spin"></lucide-icon>
                {{ queryLoading ? 'Consultando...' : 'Consultar deuda' }}
              </button>
            </section>
          }

          @if (step() === 2) {
            <section class="space-y-5">
              <div class="rounded-[24px] border border-[#C8E6C9] bg-[#FAFCF8] p-4">
                <h3 class="text-lg font-bold text-[#1B5E20]">2. Deudas pendientes</h3>
                <p class="mt-1 text-sm text-[#6B7D6C]">Selecciona la deuda que será pagada.</p>
              </div>

              @if (queryResult && queryResult.bills.length > 0) {
                <div class="space-y-3">
                  <button
                    *ngFor="let bill of queryResult.bills"
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
                </div>
              } @else {
                <div class="rounded-[24px] border border-dashed border-[#C8E6C9] bg-[#FAFCF8] p-8 text-center">
                  <p class="text-sm font-semibold text-[#1B5E20]">No hay deudas pendientes</p>
                  <p class="mt-1 text-sm text-[#6B7D6C]">Este servicio no tiene deudas disponibles para pagar.</p>
                </div>
              }
            </section>
          }

          @if (step() === 3) {
            <section class="space-y-5">
              <div class="rounded-[24px] border border-[#C8E6C9] bg-[#FAFCF8] p-4">
                <h3 class="text-lg font-bold text-[#1B5E20]">3. Confirmar pago</h3>
                <p class="mt-1 text-sm text-[#6B7D6C]">Revisa los datos antes de confirmar.</p>
              </div>

              <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-5 shadow-sm">
                <div class="space-y-3 text-sm">
                  <div class="flex justify-between gap-4">
                    <span class="text-[#6B7D6C]">Cuenta origen</span>
                    <span class="text-right font-bold text-[#1B5E20]">{{ form.value.sourceAccountNumber }}</span>
                  </div>

                  <div class="flex justify-between gap-4">
                    <span class="text-[#6B7D6C]">Proveedor</span>
                    <span class="font-bold text-[#1B5E20]">{{ queryResult?.provider?.name }}</span>
                  </div>

                  <div class="flex justify-between gap-4">
                    <span class="text-[#6B7D6C]">Código</span>
                    <span class="font-bold text-[#1B5E20]">{{ queryResult?.serviceCustomerCode }}</span>
                  </div>

                  <div class="flex justify-between gap-4">
                    <span class="text-[#6B7D6C]">Titular servicio</span>
                    <span class="text-right font-bold text-[#1B5E20]">{{ queryResult?.serviceCustomerName }}</span>
                  </div>

                  <div class="flex justify-between gap-4">
                    <span class="text-[#6B7D6C]">Periodo</span>
                    <span class="font-bold text-[#1B5E20]">{{ selectedBill()?.billingPeriod }}</span>
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

          @if (step() === 4 && payment) {
            <app-service-payment-detail
              [isOpen]="true"
              [payment]="payment"
              (close)="paymentClosed.emit()">
            </app-service-payment-detail>
          }
        </div>

        <div class="border-t border-[#E8F2E2] p-5">
          <div class="flex justify-between gap-3">
            <button
              type="button"
              (click)="back()"
              [disabled]="step() === 1 || paymentLoading || queryLoading"
              class="cursor-pointer rounded-full border border-[#DDEED8] bg-white px-5 py-2.5 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9] disabled:cursor-not-allowed disabled:opacity-50">
              Atrás
            </button>

            <button
              *ngIf="step() < 3"
              type="button"
              (click)="next()"
              [disabled]="!canGoNext()"
              class="cursor-pointer rounded-full bg-[#2E7D32] px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#256428] disabled:cursor-not-allowed disabled:opacity-50">
              Continuar
            </button>

            <button
              *ngIf="step() === 3"
              type="button"
              (click)="emitPayment()"
              [disabled]="paymentLoading || !selectedBill()"
              class="cursor-pointer rounded-full bg-[#2E7D32] px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#256428] disabled:cursor-not-allowed disabled:opacity-50">
              {{ paymentLoading ? 'Procesando...' : 'Confirmar pago' }}
            </button>

            <button
              *ngIf="step() === 4"
              type="button"
              (click)="paymentClosed.emit()"
              class="cursor-pointer rounded-full bg-[#2E7D32] px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#256428]">
              Finalizar
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class TenantBankServicePaymentDrawerComponent implements OnChanges {
  @Input() isOpen = false;
  @Input() providers: ServiceProviderResponse[] = [];
  @Input() accounts: AccountOwnerResponse[] = [];
  @Input() serviceCustomerCodesByProvider: Record<string, string[]> = {};
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
  }>();
  @Output() paymentClosed = new EventEmitter<void>();

  private readonly fb = inject(FormBuilder);

  readonly step = signal(1);
  readonly selectedBill = signal<ServiceBillQueryItemResponse | null>(null);

  form = this.fb.group({
    sourceAccountNumber: ['', Validators.required],
    providerId: ['', Validators.required],
    serviceCustomerCode: ['', Validators.required]
  });

  get serviceCustomerCodeOptions(): string[] {
    const providerId = this.form.get('providerId')?.value ?? '';
    return this.serviceCustomerCodesByProvider[providerId] ?? [];
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] && this.isOpen) {
      this.reset();
    }

    if (changes['queryResult'] && this.queryResult) {
      this.selectedBill.set(this.queryResult.bills[0] ?? null);
      this.step.set(2);
    }

    if (changes['payment'] && this.payment) {
      this.step.set(4);
    }
  }

  onProviderChange(): void {
    const serviceCodeControl = this.form.get('serviceCustomerCode');
    const hasOptions = this.serviceCustomerCodeOptions.length > 0;

    if (hasOptions) {
      serviceCodeControl?.setValidators([Validators.required]);
      serviceCodeControl?.setValue('');
    } else {
      serviceCodeControl?.clearValidators();
      serviceCodeControl?.setValue('');
    }

    serviceCodeControl?.updateValueAndValidity({ emitEvent: false });
  }

  onServiceCodeChange(value: string): void {
    this.form.get('serviceCustomerCode')?.setValue(value, { emitEvent: false });
  }

  close(): void {
    if (!this.paymentLoading && !this.queryLoading) {
      this.closed.emit();
    }
  }

  emitQuery(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();

    this.queryDebt.emit({
      providerId: raw.providerId ?? '',
      serviceCustomerCode: (raw.serviceCustomerCode ?? '').trim()
    });
  }

  selectBill(bill: ServiceBillQueryItemResponse): void {
    this.selectedBill.set(bill);
  }

  next(): void {
    this.step.set(Math.min(3, this.step() + 1));
  }

  back(): void {
    this.step.set(Math.max(1, this.step() - 1));
  }

  canGoNext(): boolean {
    if (this.step() === 1) {
      return this.form.valid;
    }

    if (this.step() === 2) {
      return !!this.selectedBill();
    }

    if (this.step() === 3) {
      return !!this.selectedBill() && this.form.valid;
    }

    return false;
  }

  emitPayment(): void {
    const bill = this.selectedBill();
    const raw = this.form.getRawValue();

    if (!bill || !this.queryResult) {
      return;
    }

    this.pay.emit({
      sourceAccountNumber: (raw.sourceAccountNumber ?? '').trim(),
      providerId: this.queryResult.provider.id,
      serviceCustomerCode: this.queryResult.serviceCustomerCode,
      billId: bill.billId
    });
  }

  private reset(): void {
    this.step.set(1);
    this.selectedBill.set(null);
    this.form.reset({
      sourceAccountNumber: '',
      providerId: '',
      serviceCustomerCode: ''
    });
  }

  categoryLabel(category: string): string {
    const labels: Record<string, string> = {
      ELECTRICITY: 'Luz',
      WATER: 'Agua',
      INTERNET: 'Internet',
      TV_CABLE: 'Cable TV'
    };

    return labels[category] ?? category;
  }
}

export { TenantBankServicePaymentDrawerComponent as TenantAssistedServicePaymentDrawerComponent };

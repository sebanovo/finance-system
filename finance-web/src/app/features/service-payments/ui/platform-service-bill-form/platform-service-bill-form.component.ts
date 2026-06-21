import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';
import { CreateServiceBillRequest, ServiceProviderResponse } from '../../../../entities/service-payments';

interface BillingPeriodOption {
  value: string;
  label: string;
}

@Component({
  selector: 'app-platform-service-bill-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <div *ngIf="isOpen" class="app-modal-overlay" (click)="close()">
      <div class="app-modal-panel app-modal-panel-sm" (click)="$event.stopPropagation()">
        <div class="app-modal-header border-b border-[#E8F2E2] pb-5">
          <div>
            <h2 class="app-modal-title">Nueva deuda de servicio</h2>
            <p class="app-modal-subtitle">
              Registra una deuda pendiente para un código de servicio existente.
            </p>
          </div>

          <button
            type="button"
            (click)="close()"
            class="cursor-pointer rounded-full border border-[#DDEED8] p-2 text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
            <lucide-icon name="x" [size]="20"></lucide-icon>
          </button>
        </div>

        <div class="max-h-[70vh] overflow-y-auto pt-6">
          <form [formGroup]="form" class="space-y-5">
            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#567157]">Proveedor</label>
              <select
                formControlName="providerId"
                class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white">
                <option value="" disabled>Selecciona proveedor</option>
                <option *ngFor="let provider of providers" [value]="provider.id">
                  {{ provider.name }}
                </option>
              </select>
            </div>

            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#567157]">
                {{ serviceCustomerCodeLabel() }}
              </label>
              <input
                type="text"
                formControlName="serviceCustomerCode"
                placeholder="Ej. 100001"
                class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors placeholder:text-[#9AA99A] focus:border-[#2E7D32] focus:bg-white" />
              <p class="text-xs text-[#6B7D6C]">Usa el código indicado por el proveedor para identificar la deuda.</p>
            </div>

            <div class="grid gap-5 sm:grid-cols-2">
              <div class="space-y-2">
                <label class="text-sm font-semibold text-[#567157]">Periodo</label>
                <select
                  formControlName="billingPeriod"
                  class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white">
                  <option value="" disabled>Selecciona periodo</option>
                  <option *ngFor="let period of billingPeriodOptions" [value]="period.value">
                    {{ period.label }}
                  </option>
                </select>
                <p class="text-xs text-[#6B7D6C]">Mes al que corresponde la deuda.</p>
              </div>

              <div class="space-y-2">
                <label class="text-sm font-semibold text-[#567157]">Vencimiento</label>
                <input
                  type="date"
                  formControlName="dueDate"
                  class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white" />
                <p class="text-xs text-[#6B7D6C]">Fecha límite para pagar esta deuda.</p>
              </div>
            </div>

            <div class="grid gap-5 sm:grid-cols-2">
              <div class="space-y-2">
                <label class="text-sm font-semibold text-[#567157]">Monto</label>
                <input
                  type="number"
                  min="0"
                  step="0.01"
                  formControlName="amount"
                  placeholder="Ej. 180.50"
                  class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors placeholder:text-[#9AA99A] focus:border-[#2E7D32] focus:bg-white" />
              </div>

              <div class="space-y-2">
                <label class="text-sm font-semibold text-[#567157]">Moneda</label>
                <select
                  formControlName="currency"
                  class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white">
                  <option value="BOB">BOB</option>
                  <option value="USD">USD</option>
                  <option value="EUR">EUR</option>
                </select>
              </div>
            </div>
          </form>

          <div class="app-modal-footer mt-6 border-t border-[#E8F2E2] pt-5">
            <button
              type="button"
              (click)="close()"
              class="cursor-pointer rounded-full border border-[#DDEED8] bg-white px-5 py-2.5 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
              Cancelar
            </button>

            <button
              type="button"
              (click)="onSubmit()"
              [disabled]="form.invalid || isSubmitting"
              class="inline-flex cursor-pointer items-center justify-center gap-2 rounded-full bg-[#2E7D32] px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#256428] disabled:cursor-not-allowed disabled:opacity-50">
              <lucide-icon *ngIf="!isSubmitting" name="save" [size]="16"></lucide-icon>
              Crear deuda
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class PlatformServiceBillFormComponent implements OnChanges {
  @Input() isOpen = false;
  @Input() providers: ServiceProviderResponse[] = [];
  @Input() isSubmitting = false;

  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<CreateServiceBillRequest>();

  private readonly fb = inject(FormBuilder);
  readonly billingPeriodOptions = this.buildBillingPeriodOptions();

  form = this.fb.group({
    providerId: ['', Validators.required],
    serviceCustomerCode: ['', Validators.required],
    billingPeriod: ['', Validators.required],
    amount: [0, [Validators.required, Validators.min(0.01)]],
    currency: ['BOB', Validators.required],
    dueDate: ['', Validators.required]
  });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] && this.isOpen) {
      this.resetForm();
    }
  }

  private resetForm(): void {
    this.form.reset({
      providerId: '',
      serviceCustomerCode: '',
      billingPeriod: '',
      amount: 0,
      currency: 'BOB',
      dueDate: ''
    });
  }

  close(): void {
    if (!this.isSubmitting) {
      this.closed.emit();
    }
  }

  onSubmit(): void {
    if (this.form.invalid || this.isSubmitting) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();

    this.saved.emit({
      providerId: raw.providerId ?? '',
      serviceCustomerCode: (raw.serviceCustomerCode ?? '').trim(),
      billingPeriod: raw.billingPeriod ?? '',
      amount: Number(raw.amount ?? 0),
      currency: raw.currency ?? 'BOB',
      dueDate: raw.dueDate ?? ''
    });
  }

  serviceCustomerCodeLabel(): string {
    const providerId = this.form.getRawValue().providerId;
    const provider = this.providers.find((item) => item.id === providerId);
    return provider?.serviceCustomerCodeLabel?.trim() || 'Código de servicio';
  }

  private buildBillingPeriodOptions(monthsAhead = 24): BillingPeriodOption[] {
    const options: BillingPeriodOption[] = [];
    const current = new Date();

    for (let i = 0; i < monthsAhead; i += 1) {
      const date = new Date(current.getFullYear(), current.getMonth() + i, 1);
      const value = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
      const label = new Intl.DateTimeFormat('es-ES', { month: 'long', year: 'numeric' })
        .format(date)
        .replace(/^./, (char) => char.toUpperCase());

      options.push({ value, label });
    }

    return options;
  }
}

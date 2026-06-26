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
                (change)="onProviderChange()"
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
              <select
                formControlName="serviceCustomerCode"
                (change)="onServiceCodeChange($any($event.target).value)"
                class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white">
                <option value="" disabled>Selecciona un código</option>
                <option *ngFor="let code of serviceCustomerCodeOptions" [value]="code">
                  {{ code }}
                </option>
                <option value="__custom__">Otro código...</option>
              </select>

              <input
                *ngIf="serviceCustomerCodeOptions.length === 0 || useCustomCode"
                type="text"
                formControlName="customServiceCustomerCode"
                placeholder="Código manual"
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
  @Input() serviceCustomerCodesByProvider: Record<string, string[]> = {};
  @Input() isSubmitting = false;

  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<CreateServiceBillRequest>();

  private readonly fb = inject(FormBuilder);
  readonly billingPeriodOptions = this.buildBillingPeriodOptions();

  form = this.fb.group({
    providerId: ['', Validators.required],
    serviceCustomerCode: ['', Validators.required],
    customServiceCustomerCode: [''],
    billingPeriod: ['', Validators.required],
    amount: [0, [Validators.required, Validators.min(0.01)]],
    currency: ['BOB', Validators.required],
    dueDate: ['', Validators.required]
  });

  get selectedProviderId(): string {
    return this.form.get('providerId')?.value ?? '';
  }

  get serviceCustomerCodeOptions(): string[] {
    const providerId = this.selectedProviderId;
    return providerId ? this.serviceCustomerCodesByProvider[providerId] ?? [] : [];
  }

  get useCustomCode(): boolean {
    return this.form.get('serviceCustomerCode')?.value === '__custom__';
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] && this.isOpen) {
      this.resetForm();
    }
  }

  private resetForm(): void {
    this.form.reset({
      providerId: '',
      serviceCustomerCode: '',
      customServiceCustomerCode: '',
      billingPeriod: '',
      amount: 0,
      currency: 'BOB',
      dueDate: ''
    });
    this.onProviderChange();
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
    const selectedCode = raw.serviceCustomerCode ?? '';
    const customCode = raw.customServiceCustomerCode?.trim() || '';
    const serviceCustomerCode =
      this.serviceCustomerCodeOptions.length === 0 || selectedCode === '__custom__'
        ? customCode
        : selectedCode.trim();

    this.saved.emit({
      providerId: raw.providerId ?? '',
      serviceCustomerCode,
      billingPeriod: raw.billingPeriod ?? '',
      amount: Number(raw.amount ?? 0),
      currency: raw.currency ?? 'BOB',
      dueDate: raw.dueDate ?? ''
    });
  }

  onProviderChange(): void {
    const serviceCodeControl = this.form.get('serviceCustomerCode');
    const customCodeControl = this.form.get('customServiceCustomerCode');
    const hasOptions = this.serviceCustomerCodeOptions.length > 0;

    if (hasOptions) {
      serviceCodeControl?.setValidators([Validators.required]);
      customCodeControl?.clearValidators();
      serviceCodeControl?.setValue('');
      customCodeControl?.setValue('');
    } else {
      serviceCodeControl?.clearValidators();
      customCodeControl?.setValidators([Validators.required]);
      serviceCodeControl?.setValue('');
      customCodeControl?.setValue('');
    }

    serviceCodeControl?.updateValueAndValidity({ emitEvent: false });
    customCodeControl?.updateValueAndValidity({ emitEvent: false });
  }

  onServiceCodeChange(value: string): void {
    if (value === '__custom__') {
      this.form.get('serviceCustomerCode')?.setValue('__custom__', { emitEvent: false });
      const customCodeControl = this.form.get('customServiceCustomerCode');
      customCodeControl?.setValidators([Validators.required]);
      customCodeControl?.updateValueAndValidity({ emitEvent: false });
      return;
    }

    this.form.get('serviceCustomerCode')?.setValue(value, { emitEvent: false });
    const customCodeControl = this.form.get('customServiceCustomerCode');
    customCodeControl?.clearValidators();
    customCodeControl?.setValue('');
    customCodeControl?.updateValueAndValidity({ emitEvent: false });
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

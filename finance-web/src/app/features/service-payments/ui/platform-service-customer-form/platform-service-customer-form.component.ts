import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';
import {
  CreateServiceCustomerRequest,
  ServiceCustomerResponse,
  ServiceProviderResponse,
  UpdateServiceCustomerRequest
} from '../../../../entities/service-payments';

@Component({
  selector: 'app-platform-service-customer-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <div *ngIf="isOpen" class="app-modal-overlay" (click)="close()">
      <div class="app-modal-panel app-modal-panel-sm" (click)="$event.stopPropagation()">
        <div class="app-modal-header border-b border-[#E8F2E2] pb-5">
          <div>
            <h2 class="app-modal-title">{{ isEditing ? 'Editar cliente de servicio' : 'Nuevo cliente de servicio' }}</h2>
            <p class="app-modal-subtitle">
              Registra códigos válidos por proveedor para simular deudas.
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
                  {{ provider.name }} - {{ categoryLabel(provider.category) }}
                </option>
              </select>
            </div>

            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#567157]">Código de servicio</label>
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
            </div>

            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#567157]">Titular</label>
              <input
                type="text"
                formControlName="customerName"
                placeholder="Ej. Carlos Mendoza"
                class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors placeholder:text-[#9AA99A] focus:border-[#2E7D32] focus:bg-white" />
            </div>

            <div *ngIf="isEditing" class="space-y-2">
              <label class="text-sm font-semibold text-[#567157]">Estado</label>
              <select
                formControlName="status"
                class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white">
                <option value="ACTIVE">Activo</option>
                <option value="INACTIVE">Inactivo</option>
              </select>
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
              {{ isEditing ? 'Actualizar' : 'Crear cliente' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class PlatformServiceCustomerFormComponent implements OnChanges {
  @Input() isOpen = false;
  @Input() customer: ServiceCustomerResponse | null = null;
  @Input() providers: ServiceProviderResponse[] = [];
  @Input() serviceCustomerCodesByProvider: Record<string, string[]> = {};
  @Input() isSubmitting = false;

  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<{
    request: CreateServiceCustomerRequest | UpdateServiceCustomerRequest;
    isEditing: boolean;
    id?: string;
  }>();

  private readonly fb = inject(FormBuilder);

  form = this.fb.group({
    providerId: ['', Validators.required],
    serviceCustomerCode: ['', Validators.required],
    customServiceCustomerCode: [''],
    customerName: ['', Validators.required],
    status: ['ACTIVE']
  });

  get isEditing(): boolean {
    return !!this.customer;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] || changes['customer']) {
      this.resetForm();
    }
  }

  private resetForm(): void {
    this.form = this.fb.group({
      providerId: [this.customer?.providerId ?? '', Validators.required],
      serviceCustomerCode: [this.customer?.serviceCustomerCode ?? '', Validators.required],
      customServiceCustomerCode: [''],
      customerName: [this.customer?.customerName ?? '', Validators.required],
      status: [this.customer?.status ?? 'ACTIVE']
    });

    if (this.isEditing) {
      this.form.get('providerId')?.disable({ emitEvent: false });
      this.form.get('serviceCustomerCode')?.disable({ emitEvent: false });
    }

    this.onProviderChange();
  }

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

    if (this.isEditing && this.customer) {
      this.saved.emit({
        isEditing: true,
        id: this.customer.id,
        request: {
          customerName: raw.customerName ?? '',
          status: raw.status ?? 'ACTIVE'
        }
      });
      return;
    }

    this.saved.emit({
      isEditing: false,
      request: {
        providerId: raw.providerId ?? '',
        serviceCustomerCode,
        customerName: raw.customerName ?? ''
      }
    });
  }

  onProviderChange(): void {
    if (this.isEditing) {
      return;
    }

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

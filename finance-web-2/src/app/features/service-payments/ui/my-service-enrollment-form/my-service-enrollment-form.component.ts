import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';
import { CreateServiceEnrollmentRequest, ServiceProviderResponse } from '../../../../entities/service-payments';

@Component({
  selector: 'app-my-service-enrollment-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <div *ngIf="isOpen" class="app-modal-overlay" (click)="close()">
      <div class="app-modal-panel app-modal-panel-sm" (click)="$event.stopPropagation()">
        <div class="app-modal-header border-b border-[#E8F2E2] pb-5">
          <div>
            <h2 class="app-modal-title">Afiliar servicio</h2>
            <p class="app-modal-subtitle">
              Guarda luz, agua, internet o cable para consultar y pagar más rápido.
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
                  {{ provider.name }} - {{ categoryLabel(provider.category) }}
                </option>
              </select>
              <p *ngIf="form.get('providerId')?.invalid && form.get('providerId')?.touched" class="text-xs text-red-600">
                Selecciona un proveedor.
              </p>
            </div>

            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#567157]">Código de servicio</label>
              <input
                type="text"
                formControlName="serviceCustomerCode"
                placeholder="Ej. 100001"
                class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors placeholder:text-[#9AA99A] focus:border-[#2E7D32] focus:bg-white" />
              <p *ngIf="form.get('serviceCustomerCode')?.invalid && form.get('serviceCustomerCode')?.touched" class="text-xs text-red-600">
                Ingresa el código del servicio.
              </p>
            </div>

            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#567157]">Alias</label>
              <input
                type="text"
                formControlName="alias"
                placeholder="Ej. Luz casa"
                class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors placeholder:text-[#9AA99A] focus:border-[#2E7D32] focus:bg-white" />
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
              <svg *ngIf="isSubmitting" class="h-4 w-4 animate-spin text-white" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Afiliar
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class MyServiceEnrollmentFormComponent implements OnChanges {
  @Input() isOpen = false;
  @Input() providers: ServiceProviderResponse[] = [];
  @Input() isSubmitting = false;

  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<CreateServiceEnrollmentRequest>();

  private readonly fb = inject(FormBuilder);

  form = this.fb.group({
    providerId: ['', Validators.required],
    serviceCustomerCode: ['', Validators.required],
    alias: ['']
  });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] && this.isOpen) {
      this.form.reset({
        providerId: '',
        serviceCustomerCode: '',
        alias: ''
      });
    }
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
      alias: raw.alias?.trim() || null
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

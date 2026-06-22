import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';
import {
  CreateServiceProviderRequest,
  ServiceProviderResponse,
  UpdateServiceProviderRequest
} from '../../../../entities/service-payments';

@Component({
  selector: 'app-platform-service-provider-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <div *ngIf="isOpen" class="app-modal-overlay" (click)="close()">
      <div class="app-modal-panel app-modal-panel-sm" (click)="$event.stopPropagation()">
        <div class="app-modal-header border-b border-[#E8F2E2] pb-5">
          <div>
            <h2 class="app-modal-title">{{ isEditing ? 'Editar proveedor' : 'Nuevo proveedor' }}</h2>
            <p class="app-modal-subtitle">
              {{ isEditing ? 'Modifica los datos del proveedor de servicio.' : 'Registra una empresa de luz, agua, internet o cable.' }}
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
          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-5">
            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#567157]">Código</label>
              <input
                type="text"
                formControlName="code"
                placeholder="Ej. ELECTRICITY_CRE"
                [readonly]="isEditing"
                class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors placeholder:text-[#9AA99A] focus:border-[#2E7D32] focus:bg-white disabled:opacity-60" />
              <p *ngIf="form.get('code')?.invalid && form.get('code')?.touched" class="text-xs text-red-600">
                El código es obligatorio.
              </p>
            </div>

            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#567157]">Nombre</label>
              <input
                type="text"
                formControlName="name"
                placeholder="Ej. CRE"
                class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors placeholder:text-[#9AA99A] focus:border-[#2E7D32] focus:bg-white" />
              <p *ngIf="form.get('name')?.invalid && form.get('name')?.touched" class="text-xs text-red-600">
                El nombre es obligatorio.
              </p>
            </div>

            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#567157]">Categoría</label>
              <select
                formControlName="category"
                class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white">
                <option value="ELECTRICITY">Luz</option>
                <option value="WATER">Agua</option>
                <option value="INTERNET">Internet</option>
                <option value="TV_CABLE">Cable TV</option>
              </select>
            </div>

            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#567157]">Etiqueta del código</label>
              <input
                type="text"
                formControlName="serviceCustomerCodeLabel"
                placeholder="Ej. Código de suministro"
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
              {{ isEditing ? 'Actualizar' : 'Crear proveedor' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class PlatformServiceProviderFormComponent implements OnChanges {
  @Input() isOpen = false;
  @Input() provider: ServiceProviderResponse | null = null;
  @Input() isSubmitting = false;

  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<{
    request: CreateServiceProviderRequest | UpdateServiceProviderRequest;
    isEditing: boolean;
    id?: string;
  }>();

  private readonly fb = inject(FormBuilder);

  form = this.fb.group({
    code: ['', Validators.required],
    name: ['', Validators.required],
    category: ['ELECTRICITY', Validators.required],
    serviceCustomerCodeLabel: ['']
  });

  get isEditing(): boolean {
    return !!this.provider;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] || changes['provider']) {
      this.resetForm();
    }
  }

  private resetForm(): void {
    this.form = this.fb.group({
      code: [this.provider?.code ?? '', Validators.required],
      name: [this.provider?.name ?? '', Validators.required],
      category: [this.provider?.category ?? 'ELECTRICITY', Validators.required],
      serviceCustomerCodeLabel: [this.provider?.serviceCustomerCodeLabel ?? '']
    });

    if (this.isEditing) {
      this.form.get('code')?.disable({ emitEvent: false });
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

    if (this.isEditing && this.provider) {
      this.saved.emit({
        isEditing: true,
        id: this.provider.id,
        request: {
          name: raw.name ?? '',
          category: raw.category ?? 'ELECTRICITY',
          serviceCustomerCodeLabel: raw.serviceCustomerCodeLabel || null
        }
      });
      return;
    }

    this.saved.emit({
      isEditing: false,
      request: {
        code: (raw.code ?? '').trim().toUpperCase(),
        name: raw.name ?? '',
        category: raw.category ?? 'ELECTRICITY',
        serviceCustomerCodeLabel: raw.serviceCustomerCodeLabel || null
      }
    });
  }
}

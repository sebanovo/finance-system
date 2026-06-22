import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';

type MyAccountFormValue = {
  accountName?: string;
  customAlias?: string;
  accountType?: string;
  currency?: string;
};

@Component({
  selector: 'app-my-account-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <div *ngIf="isOpen" class="app-modal-overlay">
      <div class="app-modal-panel app-modal-panel-sm" (click)="$event.stopPropagation()">
        <div class="app-modal-header border-b border-[#E8F2E2] pb-5">
          <div>
            <h2 class="app-modal-title">{{ isEditing ? 'Editar alias' : 'Nueva cuenta' }}</h2>
            <p class="app-modal-subtitle">
              {{ isEditing ? 'Actualiza el alias visible de tu cuenta.' : 'Crea una nueva cuenta para tu perfil.' }}
            </p>
          </div>
          <button type="button" (click)="close()" class="cursor-pointer rounded-full border border-[#DDEED8] p-2 text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
            <lucide-icon name="x" [size]="20"></lucide-icon>
          </button>
        </div>

        <div class="max-h-[70vh] overflow-y-auto pt-6">
          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-5">
            @if (!isEditing) {
              <div class="space-y-2">
                <label class="text-sm font-semibold text-[#567157]">Nombre de la cuenta</label>
                <select
                  formControlName="accountName"
                  class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white">
                  <option value="" disabled selected>Selecciona un nombre</option>
                  <option value="MAIN_WALLET">Billetera Principal</option>
                  <option value="SAVINGS_ACCOUNT">Cuenta de Ahorros</option>
                  <option value="CHECKING_ACCOUNT">Cuenta Corriente</option>
                  <option value="SECONDARY_ACCOUNT">Cuenta Secundaria</option>
                  <option value="BUSINESS_ACCOUNT">Cuenta de Negocios</option>
                  <option value="CREDIT_CARD_ACCOUNT">Tarjeta de Crédito</option>
                  <option value="PREPAID_CARD_ACCOUNT">Tarjeta Prepaga</option>
                  <option value="LOAN_ACCOUNT">Cuenta de Préstamo</option>
                </select>
              </div>

              <div class="grid gap-5 sm:grid-cols-2">
                <div class="space-y-2">
                  <label class="text-sm font-semibold text-[#567157]">Tipo de cuenta</label>
                <select
                  formControlName="accountType"
                  class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white">
                  <option value="WALLET">Billetera</option>
                  <option value="SAVINGS">Ahorros</option>
                  <option value="CHECKING">Corriente</option>
                </select>
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
            }

            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#567157]">Alias personalizado</label>
              <input
                type="text"
                formControlName="customAlias"
                placeholder="Ej. Ahorros personales"
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
              class="cursor-pointer inline-flex items-center justify-center gap-2 rounded-full bg-[#2E7D32] px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#256428] disabled:cursor-not-allowed disabled:opacity-50">
              <lucide-icon *ngIf="!isSubmitting" name="save" [size]="16"></lucide-icon>
              <svg *ngIf="isSubmitting" class="h-4 w-4 animate-spin text-white" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              {{ isEditing ? 'Actualizar alias' : 'Crear cuenta' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class MyAccountFormComponent implements OnChanges {
  @Input() isOpen = false;
  @Input() account: { id: string; customAlias?: string; accountName?: string; accountType?: string; currency?: string } | null = null;
  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<{ request: MyAccountFormValue; isEditing: boolean }>();

  private readonly fb = inject(FormBuilder);
  form = this.fb.group({
    accountName: ['', Validators.required],
    customAlias: [''],
    accountType: ['SAVINGS'],
    currency: ['BOB']
  });
  isSubmitting = false;

  get isEditing(): boolean {
    return !!this.account;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] || changes['account']) {
      this.resetForm();
    }
  }

  private resetForm(): void {
    this.form = this.fb.group({
      accountName: [this.account?.accountName || '', this.isEditing ? [] : Validators.required],
      customAlias: [this.account?.customAlias || ''],
      accountType: [this.account?.accountType || 'SAVINGS'],
      currency: [this.account?.currency || 'BOB']
    });

    if (this.isEditing) {
      this.form.get('accountName')?.disable({ emitEvent: false });
      this.form.get('accountType')?.disable({ emitEvent: false });
      this.form.get('currency')?.disable({ emitEvent: false });
    }
  }

  close(): void {
    this.closed.emit();
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    const raw = this.form.getRawValue();
    const request: MyAccountFormValue = this.isEditing
      ? { customAlias: raw.customAlias || '' }
        : {
          accountName: raw.accountName || '',
          customAlias: raw.customAlias || '',
          accountType: raw.accountType || 'SAVINGS',
          currency: raw.currency || 'BOB'
        };

    this.saved.emit({ request, isEditing: this.isEditing });
  }

  setSubmitting(value: boolean): void {
    this.isSubmitting = value;
    if (!value) {
      this.close();
    }
  }
}

import { Component, EventEmitter, Input, Output, inject, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LucideAngularModule, X, Save, Building2, UserCircle2 } from 'lucide-angular';
import { CreateAccountRequest, UpdateAccountRequest, AccountOwnerResponse } from '../../../../entities/accounts';
import { TenantUserResponse } from '../../../../entities/user';
import { UserListUseCase } from '../../../user-management';

@Component({
  selector: 'app-account-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <div *ngIf="isOpen" class="app-modal-overlay" (click)="close()">
      <div class="app-modal-panel app-modal-panel-sm" (click)="$event.stopPropagation()">
        <div class="app-modal-header border-b border-[#E8F2E2] pb-5">
          <div>
            <h2 class="app-modal-title">
              {{ isEditing ? 'Editar cuenta' : 'Nueva cuenta' }}
            </h2>
            <p class="app-modal-subtitle">
              {{ isEditing ? 'Modifica los detalles de la cuenta.' : 'Crea una nueva cuenta bancaria para un cliente.' }}
            </p>
          </div>
          <button 
            (click)="close()"
            class="cursor-pointer rounded-full border border-[#DDEED8] p-2 text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
            <lucide-icon name="x" [size]="20"></lucide-icon>
          </button>
        </div>

        <div class="max-h-[70vh] overflow-y-auto pt-6">
        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-5">
          
          <!-- Usuario (Sólo creación) -->
          <div *ngIf="!isEditing" class="space-y-2">
            <label class="text-sm font-semibold text-[#567157]">Cliente (usuario)</label>
            <div class="relative">
              <lucide-icon name="user-circle-2" class="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-[#6B7D6C]"></lucide-icon>
              <select 
                formControlName="userId"
                class="flex h-11 w-full items-center justify-between rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 pl-10 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white">
                <option value="" disabled selected>Selecciona un cliente</option>
                <option *ngFor="let user of userListUseCase.data()" [value]="user.id">
                  {{ user.firstName }} {{ user.lastName }} ({{ user.email }})
                </option>
              </select>
            </div>
            <p *ngIf="form.get('userId')?.invalid && form.get('userId')?.touched" class="text-xs text-red-600">
              El cliente es obligatorio.
            </p>
          </div>

          <!-- Tipo de Cuenta (Sólo creación) -->
          <div *ngIf="!isEditing" class="space-y-2">
            <label class="text-sm font-semibold text-[#567157]">Tipo de cuenta</label>
            <div class="relative">
              <lucide-icon name="building-2" class="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-[#6B7D6C]"></lucide-icon>
              <select 
                formControlName="accountType"
                class="flex h-11 w-full items-center justify-between rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 pl-10 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white">
                <option value="WALLET">Billetera (WALLET)</option>
                <option value="SAVINGS">Ahorros (SAVINGS)</option>
                <option value="CHECKING">Corriente (CHECKING)</option>
              </select>
            </div>
          </div>

          <!-- Nombre de Cuenta -->
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
            <p *ngIf="form.get('accountName')?.invalid && form.get('accountName')?.touched" class="text-xs text-destructive">
              El nombre es obligatorio.
            </p>
          </div>

          <!-- Moneda (Sólo creación) -->
          <div *ngIf="!isEditing" class="space-y-2">
            <label class="text-sm font-semibold text-[#567157]">Moneda</label>
            <select 
              formControlName="currency"
              class="flex h-11 w-full items-center justify-between rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white">
              <option value="BOB">Boliviano (BOB)</option>
              <option value="USD">Dólar Estadounidense (USD)</option>
              <option value="EUR">Euro (EUR)</option>
            </select>
          </div>

          <!-- Alias Personalizado -->
          <div class="space-y-2">
            <label class="text-sm font-semibold text-[#567157]">Alias personalizado (opcional)</label>
            <input 
              type="text" 
              formControlName="customAlias"
              placeholder="Ej. Ahorros Viaje"
              class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition-colors placeholder:text-[#9AA99A] focus:border-[#2E7D32] focus:bg-white" />
          </div>

          <!-- Cuenta Principal -->
          <div class="flex items-center space-x-2 pt-2">
            <input 
              type="checkbox" 
              formControlName="primary"
              id="primaryAccount"
              class="peer h-4 w-4 shrink-0 rounded-sm border border-[#2E7D32] ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#2E7D32] focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 data-[state=checked]:bg-[#2E7D32] data-[state=checked]:text-white" />
            <label for="primaryAccount" class="text-sm font-medium leading-none text-[#1B5E20] peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
              Establecer como cuenta principal
            </label>
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
            {{ isEditing ? 'Actualizar' : 'Crear cuenta' }}
          </button>
        </div>
        </div>
      </div>
    </div>
  `
})
export class AccountFormComponent implements OnInit, OnChanges {
  @Input() isOpen = false;
  @Input() account: AccountOwnerResponse | null = null;
  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<{ request: CreateAccountRequest | UpdateAccountRequest, isEditing: boolean }>();

  public readonly userListUseCase = inject(UserListUseCase);
  private fb = inject(FormBuilder);

  form!: FormGroup;
  isSubmitting = false;

  get isEditing(): boolean {
    return !!this.account;
  }

  ngOnInit() {
    this.initForm();
    if (this.userListUseCase.data().length === 0) {
      this.userListUseCase.loadUsers();
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['account'] || changes['isOpen']) {
      if (this.isOpen) {
        this.initForm();
      }
    }
  }

  initForm() {
    if (this.isEditing && this.account) {
      this.form = this.fb.group({
        accountName: [this.account.accountName, Validators.required],
        customAlias: [this.account.customAlias || ''],
        primary: [this.account.primary || false]
      });
    } else {
        this.form = this.fb.group({
          userId: ['', Validators.required],
          accountName: ['', Validators.required],
          accountType: ['SAVINGS', Validators.required],
          currency: ['BOB', Validators.required],
          customAlias: [''],
          primary: [false]
        });
    }
  }

  close() {
    this.isOpen = false;
    this.closed.emit();
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.saved.emit({
      request: this.form.value,
      isEditing: this.isEditing
    });
  }

  setSubmitting(val: boolean) {
    this.isSubmitting = val;
    if (!val) {
      this.close();
    }
  }
}

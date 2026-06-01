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
    <!-- Overlay -->
    <div 
      *ngIf="isOpen" 
      class="fixed inset-0 bg-background/80 backdrop-blur-sm z-40 transition-opacity"
      (click)="close()">
    </div>

    <!-- Slide-over -->
    <div 
      class="fixed inset-y-0 right-0 z-50 w-full max-w-md bg-card shadow-2xl border-l border-border transform transition-transform duration-300 ease-in-out flex flex-col"
      [class.translate-x-0]="isOpen"
      [class.translate-x-full]="!isOpen">
      
      <!-- Header -->
      <div class="flex items-center justify-between p-6 border-b border-border bg-muted/30">
        <div>
          <h2 class="text-xl font-bold text-foreground">
            {{ isEditing ? 'Editar Cuenta' : 'Nueva Cuenta' }}
          </h2>
          <p class="text-sm text-muted-foreground mt-1">
            {{ isEditing ? 'Modifica los detalles de la cuenta.' : 'Crea una nueva cuenta bancaria para un cliente.' }}
          </p>
        </div>
        <button 
          (click)="close()"
          class="p-2 rounded-full hover:bg-muted text-muted-foreground hover:text-foreground transition-colors">
          <lucide-icon name="x" [size]="20"></lucide-icon>
        </button>
      </div>

      <!-- Content -->
      <div class="flex-1 overflow-y-auto p-6">
        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-5">
          
          <!-- Usuario (Sólo creación) -->
          <div *ngIf="!isEditing" class="space-y-2">
            <label class="text-sm font-medium text-foreground">Cliente (Usuario)</label>
            <div class="relative">
              <lucide-icon name="user-circle-2" class="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground"></lucide-icon>
              <select 
                formControlName="userId"
                class="flex h-10 w-full items-center justify-between rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 pl-10">
                <option value="" disabled selected>Selecciona un cliente</option>
                <option *ngFor="let user of userListUseCase.data()" [value]="user.id">
                  {{ user.firstName }} {{ user.lastName }} ({{ user.email }})
                </option>
              </select>
            </div>
            <p *ngIf="form.get('userId')?.invalid && form.get('userId')?.touched" class="text-xs text-destructive">
              El cliente es obligatorio.
            </p>
          </div>

          <!-- Tipo de Cuenta (Sólo creación) -->
          <div *ngIf="!isEditing" class="space-y-2">
            <label class="text-sm font-medium text-foreground">Tipo de Cuenta</label>
            <div class="relative">
              <lucide-icon name="building-2" class="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground"></lucide-icon>
              <select 
                formControlName="accountType"
                class="flex h-10 w-full items-center justify-between rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 pl-10">
                <option value="SAVINGS">Ahorros (SAVINGS)</option>
                <option value="CHECKING">Corriente (CHECKING)</option>
              </select>
            </div>
          </div>

          <!-- Nombre de Cuenta -->
          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground">Nombre de la Cuenta</label>
            <select 
              formControlName="accountName"
              class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50">
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
            <label class="text-sm font-medium text-foreground">Moneda</label>
            <select 
              formControlName="currency"
              class="flex h-10 w-full items-center justify-between rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2">
              <option value="USD">Dólar Estadounidense (USD)</option>
              <option value="BOB">Boliviano (BOB)</option>
              <option value="EUR">Euro (EUR)</option>
            </select>
          </div>

          <!-- Alias Personalizado -->
          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground">Alias Personalizado (Opcional)</label>
            <input 
              type="text" 
              formControlName="customAlias"
              placeholder="Ej. Ahorros Viaje"
              class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50" />
          </div>

          <!-- Cuenta Principal -->
          <div class="flex items-center space-x-2 pt-2">
            <input 
              type="checkbox" 
              formControlName="primary"
              id="primaryAccount"
              class="peer h-4 w-4 shrink-0 rounded-sm border border-primary ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 data-[state=checked]:bg-primary data-[state=checked]:text-primary-foreground" />
            <label for="primaryAccount" class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70 text-foreground">
              Establecer como cuenta principal
            </label>
          </div>
        </form>
      </div>

      <!-- Footer -->
      <div class="p-6 border-t border-border bg-muted/10 flex justify-end gap-3">
        <button 
          type="button" 
          (click)="close()"
          class="inline-flex items-center justify-center rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 border border-input bg-background hover:bg-accent hover:text-accent-foreground h-10 px-4 py-2">
          Cancelar
        </button>
        <button 
          type="button" 
          (click)="onSubmit()"
          [disabled]="form.invalid || isSubmitting"
          class="inline-flex items-center justify-center rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 bg-primary text-primary-foreground hover:bg-primary/90 h-10 px-4 py-2 gap-2 shadow-sm">
          <lucide-icon *ngIf="!isSubmitting" name="save" [size]="16"></lucide-icon>
          <svg *ngIf="isSubmitting" class="animate-spin h-4 w-4 text-primary-foreground" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          {{ isEditing ? 'Actualizar' : 'Crear Cuenta' }}
        </button>
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
        currency: ['USD', Validators.required],
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

import { Component, EventEmitter, Input, Output, inject, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LucideAngularModule, X, Send, ArrowRightLeft, ArrowDownToLine, ArrowUpFromLine, CreditCard } from 'lucide-angular';
import { AccountListUseCase } from '../../../account-management';

export type TransactionActionType = 'deposit' | 'withdrawal' | 'transfer' | 'payment';

@Component({
  selector: 'app-transaction-slide-over',
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
      <div class="flex items-center justify-between p-6 border-b border-border" [ngClass]="headerConfig.bgClass">
        <div class="flex items-center gap-3">
          <div class="p-2 rounded-lg bg-background/50">
            <lucide-icon [name]="headerConfig.icon" [size]="24" [class]="headerConfig.textClass"></lucide-icon>
          </div>
          <div>
            <h2 class="text-xl font-bold text-foreground">{{ headerConfig.title }}</h2>
            <p class="text-sm text-muted-foreground">{{ headerConfig.description }}</p>
          </div>
        </div>
        <button 
          (click)="close()"
          class="p-2 rounded-full hover:bg-background/50 text-muted-foreground hover:text-foreground transition-colors">
          <lucide-icon name="x" [size]="20"></lucide-icon>
        </button>
      </div>

      <!-- Content -->
      <div class="flex-1 overflow-y-auto p-6">
        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-5">
          
          <!-- Cuenta de Origen -->
          <div *ngIf="transactionType !== 'deposit'" class="space-y-2">
            <label class="text-sm font-medium text-foreground">Cuenta Origen</label>
            <select 
              formControlName="sourceAccountId"
              class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2">
              <option value="" disabled selected>Selecciona una cuenta</option>
              <option *ngFor="let acc of accountListUseCase.data()" [value]="acc.id">
                {{ acc.accountNumber }} - {{ acc.customAlias || acc.accountNameLabel }} ({{ acc.currency }})
              </option>
            </select>
          </div>

          <!-- Cuenta Destino -->
          <div *ngIf="transactionType !== 'withdrawal'" class="space-y-2">
            <label class="text-sm font-medium text-foreground">Cuenta Destino</label>
            <!-- Si es transferencia o pago, asumo que puede seleccionar una cuenta del sistema, de lo contrario podría ser texto libre (ej targetAccountId). Por simplificación, mostramos el combo de cuentas del sistema -->
            <select 
              formControlName="targetAccountId"
              class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2">
              <option value="" disabled selected>Selecciona una cuenta destino</option>
              <option *ngFor="let acc of accountListUseCase.data()" [value]="acc.id">
                {{ acc.accountNumber }} - {{ acc.customAlias || acc.accountNameLabel }} ({{ acc.currency }})
              </option>
            </select>
          </div>

          <!-- Monto y Moneda -->
          <div class="grid grid-cols-3 gap-4">
            <div class="col-span-2 space-y-2">
              <label class="text-sm font-medium text-foreground">Monto</label>
              <input 
                type="number" 
                formControlName="amount"
                placeholder="0.00"
                min="0.01" step="0.01"
                class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2" />
            </div>
            <div class="space-y-2">
              <label class="text-sm font-medium text-foreground">Moneda</label>
              <select 
                formControlName="currency"
                class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2">
                <option value="USD">USD</option>
                <option value="BOB">BOB</option>
                <option value="EUR">EUR</option>
              </select>
            </div>
          </div>

          <!-- Canal (Sólo para Depósitos y Retiros) -->
          <div *ngIf="transactionType === 'deposit' || transactionType === 'withdrawal'" class="space-y-2">
            <label class="text-sm font-medium text-foreground">Canal</label>
            <select 
              formControlName="channel"
              class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2">
              <option value="BRANCH">Sucursal (Caja)</option>
              <option value="ATM">Cajero Automático</option>
              <option value="WEB">Web</option>
            </select>
          </div>

          <!-- Descripción -->
          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground">Descripción</label>
            <textarea 
              formControlName="description"
              placeholder="Motivo o detalle de la transacción"
              rows="3"
              class="flex w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 resize-none"></textarea>
          </div>

          <!-- Referencia Externa -->
          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground">Referencia Externa (Opcional)</label>
            <input 
              type="text" 
              formControlName="externalReference"
              placeholder="Ej. ID de comprobante"
              class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2" />
          </div>

        </form>
      </div>

      <!-- Footer -->
      <div class="p-6 border-t border-border bg-muted/10 flex justify-end gap-3">
        <button 
          type="button" 
          (click)="close()"
          class="inline-flex items-center justify-center rounded-md text-sm font-medium border border-input bg-background hover:bg-accent hover:text-accent-foreground h-10 px-4 py-2 transition-colors">
          Cancelar
        </button>
        <button 
          type="button" 
          (click)="onSubmit()"
          [disabled]="form.invalid || isSubmitting"
          class="inline-flex items-center justify-center rounded-md text-sm font-medium bg-primary text-primary-foreground hover:bg-primary/90 h-10 px-4 py-2 gap-2 shadow-sm transition-colors disabled:opacity-50">
          <lucide-icon *ngIf="!isSubmitting" name="send" [size]="16"></lucide-icon>
          <svg *ngIf="isSubmitting" class="animate-spin h-4 w-4 text-primary-foreground" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          Procesar
        </button>
      </div>
    </div>
  `
})
export class TransactionSlideOverComponent implements OnInit, OnChanges {
  @Input() isOpen = false;
  @Input() transactionType: TransactionActionType = 'deposit';
  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<{ type: TransactionActionType, request: any }>();

  public readonly accountListUseCase = inject(AccountListUseCase);
  private fb = inject(FormBuilder);
  
  form!: FormGroup;
  isSubmitting = false;

  get headerConfig() {
    switch (this.transactionType) {
      case 'deposit': return { title: 'Depósito', description: 'Abonar fondos a una cuenta', icon: 'arrow-down-to-line', bgClass: 'bg-green-500/10', textClass: 'text-green-600' };
      case 'withdrawal': return { title: 'Retiro', description: 'Debitar fondos de una cuenta', icon: 'arrow-up-from-line', bgClass: 'bg-orange-500/10', textClass: 'text-orange-600' };
      case 'transfer': return { title: 'Transferencia', description: 'Mover fondos entre cuentas', icon: 'arrow-right-left', bgClass: 'bg-blue-500/10', textClass: 'text-blue-600' };
      case 'payment': return { title: 'Pago', description: 'Registrar un pago', icon: 'credit-card', bgClass: 'bg-purple-500/10', textClass: 'text-purple-600' };
      default: return { title: 'Transacción', description: '', icon: 'send', bgClass: 'bg-muted', textClass: 'text-foreground' };
    }
  }

  ngOnInit() {
    this.initForm();
    if (this.accountListUseCase.data().length === 0) {
      this.accountListUseCase.loadAccounts();
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['transactionType'] || changes['isOpen']) {
      if (this.isOpen) {
        this.initForm();
        this.isSubmitting = false;
      }
    }
  }

  initForm() {
    this.form = this.fb.group({
      amount: ['', [Validators.required, Validators.min(0.01)]],
      currency: ['USD', Validators.required],
      description: ['', Validators.required],
      externalReference: ['']
    });

    if (this.transactionType !== 'deposit') {
      this.form.addControl('sourceAccountId', this.fb.control('', Validators.required));
    }
    
    if (this.transactionType !== 'withdrawal') {
      this.form.addControl('targetAccountId', this.fb.control('', Validators.required));
    }

    if (this.transactionType === 'deposit' || this.transactionType === 'withdrawal') {
      this.form.addControl('channel', this.fb.control('BRANCH', Validators.required));
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
      type: this.transactionType,
      request: this.form.value
    });
  }

  setSubmitting(val: boolean) {
    this.isSubmitting = val;
    if (!val) {
      this.close();
    }
  }
}

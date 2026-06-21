import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LucideAngularModule, AlertTriangle, ArrowDownToLine, ArrowRightLeft, ArrowUpFromLine, CheckCircle, CreditCard, RotateCcw, Send, X } from 'lucide-angular';
import { firstValueFrom } from 'rxjs';
import { AccountsService, AccountLookupResponse } from '../../../../entities/accounts';
import { AccountOwnerResponse } from '../../../../entities/accounts';
import { TransactionOperationType } from '../../../../entities/transactions';

export type TransactionActionType = TransactionOperationType;

interface TransactionSlideOverSaveEvent {
  type: TransactionActionType;
  request: Record<string, unknown>;
  referenceId?: string;
}

type HeaderConfig = {
  title: string;
  description: string;
  icon: string;
};

@Component({
  selector: 'app-transaction-slide-over',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <div
      *ngIf="isOpen"
      class="fixed inset-0 z-40 bg-background/80 backdrop-blur-sm transition-opacity"
      (click)="close()">
    </div>

    <div
      class="fixed inset-y-0 right-0 z-50 flex w-full max-w-md transform flex-col border-l border-[#C8E6C9] bg-white shadow-[0_18px_50px_rgba(27,94,32,0.12)] transition-transform duration-300 ease-in-out"
      [class.translate-x-0]="isOpen"
      [class.translate-x-full]="!isOpen">

      <div class="flex items-center justify-between border-b border-[#E8F2E2] bg-gradient-to-br from-white via-[#F7FBF3] to-[#EAF6EB] p-6">
        <div class="flex items-center gap-3">
          <div class="rounded-2xl border border-[#DDEED8] bg-white/90 p-2">
            <lucide-icon [name]="headerConfig.icon" [size]="24" class="text-[#2E7D32]"></lucide-icon>
          </div>
          <div>
            <h2 class="text-xl font-black text-[#1B5E20]">{{ headerConfig.title }}</h2>
            <p class="text-sm text-[#5F6F5F]">{{ headerConfig.description }}</p>
          </div>
        </div>
        <button
          type="button"
          (click)="close()"
          class="cursor-pointer rounded-full p-2 text-[#6B7D6C] transition-colors hover:bg-[#F1F8E9] hover:text-[#1B5E20]">
          <lucide-icon name="x" [size]="20"></lucide-icon>
        </button>
      </div>

      <div class="flex-1 overflow-y-auto p-6">
        @if (!accountsLoading && accounts.length === 0 && requiresAccountSelection()) {
          <div class="mb-4 rounded-2xl border border-amber-200 bg-amber-50 p-4 text-sm text-amber-800">
            No hay cuentas disponibles para esta operación.
          </div>
        }

        @if (accountsLoading && requiresAccountSelection()) {
          <div class="mb-4 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] p-4 text-sm text-[#567157]">
            Cargando cuentas disponibles...
          </div>
        }

        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-5">
          @if (requiresQrIntentId()) {
            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#1B5E20]">QR escaneado, enlace o ID</label>
              <input
                type="text"
                formControlName="qrTransactionId"
                placeholder="Pega el payload QR o el identificador"
                class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white" />
              <p class="text-xs text-[#6B7D6C]">Puedes pegar el texto del QR escaneado o el ID directo de la intención.</p>
            </div>
          }

          @if (requiresSourceAccountSelection()) {
            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#1B5E20]">Cuenta origen</label>
              <select
                formControlName="sourceAccountId"
                [disabled]="accountsLoading || accounts.length === 0"
                class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white">
                <option value="">Selecciona una cuenta</option>
                <option *ngFor="let acc of accounts" [value]="acc.id">
                  {{ acc.accountNumber }} - {{ acc.customAlias || acc.accountNameLabel }} ({{ acc.currency }})
                </option>
              </select>
            </div>
          }

          @if (requiresTargetAccountSelection()) {
            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#1B5E20]">Cuenta destino</label>
              <select
                formControlName="targetAccountId"
                [disabled]="accountsLoading || accounts.length === 0"
                class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white">
                <option value="">Selecciona una cuenta destino</option>
                <option *ngFor="let acc of accounts" [value]="acc.id">
                  {{ acc.accountNumber }} - {{ acc.customAlias || acc.accountNameLabel }} ({{ acc.currency }})
                </option>
              </select>
            </div>
          }

          @if (requiresTransferDestinationInput()) {
            <div class="space-y-3">
              <div class="space-y-2">
                <label class="text-sm font-semibold text-[#1B5E20]">Número de cuenta destino</label>
                <div class="flex gap-2">
                  <input
                    type="text"
                    formControlName="targetAccountNumber"
                    (input)="clearResolvedTargetAccount()"
                    placeholder="Ingresa el número de cuenta"
                    class="flex h-11 min-w-0 flex-1 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white" />
                  <button
                    type="button"
                    (click)="lookupTransferTargetAccount()"
                    [disabled]="isResolvingTargetAccount || !form.get('targetAccountNumber')?.value"
                    class="inline-flex cursor-pointer items-center justify-center rounded-2xl border border-[#DDEED8] bg-white px-4 py-2 text-sm font-semibold text-[#1B5E20] transition-colors hover:bg-[#F7FBF3] disabled:cursor-not-allowed disabled:opacity-50">
                    @if (isResolvingTargetAccount) {
                      <svg class="h-4 w-4 animate-spin text-[#2E7D32]" viewBox="0 0 24 24">
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                      </svg>
                    } @else {
                      Verificar
                    }
                  </button>
                </div>
                <p class="text-xs text-[#6B7D6C]">Escribe el número de cuenta del destinatario. No se muestran IDs técnicos.</p>
              </div>

              @if (resolvedTargetAccount) {
                <div class="rounded-2xl border border-[#DDEED8] bg-[#F7FBF3] p-4">
                  <p class="text-xs font-semibold uppercase tracking-[0.2em] text-[#6B7D6C]">Cuenta resuelta</p>
                  <div class="mt-2 space-y-1">
                    <p class="text-sm font-semibold text-[#1B5E20]">{{ resolvedTargetAccount.displayName }}</p>
                    <p class="text-xs text-[#6B7D6C]">{{ resolvedTargetAccount.accountNumber }} · {{ resolvedTargetAccount.currency }} · {{ accountStatusLabel(resolvedTargetAccount.status) }}</p>
                  </div>
                </div>
              } @else if (transferLookupError) {
                <div class="rounded-2xl border border-amber-200 bg-amber-50 p-4 text-sm text-amber-800">
                  {{ transferLookupError }}
                </div>
              }
            </div>
          }

          @if (requiresSingleAccountSelection()) {
            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#1B5E20]">Cuenta</label>
              <select
                formControlName="accountId"
                [disabled]="accountsLoading || accounts.length === 0"
                class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white">
                <option value="">Selecciona una cuenta</option>
                <option *ngFor="let acc of accounts" [value]="acc.id">
                  {{ acc.accountNumber }} - {{ acc.customAlias || acc.accountNameLabel }} ({{ acc.currency }})
                </option>
              </select>
            </div>
          }

          @if (requiresAmountAndCurrency()) {
            <div class="grid grid-cols-3 gap-4">
              <div class="col-span-2 space-y-2">
                <label class="text-sm font-semibold text-[#1B5E20]">Monto</label>
                <input
                  type="number"
                  formControlName="amount"
                  placeholder="0.00"
                  min="0.01"
                  step="0.01"
                  class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white" />
              </div>
              <div class="space-y-2">
                <label class="text-sm font-semibold text-[#1B5E20]">Moneda</label>
                <select
                  formControlName="currency"
                  class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white">
                  <option value="USD">USD</option>
                  <option value="BOB">BOB</option>
                  <option value="EUR">EUR</option>
                </select>
              </div>
            </div>
          }

          @if (requiresMethodSelection()) {
            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#1B5E20]">Método</label>
              <select
                formControlName="method"
                class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white">
                <option value="CASHBOX">Sucursal (Caja)</option>
                <option value="MANUAL">Manual</option>
                <option value="API">API</option>
              </select>
            </div>
          }

          @if (requiresDirectionSelection()) {
            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#1B5E20]">Dirección</label>
              <select
                formControlName="direction"
                class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white">
                <option value="CREDIT">Crédito</option>
                <option value="DEBIT">Débito</option>
              </select>
            </div>
          }

          @if (requiresReasonField()) {
            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#1B5E20]">Motivo</label>
              <textarea
                formControlName="reason"
                rows="3"
                placeholder="Describe por qué se realizará la operación"
                class="flex w-full resize-none rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white"></textarea>
            </div>
          } @else if (requiresDescriptionField()) {
            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#1B5E20]">Descripción</label>
              <textarea
                formControlName="description"
                rows="3"
                placeholder="Motivo o detalle de la operación"
                class="flex w-full resize-none rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white"></textarea>
            </div>
          }

          @if (requiresExternalReferenceField()) {
            <div class="space-y-2">
              <label class="text-sm font-semibold text-[#1B5E20]">Referencia externa (opcional)</label>
              <input
                type="text"
                formControlName="externalReference"
                placeholder="Ej. ID de comprobante"
                class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white" />
            </div>
          }
        </form>
      </div>

      <div class="flex justify-end gap-3 border-t border-[#E8F2E2] bg-[#FAFCF8] p-6">
        <button
          type="button"
          (click)="close()"
          class="inline-flex cursor-pointer items-center justify-center rounded-full border border-[#DDEED8] bg-white px-4 py-2 text-sm font-semibold text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
          Cancelar
        </button>
        <button
          type="button"
          (click)="onSubmit()"
          [disabled]="form.invalid || isSubmitting"
          class="inline-flex cursor-pointer items-center justify-center gap-2 rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-semibold text-white shadow-sm transition-colors hover:bg-[#256428] disabled:opacity-50">
          <lucide-icon *ngIf="!isSubmitting" name="send" [size]="16"></lucide-icon>
          <svg *ngIf="isSubmitting" class="h-4 w-4 animate-spin text-white" viewBox="0 0 24 24">
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
  @Input() accounts: AccountOwnerResponse[] = [];
  @Input() accountsLoading = false;
  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<TransactionSlideOverSaveEvent>();

  private readonly fb = inject(FormBuilder);
  private readonly accountsService = inject(AccountsService);

  form!: FormGroup;
  isSubmitting = false;
  isResolvingTargetAccount = false;
  resolvedTargetAccount: AccountLookupResponse | null = null;
  transferLookupError: string | null = null;

  get headerConfig(): HeaderConfig {
    switch (this.transactionType) {
      case 'deposit':
        return { title: 'Depósito', description: 'Abonar fondos a una cuenta', icon: 'arrow-down-to-line' };
      case 'withdrawal':
        return { title: 'Retiro', description: 'Debitar fondos de una cuenta', icon: 'arrow-up-from-line' };
      case 'transfer':
        return { title: 'Transferencia', description: 'Mover fondos entre cuentas', icon: 'arrow-right-left' };
      case 'payment':
        return { title: 'Pago', description: 'Registrar un pago', icon: 'send' };
      case 'fee':
        return { title: 'Comisión', description: 'Registrar un cobro por comisión', icon: 'credit-card' };
      case 'hold':
        return { title: 'Retención', description: 'Bloquear fondos temporalmente', icon: 'alert-triangle' };
      case 'release':
        return { title: 'Liberación', description: 'Liberar fondos retenidos', icon: 'rotate-ccw' };
      case 'adjustment':
        return { title: 'Ajuste', description: 'Ajustar el saldo con una dirección explícita', icon: 'arrow-right-left' };
      case 'qr-intent':
        return { title: 'Cobro por QR', description: 'Generar un QR para cobrar a otro usuario', icon: 'send' };
      case 'qr-confirm':
        return { title: 'Pago por QR', description: 'Escanear o pegar un QR para confirmar el pago', icon: 'check-circle' };
      default:
        return { title: 'Transacción', description: '', icon: 'send' };
    }
  }

  ngOnInit(): void {
    this.initForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['transactionType'] || changes['isOpen']) {
      if (this.isOpen) {
        this.initForm();
        this.isSubmitting = false;
      }
    }
  }

  initForm(): void {
    const requiresAmount = this.requiresAmountAndCurrency();
    this.resolvedTargetAccount = null;
    this.transferLookupError = null;

    this.form = this.fb.group({
      sourceAccountId: [this.requiresSourceAccountSelection() ? '' : ''],
      targetAccountId: [this.requiresTargetAccountSelection() ? '' : ''],
      targetAccountNumber: [this.requiresTransferDestinationInput() ? '' : ''],
      accountId: [this.requiresSingleAccountSelection() ? '' : ''],
      qrTransactionId: [this.requiresQrIntentId() ? '' : ''],
      amount: [requiresAmount ? '' : '', requiresAmount ? [Validators.required, Validators.min(0.01)] : []],
      currency: [requiresAmount ? 'BOB' : 'BOB', requiresAmount ? [Validators.required] : []],
      method: [this.requiresMethodSelection() ? 'CASHBOX' : 'CASHBOX', this.requiresMethodSelection() ? [Validators.required] : []],
      direction: [this.requiresDirectionSelection() ? 'CREDIT' : 'CREDIT', this.requiresDirectionSelection() ? [Validators.required] : []],
      reason: [this.requiresReasonField() ? '' : ''],
      description: [this.requiresDescriptionField() ? '' : ''],
      externalReference: ['']
    });

    if (this.requiresSourceAccountSelection()) {
      this.form.get('sourceAccountId')?.setValidators([Validators.required]);
    }
    if (this.requiresTargetAccountSelection()) {
      this.form.get('targetAccountId')?.setValidators([Validators.required]);
    }
    if (this.requiresTransferDestinationInput()) {
      this.form.get('targetAccountNumber')?.setValidators([Validators.required]);
    }
    if (this.requiresSingleAccountSelection()) {
      this.form.get('accountId')?.setValidators([Validators.required]);
    }
    if (this.requiresQrIntentId()) {
      this.form.get('qrTransactionId')?.setValidators([Validators.required]);
    }
    if (this.requiresReasonField()) {
      this.form.get('reason')?.setValidators([Validators.required]);
    }

    Object.values(this.form.controls).forEach((control) => control.updateValueAndValidity({ emitEvent: false }));
  }

  close(): void {
    this.isOpen = false;
    this.closed.emit();
  }

  async onSubmit(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;

    try {
      const payload = await this.buildRequest();
      this.saved.emit(payload);
    } catch (error: any) {
      const message = error?.error?.message || error?.message || 'No se pudo procesar la solicitud';
      if (this.requiresTransferDestinationInput()) {
        this.transferLookupError = message;
      }
    } finally {
      this.isSubmitting = false;
    }
  }

  private async buildRequest(): Promise<TransactionSlideOverSaveEvent> {
    const value = this.form.getRawValue();
    const baseIdempotencyKey = this.buildIdempotencyKey();

    switch (this.transactionType) {
      case 'deposit':
        return {
          type: this.transactionType,
          request: this.cleanPayload({
            targetAccountId: value.targetAccountId,
            amount: Number(value.amount),
            currency: value.currency,
            method: value.method,
            description: value.description,
            externalReference: value.externalReference,
            idempotencyKey: baseIdempotencyKey
          })
        };
      case 'withdrawal':
        return {
          type: this.transactionType,
          request: this.cleanPayload({
            sourceAccountId: value.sourceAccountId,
            amount: Number(value.amount),
            currency: value.currency,
            method: value.method,
            description: value.description,
            externalReference: value.externalReference,
            idempotencyKey: baseIdempotencyKey
          })
        };
      case 'transfer':
        if (!value.targetAccountId) {
          await this.lookupTransferTargetAccount();
        }

        if (!this.resolvedTargetAccount?.id) {
          throw new Error('Debes verificar un número de cuenta destino válido antes de transferir.');
        }

        if (!this.resolvedTargetAccount.active) {
          throw new Error('La cuenta destino no está activa para recibir transferencias.');
        }

        return {
          type: this.transactionType,
          request: this.cleanPayload({
            sourceAccountId: value.sourceAccountId,
            targetAccountId: this.resolvedTargetAccount.id,
            amount: Number(value.amount),
            currency: value.currency,
            description: value.description,
            externalReference: value.externalReference,
            idempotencyKey: baseIdempotencyKey
          })
        };
      case 'payment':
        return {
          type: this.transactionType,
          request: this.cleanPayload({
            sourceAccountId: value.sourceAccountId,
            amount: Number(value.amount),
            currency: value.currency,
            method: value.method,
            description: value.description,
            externalReference: value.externalReference,
            idempotencyKey: baseIdempotencyKey
          })
        };
      case 'fee':
        return {
          type: this.transactionType,
          request: this.cleanPayload({
            accountId: value.accountId,
            amount: Number(value.amount),
            currency: value.currency,
            description: value.description,
            externalReference: value.externalReference,
            idempotencyKey: baseIdempotencyKey
          })
        };
      case 'hold':
      case 'release':
        return {
          type: this.transactionType,
          request: this.cleanPayload({
            accountId: value.accountId,
            amount: Number(value.amount),
            currency: value.currency,
            description: value.description,
            idempotencyKey: baseIdempotencyKey
          })
        };
      case 'adjustment':
        return {
          type: this.transactionType,
          request: this.cleanPayload({
            accountId: value.accountId,
            amount: Number(value.amount),
            currency: value.currency,
            direction: value.direction,
            reason: value.reason,
            externalReference: value.externalReference,
            idempotencyKey: baseIdempotencyKey
          })
        };
      case 'qr-intent':
        return {
          type: this.transactionType,
          request: this.cleanPayload({
            targetAccountId: value.targetAccountId,
            amount: Number(value.amount),
            currency: value.currency,
            description: value.description,
            externalReference: value.externalReference,
            idempotencyKey: baseIdempotencyKey
          })
        };
      case 'qr-confirm':
        {
          const qrId = this.extractQrIntentId(value.qrTransactionId);
          if (!qrId) {
            throw new Error('Falta el identificador o payload de la intención QR');
          }

        return {
          type: this.transactionType,
          referenceId: qrId,
          request: this.cleanPayload({
            sourceAccountId: value.sourceAccountId,
            idempotencyKey: baseIdempotencyKey
          })
        };
        }
      default:
        return { type: this.transactionType, request: { idempotencyKey: baseIdempotencyKey } };
    }
  }

  private cleanPayload<T extends Record<string, unknown>>(payload: T): T {
    return Object.entries(payload).reduce((acc, [key, value]) => {
      if (value !== '' && value !== null && value !== undefined) {
        acc[key as keyof T] = value as T[keyof T];
      }
      return acc;
    }, {} as T);
  }

  private buildIdempotencyKey(): string {
    return globalThis.crypto?.randomUUID?.() ?? `tx-${Date.now()}-${Math.random().toString(16).slice(2)}`;
  }

  clearResolvedTargetAccount(): void {
    if (!this.requiresTransferDestinationInput()) {
      return;
    }

    this.resolvedTargetAccount = null;
    this.transferLookupError = null;
    this.form.get('targetAccountId')?.setValue('');
  }

  async lookupTransferTargetAccount(): Promise<boolean> {
    if (!this.requiresTransferDestinationInput()) {
      return false;
    }

    const accountNumber = String(this.form.get('targetAccountNumber')?.value || '').trim();
    if (!accountNumber) {
      this.transferLookupError = 'Ingresa un número de cuenta destino.';
      this.resolvedTargetAccount = null;
      this.form.get('targetAccountId')?.setValue('');
      return false;
    }

    this.isResolvingTargetAccount = true;
    this.transferLookupError = null;

    try {
      const response = await firstValueFrom(this.accountsService.resolveMyAccountByNumber(accountNumber));
      if (!response.success || !response.data) {
        throw new Error(response.message || 'No se pudo resolver la cuenta destino.');
      }

      this.resolvedTargetAccount = response.data;
      this.form.get('targetAccountId')?.setValue(response.data.id);

      if (!response.data.active) {
        this.transferLookupError = 'La cuenta destino está inactiva y no debería usarse para transferencias.';
      }
      return true;
    } catch (error: any) {
      this.resolvedTargetAccount = null;
      this.form.get('targetAccountId')?.setValue('');
      this.transferLookupError = error?.error?.message || error?.message || 'No se pudo encontrar la cuenta destino.';
      return false;
    } finally {
      this.isResolvingTargetAccount = false;
    }
  }

  accountStatusLabel(status: string): string {
    switch (status) {
      case 'ACTIVE':
        return 'Activa';
      case 'PENDING_APPROVAL':
        return 'Pendiente de aprobación';
      case 'PENDING_VERIFICATION':
        return 'Pendiente de verificación';
      case 'FROZEN':
        return 'Congelada';
      case 'BLOCKED':
        return 'Bloqueada';
      case 'SUSPENDED':
        return 'Suspendida';
      case 'CLOSED':
        return 'Cerrada';
      default:
        return status || 'Desconocida';
    }
  }

  requiresAccountSelection(): boolean {
    return this.requiresSourceAccountSelection() || this.requiresTargetAccountSelection() || this.requiresSingleAccountSelection();
  }

  requiresSourceAccountSelection(): boolean {
    return ['withdrawal', 'transfer', 'payment', 'qr-confirm'].includes(this.transactionType);
  }

  requiresTargetAccountSelection(): boolean {
    return ['deposit', 'qr-intent'].includes(this.transactionType);
  }

  requiresTransferDestinationInput(): boolean {
    return this.transactionType === 'transfer';
  }

  requiresSingleAccountSelection(): boolean {
    return ['fee', 'hold', 'release', 'adjustment'].includes(this.transactionType);
  }

  requiresAmountAndCurrency(): boolean {
    return this.transactionType !== 'qr-confirm';
  }

  requiresMethodSelection(): boolean {
    return ['deposit', 'withdrawal', 'payment'].includes(this.transactionType);
  }

  requiresDirectionSelection(): boolean {
    return this.transactionType === 'adjustment';
  }

  requiresReasonField(): boolean {
    return this.transactionType === 'adjustment';
  }

  requiresDescriptionField(): boolean {
    return this.transactionType !== 'qr-confirm' && this.transactionType !== 'adjustment';
  }

  requiresExternalReferenceField(): boolean {
    return ['deposit', 'withdrawal', 'transfer', 'payment', 'fee', 'adjustment', 'qr-intent'].includes(this.transactionType);
  }

  requiresQrIntentId(): boolean {
    return this.transactionType === 'qr-confirm';
  }

  private extractQrIntentId(value: unknown): string {
    if (typeof value !== 'string') {
      return '';
    }

    const raw = value.trim();
    if (!raw) {
      return '';
    }

    if (/^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i.test(raw)) {
      return raw;
    }

    try {
      const url = new URL(raw);
      const fromUrl = url.searchParams.get('intentId') || url.searchParams.get('id');
      if (fromUrl) {
        return fromUrl.trim();
      }
    } catch {
      // Fallback below.
    }

    const match = raw.match(/(?:[?&]intentId=)([^&]+)/i);
    if (match?.[1]) {
      return decodeURIComponent(match[1]).trim();
    }

    return raw;
  }
}

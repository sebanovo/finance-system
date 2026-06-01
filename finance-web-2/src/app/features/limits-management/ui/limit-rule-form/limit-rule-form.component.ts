import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LimitRuleResponse, CreateLimitRuleRequest, UpdateLimitRuleRequest } from '../../../../entities/limits';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-limit-rule-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <div class="fixed inset-0 z-50 flex items-center justify-end">
      <!-- Backdrop -->
      <div class="absolute inset-0 bg-background/80 backdrop-blur-sm animate-in fade-in" (click)="close()"></div>
      
      <!-- Slide-over Panel -->
      <div class="relative bg-card w-full max-w-2xl h-full shadow-2xl border-l border-border flex flex-col animate-in slide-in-from-right duration-300">
        
        <div class="flex items-center justify-between px-6 py-4 border-b border-border bg-card">
          <h3 class="text-lg font-semibold text-foreground">
            {{ isEditing ? 'Editar' : 'Nueva' }} Regla de Límite
          </h3>
          <button type="button" (click)="close()" class="text-muted-foreground hover:text-foreground transition-colors p-1 rounded-md hover:bg-muted" [disabled]="loading">
            <lucide-icon name="x" [size]="20"></lucide-icon>
          </button>
        </div>

        <div class="flex-1 overflow-y-auto p-6">
          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-6">
            
            <div class="space-y-6">
              
              <div class="grid grid-cols-2 gap-4">
                <!-- Nombre -->
                <div class="col-span-2 sm:col-span-1">
                  <label class="block text-sm font-medium mb-1 text-foreground">Nombre de Regla</label>
                  <input 
                    type="text" 
                    formControlName="name"
                    placeholder="ej. Límite Diario Transferencias"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow">
                  @if (form.get('name')?.invalid && form.get('name')?.touched) {
                    <span class="text-xs text-destructive mt-1 block">El nombre es requerido.</span>
                  }
                </div>

                <!-- Código -->
                <div class="col-span-2 sm:col-span-1">
                  <label class="block text-sm font-medium mb-1 text-foreground">Código Único</label>
                  <input 
                    type="text" 
                    formControlName="code"
                    placeholder="ej. LMT-D-TRF"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow uppercase disabled:opacity-50"
                    [attr.disabled]="isEditing ? true : null">
                  @if (form.get('code')?.invalid && form.get('code')?.touched) {
                    <span class="text-xs text-destructive mt-1 block">El código es requerido.</span>
                  }
                </div>
              </div>

              <!-- Descripción -->
              <div>
                <label class="block text-sm font-medium mb-1 text-foreground">Descripción (Opcional)</label>
                <textarea 
                  formControlName="description"
                  rows="2"
                  placeholder="Detalles de la regla..."
                  class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow resize-none"></textarea>
              </div>

              <div class="h-px w-full bg-border"></div>

              <div class="grid grid-cols-2 gap-4">
                <!-- Tipo de Regla -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Tipo de Regla (Limit Type)</label>
                  <select 
                    formControlName="limitType"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                    [attr.disabled]="isEditing ? true : null">
                    <option value="">Seleccione...</option>
                    <option value="PER_TRANSACTION_AMOUNT">Monto por Transacción</option>
                    <option value="DAILY_AMOUNT">Monto Diario Acumulado</option>
                    <option value="MONTHLY_AMOUNT">Monto Mensual Acumulado</option>
                    <option value="DAILY_COUNT">Cantidad Diaria de Tx</option>
                    <option value="MONTHLY_COUNT">Cantidad Mensual de Tx</option>
                    <option value="MIN_AMOUNT">Monto Mínimo</option>
                    <option value="MAX_BALANCE">Balance Máximo</option>
                  </select>
                  @if (form.get('limitType')?.invalid && form.get('limitType')?.touched) {
                    <span class="text-xs text-destructive mt-1 block">Requerido.</span>
                  }
                </div>

                <!-- Alcance -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Alcance (Scope)</label>
                  <select 
                    formControlName="scopeType"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                    [attr.disabled]="isEditing ? true : null">
                    <option value="">Seleccione...</option>
                    <option value="TENANT">Global (Tenant)</option>
                    <option value="ACCOUNT_TYPE">Por Tipo de Cuenta</option>
                    <option value="TRANSACTION_TYPE">Por Tipo de Transacción</option>
                    <option value="USER">Por Usuario</option>
                    <option value="ACCOUNT">Por Cuenta Específica</option>
                  </select>
                  @if (form.get('scopeType')?.invalid && form.get('scopeType')?.touched) {
                    <span class="text-xs text-destructive mt-1 block">Requerido.</span>
                  }
                </div>

                <!-- Periodo -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Periodo de Evaluación</label>
                  <select 
                    formControlName="period"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                    [attr.disabled]="isEditing ? true : null">
                    <option value="">Seleccione...</option>
                    <option value="TRANSACTION">Por Transacción</option>
                    <option value="DAILY">Diario</option>
                    <option value="MONTHLY">Mensual</option>
                  </select>
                  @if (form.get('period')?.invalid && form.get('period')?.touched) {
                    <span class="text-xs text-destructive mt-1 block">Requerido.</span>
                  }
                </div>

                <!-- Moneda (Opcional) -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Moneda (Opcional)</label>
                  <select 
                    formControlName="currency"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                    [attr.disabled]="isEditing ? true : null">
                    <option value="">Cualquiera</option>
                    <option value="USD">USD - Dólar</option>
                    <option value="BOB">BOB - Boliviano</option>
                    <option value="EUR">EUR - Euro</option>
                    <option value="USDT">USDT - Tether</option>
                  </select>
                </div>

                <!-- Tipo Transacción (Opcional) -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Tipo de Transacción (Opcional)</label>
                  <select 
                    formControlName="transactionType"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                    [attr.disabled]="isEditing ? true : null">
                    <option value="">Cualquiera</option>
                    <option value="DEPOSIT">Depósito</option>
                    <option value="WITHDRAWAL">Retiro</option>
                    <option value="TRANSFER">Transferencia</option>
                    <option value="PAYMENT">Pago</option>
                    <option value="CONVERSION">Conversión</option>
                  </select>
                </div>

                <!-- Tipo de Cuenta (Opcional) -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Tipo de Cuenta (Opcional)</label>
                  <select 
                    formControlName="accountType"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                    [attr.disabled]="isEditing ? true : null">
                    <option value="">Cualquiera</option>
                    <option value="SAVINGS">Ahorros</option>
                    <option value="CHECKING">Corriente</option>
                    <option value="INVESTMENT">Inversión</option>
                  </select>
                </div>
              </div>

              <div class="h-px w-full bg-border"></div>

              <div class="grid grid-cols-3 gap-4">
                <!-- Monto Mínimo -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Monto Mín.</label>
                  <input 
                    type="number" 
                    step="0.01"
                    formControlName="minAmount"
                    placeholder="0.00"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow">
                </div>

                <!-- Monto Máximo -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Monto Máx.</label>
                  <input 
                    type="number" 
                    step="0.01"
                    formControlName="maxAmount"
                    placeholder="0.00"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow">
                </div>

                <!-- Conteo Máximo -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Conteo Máx. (Tx)</label>
                  <input 
                    type="number" 
                    formControlName="maxCount"
                    placeholder="0"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow">
                </div>
              </div>

              @if (form.hasError('invalidRange')) {
                <div class="text-xs text-destructive p-3 bg-destructive/10 rounded-md border border-destructive/20 flex gap-2">
                  <lucide-icon name="x" [size]="16" class="shrink-0"></lucide-icon>
                  <span>El monto máximo debe ser mayor o igual al monto mínimo.</span>
                </div>
              }

              <div class="flex flex-col gap-3 pt-2">
                <!-- Active Toggle -->
                <div class="flex items-center gap-2">
                  <input 
                    type="checkbox" 
                    id="activeRule" 
                    formControlName="active"
                    class="h-4 w-4 rounded border-border text-primary focus:ring-primary/50 bg-background accent-primary">
                  <label for="activeRule" class="text-sm font-medium text-foreground cursor-pointer">
                    Regla Activa
                  </label>
                </div>
                
                <!-- Require Review Toggle -->
                <div class="flex items-center gap-2">
                  <input 
                    type="checkbox" 
                    id="requireReview" 
                    formControlName="requireReviewExceed"
                    class="h-4 w-4 rounded border-border text-primary focus:ring-primary/50 bg-background accent-primary">
                  <label for="requireReview" class="text-sm font-medium text-foreground cursor-pointer">
                    Requerir Revisión Manual al Exceder
                  </label>
                </div>
              </div>
              
            </div>

            <div class="pt-4 flex justify-end gap-3 border-t border-border mt-6">
              <button 
                type="button" 
                (click)="close()"
                class="px-4 py-2 border border-border bg-background hover:bg-muted text-foreground rounded-md text-sm font-medium transition-colors"
                [disabled]="loading">
                Cancelar
              </button>
              
              <button 
                type="submit" 
                class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2"
                [disabled]="form.invalid || loading">
                @if (loading) {
                  <svg class="animate-spin h-4 w-4 text-primary-foreground" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Guardando...
                } @else {
                  <lucide-icon name="save" [size]="16"></lucide-icon>
                  {{ isEditing ? 'Guardar Cambios' : 'Crear Regla' }}
                }
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `
})
export class LimitRuleFormComponent implements OnInit {
  @Input() rule: LimitRuleResponse | null = null;
  @Input() loading = false;
  
  @Output() formSubmit = new EventEmitter<{ type: 'create' | 'update', data: any }>();
  @Output() cancel = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  
  form!: FormGroup;
  isEditing = false;

  ngOnInit() {
    this.isEditing = !!this.rule;
    
    this.form = this.fb.group({
      code: [{ value: this.rule?.code || '', disabled: this.isEditing }, [Validators.required]],
      name: [this.rule?.name || '', [Validators.required]],
      description: [this.rule?.description || ''],
      limitType: [{ value: this.rule?.limitType || '', disabled: this.isEditing }, [Validators.required]],
      scopeType: [{ value: this.rule?.scopeType || '', disabled: this.isEditing }, [Validators.required]],
      period: [{ value: this.rule?.period || '', disabled: this.isEditing }, [Validators.required]],
      transactionType: [{ value: this.rule?.transactionType || '', disabled: this.isEditing }],
      accountType: [{ value: this.rule?.accountType || '', disabled: this.isEditing }],
      currency: [{ value: this.rule?.currency || '', disabled: this.isEditing }],
      minAmount: [this.rule?.minAmount ?? null, [Validators.min(0)]],
      maxAmount: [this.rule?.maxAmount ?? null, [Validators.min(0)]],
      maxCount: [this.rule?.maxCount ?? null, [Validators.min(1)]],
      active: [this.rule?.active ?? true],
      requireReviewExceed: [this.rule?.requireReviewExceed ?? false]
    }, { validators: this.rangeValidator });
  }

  private rangeValidator(group: FormGroup) {
    const min = group.get('minAmount')?.value;
    const max = group.get('maxAmount')?.value;
    if (min !== null && max !== null && min > max) {
      return { invalidRange: true };
    }
    return null;
  }

  onSubmit() {
    if (this.form.invalid) return;

    const formValues = this.form.getRawValue();

    if (this.isEditing) {
      const updateReq: UpdateLimitRuleRequest = {
        name: formValues.name,
        description: formValues.description,
        minAmount: formValues.minAmount,
        maxAmount: formValues.maxAmount,
        maxCount: formValues.maxCount,
        active: formValues.active,
        requireReviewExceed: formValues.requireReviewExceed
      };
      this.formSubmit.emit({ type: 'update', data: updateReq });
    } else {
      const createReq: CreateLimitRuleRequest = {
        code: formValues.code.toUpperCase(),
        name: formValues.name,
        description: formValues.description,
        limitType: formValues.limitType,
        scopeType: formValues.scopeType,
        period: formValues.period,
        transactionType: formValues.transactionType || undefined,
        accountType: formValues.accountType || undefined,
        currency: formValues.currency || undefined,
        minAmount: formValues.minAmount,
        maxAmount: formValues.maxAmount,
        maxCount: formValues.maxCount,
        active: formValues.active,
        requireReviewExceed: formValues.requireReviewExceed
      };
      this.formSubmit.emit({ type: 'create', data: createReq });
    }
  }

  close() {
    if (!this.loading) {
      this.cancel.emit();
    }
  }
}

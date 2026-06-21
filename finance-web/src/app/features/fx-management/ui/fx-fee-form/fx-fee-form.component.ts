import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { OperationFeeResponse, CreateOperationFeeRequest, UpdateOperationFeeRequest } from '../../../../entities/fx';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-fx-fee-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <div class="fixed inset-0 z-50 flex items-center justify-end">
      <div class="absolute inset-0 bg-background/80 backdrop-blur-sm animate-in fade-in" (click)="close()"></div>
      
      <div class="relative bg-card w-full max-w-md h-full shadow-2xl border-l border-border flex flex-col animate-in slide-in-from-right duration-300">
        <div class="flex items-center justify-between px-6 py-4 border-b border-border bg-card">
          <h3 class="text-lg font-semibold text-foreground">
            {{ isEditing ? 'Editar' : 'Nueva' }} Comisión
          </h3>
          <button type="button" (click)="close()" class="cursor-pointer text-muted-foreground hover:text-foreground transition-colors p-1 rounded-md hover:bg-muted" [disabled]="loading">
            <lucide-icon name="x" [size]="20"></lucide-icon>
          </button>
        </div>

        <div class="flex-1 overflow-y-auto p-6">
          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-6">
            
            <div class="space-y-4">
              <!-- Operation Code -->
              <div>
                <label class="block text-sm font-medium mb-1 text-foreground">Código de Operación</label>
                <select 
                  formControlName="operationCode"
                  class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                  [attr.disabled]="isEditing ? true : null">
                  <option value="">Seleccione...</option>
                  <option value="DEPOSIT">Depósito (DEPOSIT)</option>
                  <option value="WITHDRAWAL">Retiro (WITHDRAWAL)</option>
                  <option value="TRANSFER">Transferencia (TRANSFER)</option>
                  <option value="PAYMENT">Pago (PAYMENT)</option>
                  <option value="CONVERSION">Conversión (CONVERSION)</option>
                </select>
                @if (form.get('operationCode')?.invalid && form.get('operationCode')?.touched) {
                  <span class="text-xs text-destructive mt-1 block">El código de operación es requerido.</span>
                }
              </div>

              <div class="grid grid-cols-2 gap-4">
                <!-- Fee Type -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Tipo de Comisión</label>
                  <select 
                    formControlName="feeType"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                    [attr.disabled]="isEditing ? true : null">
                    <option value="">Seleccione...</option>
                    <option value="NONE">Sin Comisión (NONE)</option>
                    <option value="FIXED">Fija (Monto exacto)</option>
                    <option value="PERCENTAGE">Porcentaje (%)</option>
                  </select>
                  @if (form.get('feeType')?.invalid && form.get('feeType')?.touched) {
                    <span class="text-xs text-destructive mt-1 block">Requerido.</span>
                  }
                </div>

                <!-- Fee Value -->
                <div>
                  <label class="block text-sm font-medium mb-1 text-foreground">Valor (Monto / %)</label>
                  <input 
                    type="number" 
                    step="0.01"
                    formControlName="feeValue"
                    placeholder="0.00"
                    class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow">
                  @if (form.get('feeValue')?.invalid && form.get('feeValue')?.touched) {
                    <span class="text-xs text-destructive mt-1 block">Mayor o igual a 0.</span>
                  }
                </div>
              </div>

              <!-- Calculation Mode -->
              <div>
                <label class="block text-sm font-medium mb-1 text-foreground">Modo de Cálculo</label>
                <select 
                  formControlName="calculationMode"
                  class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow">
                  <option value="">Seleccione...</option>
                  <option value="SEPARATE">Separado (SEPARATE - Se suma al monto)</option>
                  <option value="INCLUDED">Incluido (INCLUDED - Se deduce del monto)</option>
                </select>
                @if (form.get('calculationMode')?.invalid && form.get('calculationMode')?.touched) {
                  <span class="text-xs text-destructive mt-1 block">El modo es requerido.</span>
                }
              </div>

              <!-- Description -->
              <div>
                <label class="block text-sm font-medium mb-1 text-foreground">Descripción (Opcional)</label>
                <textarea 
                  formControlName="description"
                  rows="2"
                  placeholder="Información adicional..."
                  class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow resize-none"></textarea>
              </div>

              <!-- Active Toggle -->
              <div class="flex items-center gap-2 pt-2">
                <input 
                  type="checkbox" 
                  id="activeFee" 
                  formControlName="active"
                  class="h-4 w-4 rounded border-border text-primary focus:ring-primary/50 bg-background accent-primary">
                <label for="activeFee" class="text-sm font-medium text-foreground cursor-pointer">
                  Comisión Activa
                </label>
              </div>
            </div>

            @if (form.hasError('zeroFeeRequired')) {
              <div class="text-xs text-destructive mt-4 p-3 bg-destructive/10 rounded-md border border-destructive/20 flex gap-2">
                <lucide-icon name="x" [size]="16" class="shrink-0"></lucide-icon>
                <span>Si el tipo de comisión es 'Sin Comisión', el valor debe ser exactamente 0.</span>
              </div>
            }

            <div class="pt-4 flex justify-end gap-3 border-t border-border mt-6">
              <button 
                type="button" 
                (click)="close()"
                class="cursor-pointer px-4 py-2 border border-border bg-background hover:bg-muted text-foreground rounded-md text-sm font-medium transition-colors"
                [disabled]="loading">
                Cancelar
              </button>
              
              <button 
                type="submit" 
                class="cursor-pointer bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2"
                [disabled]="form.invalid || loading">
                @if (loading) {
                  <svg class="animate-spin h-4 w-4 text-primary-foreground" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Guardando...
                } @else {
                  <lucide-icon name="save" [size]="16"></lucide-icon>
                  {{ isEditing ? 'Guardar Cambios' : 'Crear Comisión' }}
                }
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `
})
export class FxFeeFormComponent implements OnInit {
  @Input() fee: OperationFeeResponse | null = null;
  @Input() loading = false;
  
  @Output() formSubmit = new EventEmitter<{ type: 'create' | 'update', data: any }>();
  @Output() cancel = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  
  form!: FormGroup;
  isEditing = false;

  ngOnInit() {
    this.isEditing = !!this.fee;
    
    this.form = this.fb.group({
      operationCode: [{ value: this.fee?.operationCode || '', disabled: this.isEditing }, [Validators.required]],
      feeType: [{ value: this.fee?.feeType || '', disabled: this.isEditing }, [Validators.required]],
      feeValue: [this.fee?.feeValue ?? null, [Validators.required, Validators.min(0)]],
      calculationMode: [this.fee?.calculationMode || '', [Validators.required]],
      description: [this.fee?.description || ''],
      active: [this.fee?.active ?? true]
    }, { validators: this.feeZeroValidator });
  }

  private feeZeroValidator(group: FormGroup) {
    const feeType = group.get('feeType')?.value;
    const feeValue = group.get('feeValue')?.value;
    
    if (feeType === 'NONE' && feeValue !== 0) {
      return { zeroFeeRequired: true };
    }
    return null;
  }

  onSubmit() {
    if (this.form.invalid) return;

    const formValues = this.form.getRawValue();

    if (this.isEditing) {
      const updateReq: UpdateOperationFeeRequest = {
        operationCode: formValues.operationCode,
        feeType: formValues.feeType,
        feeValue: formValues.feeValue,
        calculationMode: formValues.calculationMode,
        active: formValues.active,
        description: formValues.description
      };
      this.formSubmit.emit({ type: 'update', data: updateReq });
    } else {
      const createReq: CreateOperationFeeRequest = {
        operationCode: formValues.operationCode,
        feeType: formValues.feeType,
        feeValue: formValues.feeValue,
        calculationMode: formValues.calculationMode,
        active: formValues.active,
        description: formValues.description
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

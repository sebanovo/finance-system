import { Component, EventEmitter, Input, Output, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FxExchangeRateResponse, CreateFxExchangeRateRequest, UpdateFxExchangeRateRequest } from '../../../../entities/fx';
import { LucideAngularModule, X, Save } from 'lucide-angular';

@Component({
  selector: 'app-fx-rate-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <div class="fixed inset-0 z-50 flex items-center justify-end">
      <!-- Backdrop -->
      <div class="absolute inset-0 bg-background/80 backdrop-blur-sm animate-in fade-in" (click)="close()"></div>
      
      <!-- Slide-over Panel -->
      <div class="relative bg-card w-full max-w-md h-full shadow-2xl border-l border-border flex flex-col animate-in slide-in-from-right duration-300">
        
        <div class="flex items-center justify-between px-6 py-4 border-b border-border bg-card">
          <h3 class="text-lg font-semibold text-foreground">
            {{ isEditing ? 'Editar' : 'Nueva' }} Tasa de Cambio
          </h3>
          <button type="button" (click)="close()" class="cursor-pointer text-muted-foreground hover:text-foreground transition-colors p-1 rounded-md hover:bg-muted" [disabled]="loading">
            <lucide-icon name="x" [size]="20"></lucide-icon>
          </button>
        </div>

        <div class="flex-1 overflow-y-auto p-6">
          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-6">
            
            <div class="space-y-4">
              <!-- Source Currency -->
              <div>
                <label class="block text-sm font-medium mb-1 text-foreground">Moneda Origen</label>
                <select 
                  formControlName="sourceCurrency"
                  class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                  [attr.disabled]="isEditing ? true : null">
                  <option value="">Seleccione...</option>
                  <option value="USD">USD - Dólar Estadounidense</option>
                  <option value="BOB">BOB - Boliviano</option>
                  <option value="EUR">EUR - Euro</option>
                  <option value="USDT">USDT - Tether</option>
                </select>
                @if (form.get('sourceCurrency')?.invalid && form.get('sourceCurrency')?.touched) {
                  <span class="text-xs text-destructive mt-1 block">La moneda origen es requerida.</span>
                }
              </div>

              <!-- Target Currency -->
              <div>
                <label class="block text-sm font-medium mb-1 text-foreground">Moneda Destino</label>
                <select 
                  formControlName="targetCurrency"
                  class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow disabled:opacity-50"
                  [attr.disabled]="isEditing ? true : null">
                  <option value="">Seleccione...</option>
                  <option value="USD">USD - Dólar Estadounidense</option>
                  <option value="BOB">BOB - Boliviano</option>
                  <option value="EUR">EUR - Euro</option>
                  <option value="USDT">USDT - Tether</option>
                </select>
                @if (form.get('targetCurrency')?.invalid && form.get('targetCurrency')?.touched) {
                  <span class="text-xs text-destructive mt-1 block">La moneda destino es requerida.</span>
                }
              </div>

              <!-- Rate -->
              <div>
                <label class="block text-sm font-medium mb-1 text-foreground">Tasa (Rate)</label>
                <input 
                  type="number" 
                  step="0.000001"
                  formControlName="rate"
                  placeholder="0.000000"
                  class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow">
                @if (form.get('rate')?.invalid && form.get('rate')?.touched) {
                  <span class="text-xs text-destructive mt-1 block">La tasa es requerida y debe ser mayor a 0.</span>
                }
              </div>

              <!-- Description -->
              <div>
                <label class="block text-sm font-medium mb-1 text-foreground">Descripción (Opcional)</label>
                <textarea 
                  formControlName="description"
                  rows="3"
                  placeholder="Información adicional sobre la tasa..."
                  class="w-full px-3 py-2 bg-background border border-border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 text-foreground transition-shadow resize-none"></textarea>
              </div>

              <!-- Active Toggle -->
              <div class="flex items-center gap-2 pt-2">
                <input 
                  type="checkbox" 
                  id="active" 
                  formControlName="active"
                  class="h-4 w-4 rounded border-border text-primary focus:ring-primary/50 bg-background accent-primary">
                <label for="active" class="text-sm font-medium text-foreground cursor-pointer">
                  Tasa Activa
                </label>
              </div>
            </div>

            @if (form.hasError('selfExchangeRate')) {
              <div class="text-xs text-destructive mt-4 p-3 bg-destructive/10 rounded-md border border-destructive/20 flex gap-2">
                <lucide-icon name="x" [size]="16" class="shrink-0"></lucide-icon>
                <span>Si la moneda origen y destino son la misma, la tasa debe ser exactamente 1.</span>
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
                  {{ isEditing ? 'Guardar Cambios' : 'Crear Tasa' }}
                }
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `
})
export class FxRateFormComponent implements OnInit {
  @Input() rate: FxExchangeRateResponse | null = null;
  @Input() loading = false;
  
  @Output() formSubmit = new EventEmitter<{ type: 'create' | 'update', data: any }>();
  @Output() cancel = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  
  form!: FormGroup;
  isEditing = false;

  ngOnInit() {
    this.isEditing = !!this.rate;
    
    this.form = this.fb.group({
      sourceCurrency: [{ value: this.rate?.sourceCurrency || '', disabled: this.isEditing }, [Validators.required]],
      targetCurrency: [{ value: this.rate?.targetCurrency || '', disabled: this.isEditing }, [Validators.required]],
      rate: [this.rate?.rate || null, [Validators.required, Validators.min(0.000001)]],
      description: [this.rate?.description || ''],
      active: [this.rate?.active ?? true]
    }, { validators: this.selfExchangeRateValidator });
  }

  private selfExchangeRateValidator(group: FormGroup) {
    const source = group.get('sourceCurrency')?.value;
    const target = group.get('targetCurrency')?.value;
    const rate = group.get('rate')?.value;
    
    if (source && target && source === target && rate !== 1) {
      return { selfExchangeRate: true };
    }
    return null;
  }

  onSubmit() {
    if (this.form.invalid) return;

    const formValues = this.form.getRawValue();

    if (this.isEditing) {
      const updateReq: UpdateFxExchangeRateRequest = {
        sourceCurrency: formValues.sourceCurrency,
        targetCurrency: formValues.targetCurrency,
        rate: formValues.rate,
        active: formValues.active,
        description: formValues.description
      };
      this.formSubmit.emit({ type: 'update', data: updateReq });
    } else {
      const createReq: CreateFxExchangeRateRequest = {
        sourceCurrency: formValues.sourceCurrency,
        targetCurrency: formValues.targetCurrency,
        rate: formValues.rate,
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

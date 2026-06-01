import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LucideAngularModule } from 'lucide-angular';

interface TenantPlanOption {
  code: string;
  name: string;
  planType: string;
  active: boolean;
  trialDays: number | null;
}

@Component({
  selector: 'app-platform-tenant-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <div class="bg-white rounded-xl border border-[#C8E6C9] p-6">
      <h2 class="text-xl font-bold text-[#2E7D32] mb-4">Crear nuevo tenant</h2>
      
      <div *ngIf="error" class="mb-4 p-3 bg-red-50 text-red-700 rounded-lg text-sm">
        {{ error }}
      </div>
      
      <form [formGroup]="tenantForm" (ngSubmit)="onSubmit()" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-[#2E7D32] mb-1">Nombre de la empresa</label>
          <input type="text" formControlName="name" placeholder="Ej: Mi Empresa SRL"
                 class="w-full px-3 py-2 border border-[#C8E6C9] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#4CAF50]">
          <span *ngIf="isFieldInvalid('name')" class="text-xs text-red-600 mt-1">El nombre es obligatorio</span>
        </div>
        
        <div>
          <label class="block text-sm font-medium text-[#2E7D32] mb-1">Slug (identificador único)</label>
          <input type="text" formControlName="slug" placeholder="Ej: miempresa"
                 class="w-full px-3 py-2 border border-[#C8E6C9] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#4CAF50]">
          <span *ngIf="isFieldInvalid('slug')" class="text-xs text-red-600 mt-1">
            Solo letras minúsculas, números y guiones. Mínimo 3 caracteres.
          </span>
        </div>
        
        <div>
          <label class="block text-sm font-medium text-[#2E7D32] mb-1">Plan</label>
          <select formControlName="planCode"
                  class="w-full px-3 py-2 border border-[#C8E6C9] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#4CAF50]">
            @if (plans.length === 0) {
              <option value="DEMO">DEMO - Plan por defecto</option>
            } @else {
              @for (plan of plans; track plan.code) {
                <option [value]="plan.code">
                  {{ plan.code }} - {{ plan.name }}{{ plan.active ? '' : ' (Inactivo)' }}
                </option>
              }
            }
          </select>
          @if (plans.length === 0) {
            <p class="mt-1 text-xs text-[#6B7D6C]">No hay planes cargados. Se usará DEMO como respaldo.</p>
          }
        </div>
        
        <div class="flex justify-end gap-3 pt-4">
          <button type="button" (click)="cancel.emit()" 
                  class="cursor-pointer px-4 py-2 border border-[#C8E6C9] text-[#666666] rounded-lg hover:bg-[#F1F8E9] transition-colors">
            Cancelar
          </button>
          <button type="submit" [disabled]="isLoading" 
                  class="cursor-pointer px-4 py-2 bg-[#2E7D32] text-white rounded-lg hover:bg-[#4CAF50] transition-colors disabled:opacity-50">
            {{ isLoading ? 'Creando...' : 'Crear tenant' }}
          </button>
        </div>
      </form>
    </div>
  `
})
export class PlatformTenantFormComponent {
  @Input() isLoading = false;
  @Input() error: string | null = null;
  @Input() plans: TenantPlanOption[] = [];
  @Output() submitForm = new EventEmitter<{ name: string; slug: string; planCode: string }>();
  @Output() cancel = new EventEmitter<void>();

  private fb = inject(FormBuilder);

  tenantForm = this.fb.group({
    name: ['', [Validators.required]],
    slug: ['', [Validators.required, Validators.minLength(3), Validators.pattern(/^[a-z0-9-]+$/)]],
    planCode: ['DEMO', Validators.required]
  });

  ngOnChanges(): void {
    if (this.plans.length > 0) {
      const currentPlanCode = this.tenantForm.get('planCode')?.value;
      const currentPlanExists = this.plans.some(plan => plan.code === currentPlanCode);
      if (!currentPlanExists) {
        this.tenantForm.patchValue({ planCode: this.plans[0].code });
      }
    }
  }

  onSubmit(): void {
    if (this.tenantForm.valid) {
      this.submitForm.emit(this.tenantForm.value as { name: string; slug: string; planCode: string });
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.tenantForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }
}

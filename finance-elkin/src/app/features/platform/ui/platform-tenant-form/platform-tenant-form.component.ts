import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LucideAngularModule } from 'lucide-angular';

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
            <option value="DEMO">DEMO - Prueba gratuita (10 días, 2 usuarios, 2 roles)</option>
            <option value="BASIC">BASIC - Básico</option>
            <option value="PRO">PRO - Profesional</option>
            <option value="ENTERPRISE">ENTERPRISE - Empresarial</option>
          </select>
        </div>
        
        <div class="flex justify-end gap-3 pt-4">
          <button type="button" (click)="cancel.emit()" 
                  class="px-4 py-2 border border-[#C8E6C9] text-[#666666] rounded-lg hover:bg-[#F1F8E9] transition-colors">
            Cancelar
          </button>
          <button type="submit" [disabled]="isLoading" 
                  class="px-4 py-2 bg-[#2E7D32] text-white rounded-lg hover:bg-[#4CAF50] transition-colors disabled:opacity-50">
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
  @Output() submitForm = new EventEmitter<{ name: string; slug: string; planCode: string }>();
  @Output() cancel = new EventEmitter<void>();

  private fb = inject(FormBuilder);

  tenantForm = this.fb.group({
    name: ['', [Validators.required]],
    slug: ['', [Validators.required, Validators.minLength(3), Validators.pattern(/^[a-z0-9-]+$/)]],
    planCode: ['DEMO', Validators.required]
  });

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
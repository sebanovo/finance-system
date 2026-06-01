import { Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UpdateTenantUserRequest, TenantUserResponse } from '../../../../entities/user';

@Component({
  selector: 'app-user-edit-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <form [formGroup]="userForm" (ngSubmit)="onSubmit()" class="space-y-4">
      <div class="grid grid-cols-2 gap-4">
        <div>
          <label for="firstNameEdit" class="block text-sm font-medium text-foreground mb-1">Nombre</label>
          <input 
            id="firstNameEdit" 
            type="text" 
            formControlName="firstName"
            class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
            [class.border-destructive]="isFieldInvalid('firstName')"
            placeholder="Ej: Carlos">
          <span *ngIf="isFieldInvalid('firstName')" class="text-xs text-destructive mt-1">El nombre es requerido.</span>
        </div>
        
        <div>
          <label for="lastNameEdit" class="block text-sm font-medium text-foreground mb-1">Apellido</label>
          <input 
            id="lastNameEdit" 
            type="text" 
            formControlName="lastName"
            class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
            [class.border-destructive]="isFieldInvalid('lastName')"
            placeholder="Ej: Rojas">
          <span *ngIf="isFieldInvalid('lastName')" class="text-xs text-destructive mt-1">El apellido es requerido.</span>
        </div>
      </div>

      <div>
        <label for="emailEdit" class="block text-sm font-medium text-foreground mb-1">Correo Electrónico</label>
        <input 
          id="emailEdit" 
          type="email" 
          formControlName="email"
          class="w-full px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-colors"
          [class.border-destructive]="isFieldInvalid('email')"
          placeholder="usuario@empresa.com">
        <span *ngIf="isFieldInvalid('email')" class="text-xs text-destructive mt-1">Ingresa un correo válido.</span>
      </div>

      <!-- Actions -->
      <div class="flex items-center justify-end gap-3 pt-4 border-t border-border mt-6">
        <button 
          type="button" 
          (click)="onCancel()"
          [disabled]="status === 'loading'"
          class="px-4 py-2 text-sm font-medium text-foreground bg-background border border-input rounded-md hover:bg-muted transition-colors disabled:opacity-50">
          Cancelar
        </button>
        <button 
          type="submit" 
          [disabled]="status === 'loading'"
          class="px-4 py-2 text-sm font-medium text-primary-foreground bg-primary rounded-md hover:bg-primary/90 transition-colors disabled:opacity-50 flex items-center">
          
          <ng-container *ngIf="status !== 'loading'; else loadingSpinner">
            Guardar Cambios
          </ng-container>
          
          <ng-template #loadingSpinner>
            <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Guardando...
          </ng-template>
        </button>
      </div>
    </form>
  `
})
export class UserEditFormComponent implements OnInit {
  @Input() status: 'idle' | 'loading' | 'success' | 'error' = 'idle';
  @Input() user!: TenantUserResponse;
  
  @Output() formSubmit = new EventEmitter<UpdateTenantUserRequest>();
  @Output() cancel = new EventEmitter<void>();

  private readonly fb = inject(FormBuilder);

  userForm: FormGroup = this.fb.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]]
  });

  ngOnInit() {
    if (this.user) {
      this.userForm.patchValue({
        firstName: this.user.firstName,
        lastName: this.user.lastName,
        email: this.user.email
      });
    }
  }

  onSubmit(): void {
    if (this.userForm.valid && this.status !== 'loading') {
      this.formSubmit.emit(this.userForm.value);
    } else {
      this.userForm.markAllAsTouched();
    }
  }

  onCancel(): void {
    if (this.status !== 'loading') {
      this.cancel.emit();
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.userForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }
}

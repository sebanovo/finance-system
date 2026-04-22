import { Component, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CreateTenantRequest } from '../../models/platform-tenant.models';

@Component({
  selector: 'app-tenant-create-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './tenant-create-form.component.html'
})
export class TenantCreateFormComponent {
  private readonly fb = inject(FormBuilder);
  
  isLoading = input<boolean>(false);
  submitForm = output<CreateTenantRequest>();

  tenantForm = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(150)]],
    slug: ['', [Validators.required, Validators.pattern('^[a-z0-9-]+$')]],
    planCode: ['BASIC'] // Plan por defecto
  });

  onSubmit() {
    if (this.tenantForm.valid) {
      this.submitForm.emit(this.tenantForm.value as CreateTenantRequest);
    } else {
      this.tenantForm.markAllAsTouched();
    }
  }
}
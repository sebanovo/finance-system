import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PublicSignupRequest } from '../../../../entities/tenant/model/public-signup-request.model';

@Component({
  selector: 'app-signup-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './signup-form.component.html'
})
export class SignupFormComponent {
  @Input() loading = false;
  @Input() error: string | null = null;
  @Input() success = false;
  
  @Output() formSubmit = new EventEmitter<PublicSignupRequest>();

  signupForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.signupForm = this.fb.group({
      companyName: ['', [Validators.required, Validators.minLength(3)]],
      tenantSlug: ['', [Validators.required, Validators.pattern(/^[a-z0-9-]+$/)]],
      adminEmail: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.signupForm.valid && !this.loading) {
      this.formSubmit.emit(this.signupForm.value);
    } else {
      this.signupForm.markAllAsTouched();
    }
  }

  // Helpers para la UI
  isFieldInvalid(fieldName: string): boolean {
    const field = this.signupForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }
}

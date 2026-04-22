import { Component, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PlatformLoginRequest } from '../../models/platform-tenant.models';

@Component({
  selector: 'app-admin-login-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './admin-login-form.component.html'
})
export class AdminLoginFormComponent {
  private readonly fb = inject(FormBuilder);

  isLoading = input<boolean>(false);
  errorMessage = input<string | null>(null);
  submitForm = output<PlatformLoginRequest>();

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  onSubmit() {
    if (this.loginForm.valid) {
      this.submitForm.emit(this.loginForm.value as PlatformLoginRequest);
    } else {
      this.loginForm.markAllAsTouched();
    }
  }
}
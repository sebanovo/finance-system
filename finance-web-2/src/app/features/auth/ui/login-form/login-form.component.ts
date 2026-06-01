import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { LoginRequest } from '../../../../entities/auth/model/login-request.model';

@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, LucideAngularModule],
  templateUrl: './login-form.component.html'
})
export class LoginFormComponent {
  @Input() status: 'idle' | 'loading' | 'success' | 'error' = 'idle';
  @Input() error: string | null = null;
  
  @Output() loginSubmit = new EventEmitter<LoginRequest>();

  showPassword = false;

  private readonly fb = inject(FormBuilder);

  loginForm: FormGroup = this.fb.group({
    tenantSlug: ['', [Validators.required, Validators.pattern(/^[a-z0-9-]+$/)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  onSubmit(): void {
    if (this.loginForm.valid && this.status !== 'loading') {
      const tenantSlug = typeof this.loginForm.value.tenantSlug === 'string'
        ? this.loginForm.value.tenantSlug.trim()
        : '';

      this.loginSubmit.emit({
        email: this.loginForm.value.email,
        password: this.loginForm.value.password,
        tenantSlug
      });
    } else {
      this.loginForm.markAllAsTouched();
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.loginForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }
}

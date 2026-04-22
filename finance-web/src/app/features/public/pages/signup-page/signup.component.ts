import { Component, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { OnboardingService, SignupRequest } from '../../data-access/onboarding.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css',
})
export class SignupComponent {
  private fb = inject(FormBuilder);
  private onboardingService = inject(OnboardingService);
  private router = inject(Router);

  // Formulario reactivo dividido en secciones
  signupForm: FormGroup = this.fb.group({
    // Sección: Datos de la Empresa
    companyName: ['', [Validators.required, Validators.minLength(2)]],
    tenantSlug: [
      '',
      [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(50),
        Validators.pattern(/^[a-z0-9-]+$/),
      ],
    ],
    // Sección: Datos del Administrador
    firstName: ['', [Validators.required, Validators.minLength(2)]],
    lastName: ['', [Validators.required, Validators.minLength(2)]],
    adminEmail: ['', [Validators.required, Validators.email]],
    password: [
      '',
      [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/),
      ],
    ],
    confirmPassword: ['', [Validators.required]],
  });

  // Signals para estado
  readonly isLoading = signal(false);
  readonly error = signal<string | null>(null);
  readonly isSuccess = signal(false);

  constructor() {
    // Validación personalizada para confirmar contraseña
    this.signupForm.get('confirmPassword')?.setValidators([
      Validators.required,
      this.passwordMatchValidator.bind(this),
    ]);
  }

  /**
   * Validador personalizado para confirmar que las contraseñas coinciden
   */
  private passwordMatchValidator(control: AbstractControl): { [key: string]: boolean } | null {
    const password = this.signupForm?.get('password')?.value;
    const confirmPassword = control.value;

    if (password !== confirmPassword) {
      return { passwordMismatch: true };
    }

    return null;
  }

  /**
   * Envía el formulario de registro
   */
  onSubmit(): void {
    if (this.signupForm.invalid) {
      this.markFormGroupTouched();
      return;
    }

    this.isLoading.set(true);
    this.error.set(null);

    const request: SignupRequest = {
      companyName: this.signupForm.value.companyName,
      tenantSlug: this.signupForm.value.tenantSlug,
      adminEmail: this.signupForm.value.adminEmail,
      password: this.signupForm.value.password,
      firstName: this.signupForm.value.firstName,
      lastName: this.signupForm.value.lastName,
    };

    this.onboardingService.signup(request).subscribe({
      next: (response) => {
        console.log('Signup successful:', response);
        this.isSuccess.set(true);
        this.isLoading.set(false);
      },
      error: (err) => {
        const errorMsg = err.error?.message || 'Error al registrar la empresa';
        this.error.set(errorMsg);
        this.isLoading.set(false);
      },
    });
  }

  /**
   * Marca todos los campos del formulario como tocados para mostrar errores
   */
  private markFormGroupTouched(): void {
    Object.keys(this.signupForm.controls).forEach((key) => {
      const control = this.signupForm.get(key);
      control?.markAsTouched();
    });
  }

  /**
   * Obtiene el mensaje de error para un campo específico
   */
  getFieldError(fieldName: string): string {
    const control = this.signupForm.get(fieldName);
    if (control?.errors && control.touched) {
      if (control.errors['required']) {
        return 'Este campo es obligatorio';
      }
      if (control.errors['email']) {
        return 'Ingresa un email válido';
      }
      if (control.errors['minlength']) {
        const minLength = control.errors['minlength'].requiredLength;
        return `Debe tener al menos ${minLength} caracteres`;
      }
      if (control.errors['maxlength']) {
        const maxLength = control.errors['maxlength'].requiredLength;
        return `No puede tener más de ${maxLength} caracteres`;
      }
      if (control.errors['pattern']) {
        if (fieldName === 'tenantSlug') {
          return 'Solo letras minúsculas, números y guiones (sin espacios)';
        }
        if (fieldName === 'password') {
          return 'Debe contener al menos una mayúscula, una minúscula y un número';
        }
      }
      if (control.errors['passwordMismatch']) {
        return 'Las contraseñas no coinciden';
      }
    }
    return '';
  }

  /**
   * Verifica si un campo tiene errores
   */
  hasFieldError(fieldName: string): boolean {
    const control = this.signupForm.get(fieldName);
    return !!(control?.errors && control.touched);
  }

  /**
   * Navega al login
   */
  goToLogin(): void {
    this.router.navigate(['/auth/login']);
  }

  /**
   * Genera automáticamente el tenantSlug basado en el companyName
   */
  generateSlug(): void {
    const companyName = this.signupForm.get('companyName')?.value || '';
    const slug = companyName
      .toLowerCase()
      .replace(/[^a-z0-9\s-]/g, '') // Remover caracteres especiales
      .replace(/\s+/g, '-') // Reemplazar espacios con guiones
      .replace(/-+/g, '-') // Remover guiones múltiples
      .replace(/^-|-$/g, ''); // Remover guiones al inicio/fin

    this.signupForm.patchValue({ tenantSlug: slug });
  }
}
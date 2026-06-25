import { Injectable, inject, signal, computed } from '@angular/core';
import { PublicSignupRequest } from '../../../entities/tenant/model/public-signup-request.model';
import { TenantService } from '../../../entities/tenant/api/tenant.service';
import { PublicPaidSignupRequest } from '../../../entities/tenant/model/public-paid-signup-request.model';

export interface SignupState {
  loading: boolean;
  error: string | null;
  success: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class SignupUseCase {
  private readonly tenantService = inject(TenantService);

  // Estado privado
  private readonly state = signal<SignupState>({
    loading: false,
    error: null,
    success: false
  });

  // Selectores públicos (Readonly)
  readonly loading = computed(() => this.state().loading);
  readonly error = computed(() => this.state().error);
  readonly success = computed(() => this.state().success);

  signup(request: PublicSignupRequest): void {
    // Actualizar estado a loading
    this.state.set({ loading: true, error: null, success: false });

    this.tenantService.publicSignup(request).subscribe({
      next: (response) => {
        if (response.success) {
          this.state.set({ loading: false, error: null, success: true });
        } else {
          this.state.set({ loading: false, error: response.message || 'Error desconocido', success: false });
        }
      },
      error: (err) => {
        const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
        this.state.set({ loading: false, error: errorMsg, success: false });
      }
    });
  }

  signupPaid(request: PublicPaidSignupRequest): void {
    this.state.set({ loading: true, error: null, success: false });

    this.tenantService.publicPaidSignup(request).subscribe({
      next: (response) => {
        if (response.success && response.data?.checkoutUrl) {
          this.state.set({ loading: false, error: null, success: true });
          window.location.href = response.data.checkoutUrl;
          return;
        }

        this.state.set({
          loading: false,
          error: response.message || 'No se pudo iniciar el pago',
          success: false
        });
      },
      error: (err) => {
        const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
        this.state.set({ loading: false, error: errorMsg, success: false });
      }
    });
  }

  resetState(): void {
    this.state.set({ loading: false, error: null, success: false });
  }
}

import { inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthStorageService } from '../storage/auth-storage.service';
import { AuthService } from '../../../entities/auth/api/auth.service';
import { AuthenticatedTenantUserResponse } from '../../../entities/auth/model/authenticated-tenant-user-response.model';

@Injectable({
  providedIn: 'root'
})
export class AuthFacade {
  private readonly authStorage = inject(AuthStorageService);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  readonly currentUser = signal<AuthenticatedTenantUserResponse | null>(null);

  /** Carga la información del usuario actual autenticado. */
  loadCurrentUser(): void {
    if (!this.authStorage.getToken()) return;
    
    this.authService.getMe().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.currentUser.set(res.data);
        }
      },
      error: (err) => {
        console.error('Error loading current user', err);
        // Si hay error 401/403, el interceptor probablemente ya se encargue, pero podemos setear null por seguridad.
        this.currentUser.set(null);
      }
    });
  }

  /** Cierra la sesión del usuario actual limpiando el almacenamiento y redirigiendo a la vista de login. */
  logout(): void {
    // Limpiar almacenamiento persistente
    this.authStorage.clear();
    
    // Limpiar el estado de usuario
    this.currentUser.set(null);
    
    // Redirigir al inicio de sesión
    this.router.navigate(['/login']);
  }
}

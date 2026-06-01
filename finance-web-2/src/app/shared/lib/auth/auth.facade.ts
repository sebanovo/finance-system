import { inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
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
  readonly status = signal<'idle' | 'loading' | 'ready' | 'error'>('idle');

  /** Carga la información del usuario actual autenticado. */
  async loadCurrentUser(): Promise<void> {
    if (!this.authStorage.hasValidTenantSession()) {
      this.currentUser.set(null);
      this.status.set('idle');
      return;
    }

    this.status.set('loading');

    try {
      const res = await firstValueFrom(this.authService.getMe());
      if (res.success && res.data) {
        this.currentUser.set(res.data);
        this.status.set('ready');
        return;
      }

      this.currentUser.set(null);
      this.status.set('error');
    } catch (err) {
      console.error('Error loading current user', err);
      this.currentUser.set(null);
      this.status.set('error');
    }
  }

  async bootstrapCurrentUser(): Promise<void> {
    if (this.currentUser()) {
      this.status.set('ready');
      return;
    }

    await this.loadCurrentUser();
  }

  /** Cierra la sesión del usuario actual limpiando el almacenamiento y redirigiendo a la vista de login. */
  logout(): void {
    // Limpiar almacenamiento persistente
    this.authStorage.clear();
    
    // Limpiar el estado de usuario
    this.currentUser.set(null);
    this.status.set('idle');
    
    // Redirigir al inicio de sesión
    this.router.navigate(['/login']);
  }
}

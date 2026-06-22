import { inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { AuthStorageService } from '../storage/auth-storage.service';
import { PermissionService } from '../auth/permission.service';
import { AuthService } from '../../../entities/auth/api/auth.service';
import { AuthenticatedTenantUserResponse } from '../../../entities/auth/model/authenticated-tenant-user-response.model';

@Injectable({
  providedIn: 'root'
})
export class AuthFacade {
  private readonly authStorage = inject(AuthStorageService);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  private readonly permissionService = inject(PermissionService);
  readonly currentUser = signal<AuthenticatedTenantUserResponse | null>(null);
  readonly status = signal<'idle' | 'loading' | 'ready' | 'error'>('idle');

  constructor() {
    if (this.authStorage.hasValidTenantSession()) {
      void this.loadCurrentUser();
    }
  }

  /** Carga la información del usuario actual autenticado. */
  async loadCurrentUser(): Promise<void> {
    if (!this.authStorage.hasValidTenantSession()) {
      this.currentUser.set(null);
      this.status.set('idle');
      return;
    }

    this.status.set('loading');

    try {
      const [meResult, profileResult] = await Promise.allSettled([
        firstValueFrom(this.authService.getMe()),
        firstValueFrom(this.authService.getProfile())
      ]);

      if (meResult.status === 'fulfilled' && meResult.value.success && meResult.value.data) {
        const currentUser = meResult.value.data;
        const profile = profileResult.status === 'fulfilled' && profileResult.value.success ? profileResult.value.data : null;

        this.currentUser.set({
          ...currentUser,
          profilePhotoUrl: profile?.profilePhotoUrl ?? currentUser.profilePhotoUrl ?? null,
          profilePhotoContentType: profile?.profilePhotoContentType ?? currentUser.profilePhotoContentType ?? null
        });
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

  getTenantLandingRoute(): string {
    return this.permissionService.hasRole('OWNER_ADMIN')
      ? '/dashboard/summary'
      : '/dashboard/me';
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

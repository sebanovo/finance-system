import { Injectable, inject, signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { PlatformAuthService } from '../services/platform-auth.service';
import { PlatformLoginRequest, PlatformUserResponse } from '../models/platform-tenant.models';

// Keys exclusivas del ecosistema Platform (aisladas del tenant)
const PLATFORM_ACCESS_TOKEN = 'platform_access_token';
const PLATFORM_REFRESH_TOKEN = 'platform_refresh_token';

@Injectable({
  providedIn: 'root'
})
export class PlatformAuthStore {
  private readonly authService = inject(PlatformAuthService);
  private readonly router = inject(Router);

  // Estado Reactivo (Signals)
  readonly isLoading = signal<boolean>(false);
  readonly error = signal<string | null>(null);
  readonly currentUser = signal<PlatformUserResponse | null>(null);

  // Selectores derivados
  readonly userName = computed(() => {
    const user = this.currentUser();
    return user ? `${user.firstName} ${user.lastName}` : 'Superadmin';
  });

  readonly userInitials = computed(() => {
    const user = this.currentUser();
    if (!user) return 'SA';
    return `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase();
  });

  // --- Acciones ---

  login(credentials: PlatformLoginRequest): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.authService.login(credentials).subscribe({
      next: (response) => {
        const { accessToken, refreshToken } = response.data;
        localStorage.setItem(PLATFORM_ACCESS_TOKEN, accessToken);
        localStorage.setItem(PLATFORM_REFRESH_TOKEN, refreshToken);
        this.isLoading.set(false);
        this.router.navigate(['/platform/tenants']);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.error.set(err.error?.message || 'Error al iniciar sesión. Verifica tus credenciales.');
      }
    });
  }

  loadMe(): void {
    this.authService.getMe().subscribe({
      next: (response) => {
        this.currentUser.set(response.data);
      },
      error: () => {
        // Silencioso: si falla /me no bloqueamos la UI, solo no mostramos datos del perfil
        this.currentUser.set(null);
      }
    });
  }

  logout(): void {
    localStorage.removeItem(PLATFORM_ACCESS_TOKEN);
    localStorage.removeItem(PLATFORM_REFRESH_TOKEN);
    this.currentUser.set(null);
    this.router.navigate(['/platform/auth/login']);
  }

  // Utilidad para el interceptor de refresh
  static getRefreshToken(): string | null {
    return localStorage.getItem(PLATFORM_REFRESH_TOKEN);
  }
}
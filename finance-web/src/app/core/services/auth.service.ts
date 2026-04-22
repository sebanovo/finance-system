import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { API_CONFIG, buildApiUrl } from '../config/api.config';
import { LoginRequest, LoginResponse, AuthUser } from '../models/auth.model';
import { ApiResponse } from '../models/api-response.model';
import { TenantStorage } from '../storage/tenant-storage';

/**
 * Servicio de autenticación para el ecosistema de tenants.
 * Maneja login, logout y obtención de datos del usuario actual.
 * 
 * Usa inject() para inyectar HttpClient (Angular 18+ standalone pattern)
 */
@Injectable({
    providedIn: 'root',
})
export class AuthService {
    private httpClient = inject(HttpClient);

    /**
     * Inicia sesión con email y contraseña
     * POST /api/auth/login
     * 
     * Después del login:
     * 1. Guarda el access token en localStorage (finance_access_token)
     * 2. Guarda el tenant slug en localStorage (tenant_slug)
     * 
     * @param loginData Email y contraseña del usuario
     * @returns Observable<LoginResponse> con token y datos del usuario
     */
    login(loginData: LoginRequest): Observable<ApiResponse<LoginResponse>> {
        const url = buildApiUrl(API_CONFIG.auth.login);

        return this.httpClient.post<ApiResponse<LoginResponse>>(url, loginData).pipe(
            tap((response) => {
                if (response.success && response.data) {
                    // Guardar el access token
                    localStorage.setItem('finance_access_token', response.data.accessToken);

                    // Guardar el tenant slug para futuras peticiones
                    TenantStorage.setTenantSlug(response.data.tenantSlug);

                    // Opcionalmente guardar el refresh token si existe
                    if (response.data.refreshToken) {
                        localStorage.setItem('finance_refresh_token', response.data.refreshToken);
                    }
                }
            })
        );
    }

    /**
     * Obtiene los datos del usuario autenticado
     * GET /api/auth/me
     * 
     * Esta petición requiere que esté autenticado (token en Authorization header)
     * 
     * @returns Observable<AuthUser> con datos del usuario actual
     */
    getAuthenticatedUser(): Observable<ApiResponse<AuthUser>> {
        const url = buildApiUrl(API_CONFIG.auth.me);
        return this.httpClient.get<ApiResponse<AuthUser>>(url);
    }

    /**
     * Cierra la sesión del usuario
     * POST /api/auth/logout
     * 
     * Después del logout:
     * 1. Limpia el token de acceso
     * 2. Limpia el tenant slug
     * 3. Limpia el refresh token (si existe)
     * 
     * @returns Observable<ApiResponse> confirmación del logout
     */
    logout(): Observable<ApiResponse<any>> {
        const url = buildApiUrl(API_CONFIG.auth.logout);

        return this.httpClient.post<ApiResponse<any>>(url, {}).pipe(
            tap(() => {
                // Limpiar tokens y datos locales
                localStorage.removeItem('finance_access_token');
                localStorage.removeItem('finance_refresh_token');
                TenantStorage.clearTenantSlug();
            })
        );
    }

    /**
     * Verifica si el usuario está autenticado
     * (simplemente comprueba si existe un token)
     * 
     * @returns true si hay un token, false si no
     */
    isAuthenticated(): boolean {
        return !!localStorage.getItem('finance_access_token');
    }

    /**
     * Obtiene el token de acceso actual
     * 
     * @returns El access token o null si no existe
     */
    getAccessToken(): string | null {
        return localStorage.getItem('finance_access_token');
    }

    /**
     * Obtiene el tenant slug actual
     * 
     * @returns El tenant slug o null si no existe
     */
    getTenantSlug(): string | null {
        return TenantStorage.getTenantSlug();
    }
}

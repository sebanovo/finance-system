/**
 * DTO para login
 */
export interface LoginRequest {
    email: string;
    password: string;
    tenantSlug?: string; // Opcional: el slug del tenant
}

/**
 * Respuesta del login
 */
export interface LoginResponse {
    user: {
        id: string;
        email: string;
        firstName: string;
        lastName: string;
        role: string;
        tenantId?: string;
    };
    accessToken: string;
    refreshToken?: string;
    tenantSlug: string; // El slug del tenant al que pertenece el usuario
    expiresIn?: number; // Segundos hasta la expiración del token
}

/**
 * Datos del usuario autenticado (respuesta de GET /auth/me)
 */
export interface AuthUser {
    id: string;
    email: string;
    firstName: string;
    lastName: string;
    role: string;
    tenantId: string;
    tenantSlug: string;
    tenantName?: string;
    isActive: boolean;
    lastLogin?: string | null;
}

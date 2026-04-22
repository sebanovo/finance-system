import { HttpInterceptorFn } from '@angular/common/http';

/**
 * HTTP Interceptor funcional que inyecta automáticamente el header X-Tenant-Slug
 * en todas las peticiones HTTP hacia la API del tenant.
 *
 * Angular 18+ usa HttpInterceptorFn (function-based interceptors) en lugar de clases.
 * Este interceptor:
 * 1. Verifica si la petición es de plataforma (superadmin) o del tenant
 * 2. Para peticiones de tenant: inyecta el X-Tenant-Slug header
 * 3. También maneja el token de autenticación correspondiente
 */
export const tenantInterceptor: HttpInterceptorFn = (req, next) => {
    // Verificar si la petición va al ecosistema de plataforma (superadmin)
    const isPlatformRequest = req.url.includes('/api/platform');

    let headers = req.headers;

    if (isPlatformRequest) {
        // Peticiones a la plataforma (NO llevan X-Tenant-Slug)
        const platformToken = localStorage.getItem('platform_access_token');

        if (platformToken) {
            headers = headers.set('Authorization', `Bearer ${platformToken}`);
        }
    } else {
        // Peticiones al ecosistema del tenant
        const tenantToken = localStorage.getItem('finance_access_token');
        const tenantSlug = localStorage.getItem('tenant_slug');

        if (tenantToken) {
            headers = headers.set('Authorization', `Bearer ${tenantToken}`);
        }

        // ✅ Inyección del header X-Tenant-Slug (requerido para multitenancia)
        if (tenantSlug) {
            headers = headers.set('X-Tenant-Slug', tenantSlug);
        }
    }

    // Clonar la petición con los headers modificados
    const clonedReq = req.clone({ headers });

    return next(clonedReq);
};


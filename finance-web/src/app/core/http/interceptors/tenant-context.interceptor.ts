import { HttpInterceptorFn } from '@angular/common/http';

export const tenantContextInterceptor: HttpInterceptorFn = (req, next) => {
  // Verificamos si la petición va dirigida al ecosistema del Superadmin
  const isPlatformRequest = req.url.includes('/api/platform');
  
  let headers = req.headers;

  if (isPlatformRequest) {
    // 1. Lógica para el Superadmin (NO lleva X-Tenant-Slug)
    const platformToken = localStorage.getItem('platform_access_token');
    
    if (platformToken) {
      headers = headers.set('Authorization', `Bearer ${platformToken}`);
    }
  } else {
    // 2. Lógica para el Ecosistema Tenant / Clientes
    const tenantToken = localStorage.getItem('finance_access_token');
    const tenantSlug = localStorage.getItem('tenant_slug');

    if (tenantToken) {
      headers = headers.set('Authorization', `Bearer ${tenantToken}`);
    }
    
    // Inyección obligatoria para multitenencia por esquemas en Spring Boot
    if (tenantSlug) {
      headers = headers.set('X-Tenant-Slug', tenantSlug);
    }
  }

  // Clonamos la petición inyectando los nuevos headers
  const clonedReq = req.clone({ headers });

  return next(clonedReq);
};


// core/http/interceptors/auth-token.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthStorageService } from '../../../shared/lib/storage/auth-storage.service';
import { PlatformStorageService } from '../../../features/platform/lib/platform-storage.service';

export const authTokenInterceptor: HttpInterceptorFn = (req, next) => {
  const authStorage = inject(AuthStorageService);
  const platformStorage = inject(PlatformStorageService);
  
  let clonedRequest = req;
  
  // ✅ Detectar si es una petición de platform
  if (req.url.includes('/api/platform/')) {
    const platformToken = platformStorage.getToken();
    if (platformToken) {
      clonedRequest = req.clone({
        setHeaders: {
          'Authorization': `Bearer ${platformToken}`
        }
      });
      console.log('🔑 Platform token agregado a:', req.url);
    } else {
      console.warn('⚠️ No platform token found for:', req.url);
    }
  } 
  // ✅ Para peticiones de tenant (normal)
  else if (req.url.includes('/api/')) {
    const token = authStorage.getToken();
    const tenantSlug = authStorage.getTenantSlug();
    
    const headersConfig: Record<string, string> = {};
    
    if (token) {
      headersConfig['Authorization'] = `Bearer ${token}`;
    }
    
    if (tenantSlug) {
      headersConfig['X-Tenant-Slug'] = tenantSlug;
    }
    
    if (Object.keys(headersConfig).length > 0) {
      clonedRequest = req.clone({ setHeaders: headersConfig });
      console.log('🔑 Tenant token agregado a:', req.url);
    }
  }
  
  return next(clonedRequest);
};
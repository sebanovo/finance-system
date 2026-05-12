import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthStorageService } from '../../lib/storage/auth-storage.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authStorage = inject(AuthStorageService);
  const token = authStorage.getToken();
  const tenantSlug = authStorage.getTenantSlug();

  let clonedRequest = req;

  // 1. Clonar y agregar cabeceras si existen
  const headersConfig: { [name: string]: string | string[] } = {};

  if (token) {
    headersConfig['Authorization'] = `Bearer ${token}`;
  }

  if (tenantSlug) {
    headersConfig['X-Tenant-Slug'] = tenantSlug;
  }

  if (Object.keys(headersConfig).length > 0) {
    clonedRequest = req.clone({ setHeaders: headersConfig });
  }

  // 2. Manejar la petición y capturar errores globales
  return next(clonedRequest).pipe(
    catchError((error: HttpErrorResponse) => {
      // Aquí se pueden manejar errores de red básicos (ej: 401 Unauthorized para cerrar sesión)
      console.error('[API Error]:', error.message);
      
      if (error.status === 401) {
        // Lógica para desloguear o redirigir al login
        console.warn('Unauthorized access - token might be expired');
      }

      // Propagar el error para que las capas superiores (features/use-cases) lo manejen
      return throwError(() => error);
    })
  );
};

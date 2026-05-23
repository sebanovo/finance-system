import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthStorageService } from '../../lib/storage/auth-storage.service';
import { ToastService } from '../../ui/toast/toast.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authStorage = inject(AuthStorageService);
  const toastService = inject(ToastService);
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
      console.error('[API Error]:', error.message);
      
      if (error.status === 401) {
        toastService.error('Tu sesión ha expirado o no es válida. Por favor, inicia sesión nuevamente.', 'Acceso No Autorizado');
        // Lógica para desloguear o redirigir al login puede ir aquí
      } else if (error.status === 403) {
        toastService.error('No tienes permisos suficientes para realizar esta acción.', 'Acceso Denegado');
      } else if (error.status === 500) {
        toastService.error('Ocurrió un error interno en el servidor. Por favor, intenta más tarde.', 'Error del Servidor');
      } else if (error.status === 0) {
        toastService.error('No se pudo conectar con el servidor. Revisa tu conexión a internet.', 'Error de Conexión');
      }

      // Propagar el error para que las capas superiores (features/use-cases) lo manejen si quieren
      return throwError(() => error);
    })
  );
};

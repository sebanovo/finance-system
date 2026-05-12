import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthStorageService } from '../../lib/storage/auth-storage.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authStorage = inject(AuthStorageService);
  const router = inject(Router);

  const token = authStorage.getToken();

  // Validación básica del token
  if (!token || token.trim() === '') {
    // Redirigir a login si no hay token
    return router.createUrlTree(['/login']);
  }

  // Permitir el acceso
  return true;
};

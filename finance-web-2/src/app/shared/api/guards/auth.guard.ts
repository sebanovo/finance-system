import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthStorageService } from '../../lib/storage/auth-storage.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authStorage = inject(AuthStorageService);
  const router = inject(Router);

  if (!authStorage.hasValidTenantSession()) {
    return router.createUrlTree(['/login']);
  }

  return true;
};

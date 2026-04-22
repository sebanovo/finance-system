import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const platformGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const platformToken = localStorage.getItem('platform_access_token');

  if (platformToken) {
    return true;
  }

  // Si no hay token de plataforma, lo expulsamos al login administrativo
  router.navigate(['/platform/auth/login']);
  return false;
};
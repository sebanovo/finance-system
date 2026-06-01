import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { PlatformStorageService } from '../../../features/platform/lib/platform-storage.service';

export const platformAuthGuard: CanActivateFn = () => {
  const router = inject(Router);
  const platformStorage = inject(PlatformStorageService);

  if (platformStorage.hasAccessToken() || platformStorage.hasRefreshToken()) {
    return true;
  }

  return router.createUrlTree(['/platform/login']);
};

import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { PermissionService } from '../../lib/auth/permission.service';

type TenantStaticRole = 'OWNER_ADMIN' | 'USER';

export function tenantRoleGuard(requiredRole: TenantStaticRole, redirectTo: string): CanActivateFn {
  return () => {
    const permissionService = inject(PermissionService);
    const router = inject(Router);

    const isOwnerAdmin = permissionService.hasRole('OWNER_ADMIN');
    const isClient = !isOwnerAdmin;

    if (requiredRole === 'OWNER_ADMIN') {
      return isOwnerAdmin ? true : router.createUrlTree([redirectTo]);
    }

    return isClient ? true : router.createUrlTree([redirectTo]);
  };
}

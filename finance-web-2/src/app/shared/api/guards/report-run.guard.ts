import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { PermissionService } from '../../lib/auth/permission.service';

export function reportRunGuard(): CanActivateFn {
  return (
    route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot
  ): boolean | UrlTree => {
    const permissionService = inject(PermissionService);
    const router = inject(Router);
    const mode = String(route.paramMap.get('mode') ?? '').toLowerCase();

    const requiredPermission = mode === 'analytic'
      ? 'reports.analytic.run'
      : mode === 'managerial'
        ? 'reports.managerial.run'
        : null;

    if (!requiredPermission || !permissionService.hasPermission(requiredPermission)) {
      return router.createUrlTree(['/dashboard/reports']);
    }

    return true;
  };
}

import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { PermissionService } from '../../lib/auth/permission.service';

/**
 * Genera un CanActivateFn Guard que verifica que el usuario tenga
 * AL MENOS UNO de los permisos requeridos para acceder a la ruta.
 *
 * Uso en app.routes.ts:
 *   { path: 'accounts', canActivate: [permissionGuard('accounts.admin.read', 'accounts.create')], ... }
 *   { path: 'transactions', canActivate: [permissionGuard('transactions.admin.read')], ... }
 */
export function permissionGuard(...requiredPermissions: string[]): CanActivateFn {
  return (route, state) => {
    const permissionService = inject(PermissionService);
    const router = inject(Router);

    const hasAccess = permissionService.hasAnyPermission(...requiredPermissions);

    if (!hasAccess) {
      // Redirigir al resumen del dashboard si no tiene permisos
      return router.createUrlTree(['/dashboard/summary']);
    }

    return true;
  };
}

import { Routes } from '@angular/router';
import { platformGuard } from '../../core/session/guards/platform.guard';

export const PLATFORM_ROUTES: Routes = [

  // Ruta pública — Login Superadmin (sin registro público por seguridad)
  { path: 'auth/login',
    loadComponent: () => import('./auth/pages/login/admin-login-page.component').then(m => m.AdminLoginPageComponent)
  },

  // Rutas protegidas o privadas (Con layout: Header + Sidebar)
  { path: '',
    loadComponent: () => import('./layout/platform-layout.component').then(m => m.PlatformLayoutComponent),
    canActivate: [platformGuard],
    children: [
      { path: 'tenants',
        children: [
          { path: '',
            loadComponent: () => import('./tenants/pages/tenant-list-page/tenant-list-page.component').then(m => m.TenantListPageComponent)
          },
          { path: 'create',
            loadComponent: () => import('./tenants/pages/tenant-create-page/tenant-create-page.component').then(m => m.TenantCreatePageComponent)
          }
        ]
      },
      // Redirección por defecto cuando entran a /platform o la ruta base del admin
      { path: '',
        redirectTo: 'tenants',
        pathMatch: 'full'
      }
    ]
  },
  { path: '**',
    redirectTo: 'auth/login',
    pathMatch: 'full'
  }
];

import { Routes } from "@angular/router";
import { authGuard } from "./core/session/guards/auth.guard";
import { platformGuard } from "./core/session/guards/platform.guard";
import { DashboardLayoutComponent } from "./features/dashboard/layout/dashboard-layout.component";

export const routes: Routes = [
  
  // Ruta de auth de administracion de tenants (superadmin)
  {
    path: "platform",
    loadChildren: () =>
      import("./features/platform/platform.routes").then(m => m.PLATFORM_ROUTES)
  },

  // Ruta auth cliente (tenant)
  {
    path: "auth",
    loadChildren: () =>
      import("./features/auth/auth.routes").then(m => m.AUTH_ROUTES)
  },

  // Ruta de nuestra web SaaS
  { path:"finance",
    loadChildren: () =>
      import("./features/public/public.routes").then(m => m.PUBLIC_ROUTES)
  },
  
  // Rutas privadas del tenant
  {
    path: "app",
    canActivate: [authGuard],
    component: DashboardLayoutComponent,
    children: [
      { path: "", redirectTo: "dashboard", pathMatch: "full" },
      { path: "dashboard", loadChildren: () => import("./features/dashboard/dashboard.routes").then(m => m.DASHBOARD_ROUTES)},
      { path: "users", loadChildren: () => import("./features/users/users.routes").then(m => m.USERS_ROUTES)},
      { path: "access", loadChildren: () => import("./features/access/access.routes").then(m => m.ACCESS_ROUTES)},
    ],
  },

  // Ruta comodín
  {
    path: "**",
    redirectTo: "/finance",
  },
];


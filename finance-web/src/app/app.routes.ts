import { Routes } from "@angular/router";

import { AdminAuthLayout } from "./core/layout/layouts/admin-auth-layout/admin-auth-layout";
import { AppLayout } from "./core/layout/layouts/app-layout/app-layout";
import { AuthLayout } from "./core/layout/layouts/auth-layout/auth-layout";
import { PublicLayout } from "./core/layout/layouts/public-layout/public-layout";
import { authMatchGuard } from "./core/session/guards/auth-match.guard";
import { publicOnlyGuard } from "./core/session/guards/public-only.guard";

export const routes: Routes = [
  // Public
  {
    path: "",
    component: PublicLayout,
    children: [
      {
        path: "",
        loadChildren: () =>
          import("./features/public/public.routes").then(m => m.PUBLIC_ROUTES),
      },
    ],
  },

  // Authentication
  {
    path: "auth",
    component: AuthLayout,
    canActivate: [publicOnlyGuard],
    children: [
      // public login
      {
        path: "",
        loadChildren: () =>
          import("./features/auth/auth.routes").then(m => m.AUTH_ROUTES),
      },
    ],
  },

  {
    path: "",
    component: AdminAuthLayout,
    children: [
      // hidden login
      {
        path: "plataform/auth",
        loadChildren: () =>
          import("./features/auth/auth.routes").then(m => m.ADMIN_ROUTES),
      },
    ],
  },

  // Private
  {
    path: "app",
    canMatch: [authMatchGuard],
    component: AppLayout,
    children: [
      {
        path: "", //default route
        redirectTo: "dashboard",
        pathMatch: "full",
      },

      {
        path: "dashboard",
        loadChildren: () =>
          import("./features/dashboard/dashboard.routes").then(
            m => m.DASHBOARD_ROUTES
          ),
      },
      {
        path: "tenants",
        loadChildren: () =>
          import("./features/tanants/tenants.routes").then(
            m => m.TENANTS_ROUTES
          ),
      },
      {
        path: "subscriptions",
        loadChildren: () =>
          import("./features/subscriptions/subscriptions.routes").then(
            m => m.SUBSCRIPTIONS_ROUTES
          ),
      },
      {
        path: "users",
        loadChildren: () =>
          import("./features/users/users.routes").then(m => m.USERS_ROUTES),
      },
      {
        path: "plans",
        loadChildren: () =>
          import("./features/plans/plans.routes").then(m => m.PLANS_ROUTES),
      },
      {
        path: "access",
        loadChildren: () =>
          import("./features/access/access.routes").then(m => m.ACCESS_ROUTES),
      },
      {
        path: "audit",
        loadChildren: () =>
          import("./features/audit/audit.routes").then(m => m.AUDIT_ROUTES),
      },
      // {
      //   path: "products",
      //   loadChildren: () =>
      //     import("./features/products/products.routes").then(
      //       m => m.PRODUCTS_ROUTES
      //     ),
      // },
      {
        path: "reports",
        loadChildren: () =>
          import("./features/reports/reports.routes").then(
            m => m.REPORTS_ROUTES
          ),
      },
      {
        path: "settings",
        loadChildren: () =>
          import("./features/settings/settings.routes").then(
            m => m.SETTINGS_ROUTES
          ),
      },
    ],
  },

  //unknown routes
  {
    path: "**",
    redirectTo: "",
  },
];

// 1. canActivateChild
//    Protect all children
// {
//   path: "app",
//   canActivateChild: [authGuard],
//   children: [...]
// }

// 2. canDeactivate
// {
//   path: "form",
//   canDeactivate: [unsavedChangesGuard]
// }
//    Do not allow to leave without saving form (data)

// 3. canActivate

// canMatch     → ¿esta ruta aplica?
// canActivate  → ¿puede entrar?
// canActivateChild → ¿puede entrar a hijos?
// canDeactivate → ¿puede salir?

// Busca otra ruta que haga match
// [
//   {
//     path: "dashboard",
//     canMatch: [isAdmin],
//     loadComponent: () => AdminDashboard
//   },
//   {
//     path: "dashboard",
//     loadComponent: () => UserDashboard
//   }
// ]

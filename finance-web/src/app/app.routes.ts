import { Routes } from "@angular/router";
import { AdminAuthLayout } from "./core/layout/layouts/admin-auth-layout/admin-auth-layout";
import { AppLayout } from "./core/layout/layouts/app-layout/app-layout";
import { AuthLayout } from "./core/layout/layouts/auth-layout/auth-layout";
import { PublicLayout } from "./core/layout/layouts/public-layout/public-layout";

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
    component: AppLayout,
    children: [
      {
        path: "", //default route
        loadChildren: () =>
          import("./features/dashboard/dashboard.routes").then(
            m => m.DASHBOARD_ROUTES
          ),
      },
      {
        path: "dashboard",
        loadChildren: () =>
          import("./features/dashboard/dashboard.routes").then(
            m => m.DASHBOARD_ROUTES
          ),
      },
      {
        path: "users",
        loadChildren: () =>
          import("./features/users/users.routes").then(m => m.USERS_ROUTES),
      },
      {
        path: "products",
        loadChildren: () =>
          import("./features/products/products.routes").then(
            m => m.PRODUCTS_ROUTES
          ),
      },
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

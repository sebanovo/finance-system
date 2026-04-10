import { Routes } from "@angular/router";

export const AUTH_ROUTES: Routes = [
  // Public auth routes
  {
    path: "login",
    loadComponent: () =>
      import("./pages/login-page/login-page").then(m => m.LoginPage),
  },
  {
    path: "signup",
    loadComponent: () =>
      import("./pages/signup-page/signup-page").then(m => m.SignupPage),
  },
  //path: "forgot-password"
  //path: "register"
];

// Superadmin routes
export const ADMIN_ROUTES: Routes = [
  {
    path: "login",
    loadComponent: () =>
      import("./pages/login-admin-page/login-admin-page").then(
        m => m.LoginAdminPage
      ),
  },
];

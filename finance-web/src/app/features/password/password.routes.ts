import { Routes } from "@angular/router";

export const PASSWORD_ROUTES: Routes = [
  {
    path: "forgot-password",
    loadComponent: () =>
      import("./pages/forgot-password-page/forgot-password-page").then(
        m => m.ForgotPasswordPage
      ),
  },
];
export const RESET_PASSWORD: Routes = [
  {
    path: "reset-password",
    loadComponent: () =>
      import("./pages/reset-password-page/reset-password-page").then(
        m => m.ResetPasswordPage
      ),
  },
];

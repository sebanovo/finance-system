import { Routes } from "@angular/router";
import { guestGuard } from "../../core/session/guards/guest.guard";

export const AUTH_ROUTES: Routes = [
  // Public auth routes
  {
    path: "login",
    canActivate: [guestGuard],
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
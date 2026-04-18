import { inject } from "@angular/core";
import { CanActivateChildFn, Router, UrlTree } from "@angular/router";

import { SessionStore } from "../store/session.store";

export const adminGuestGuard: CanActivateChildFn = (): boolean | UrlTree => {
  const sessionStore = inject(SessionStore);
  const router = inject(Router);

  if (!sessionStore.isAuthenticated()) {
    return true;
  }

  const isAdmin =
    sessionStore.roles().includes("SUPERADMIN") ||
    sessionStore.roles().includes("ADMIN");

  return router.createUrlTree([isAdmin ? "/app" : "/app"]);
};

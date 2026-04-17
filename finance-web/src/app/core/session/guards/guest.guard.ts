import { inject } from "@angular/core";
import { CanActivateFn, Router, UrlTree } from "@angular/router";

import { SessionStore } from "../store/session.store";

export const guestGuard: CanActivateFn = (): boolean | UrlTree => {
  const sessionStore = inject(SessionStore);
  const router = inject(Router);

  if (sessionStore.isAuthenticated()) {
    console.log("guestGuard: user is authenticated, redirecting to /app");
    return router.createUrlTree(["/app"]);
  }

  return true;
};

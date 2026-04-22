import { inject } from "@angular/core";
import { CanActivateFn, Router, UrlTree } from "@angular/router";

import { SessionStore } from "../store/session.store";

export const adminAuthGuard: CanActivateFn = (
  _route,
  state
): boolean | UrlTree => {
  const sessionStore = inject(SessionStore);
  const router = inject(Router);

  if (!sessionStore.initialized()) {
    return router.createUrlTree(["/platform/auth/login"], {
      queryParams: {
        returnUrl: state.url,
      },
    });
  }

  if (!sessionStore.isAuthenticated()) {
    return router.createUrlTree(["/platform/auth/login"], {
      queryParams: {
        returnUrl: state.url,
      },
    });
  }

  const isAdmin =
    sessionStore.roles().includes("SUPERADMIN") ||
    sessionStore.roles().includes("ADMIN");

  if (!isAdmin) {
    return router.createUrlTree(["/app"]);
  }

  return true;
};

import { inject } from "@angular/core";
import { CanActivateFn, Router, UrlTree } from "@angular/router";

import { SessionStore } from "../store/session.store";

export const authGuard: CanActivateFn = (_route, state): boolean | UrlTree => {
  const sessionStore = inject(SessionStore);
  const router = inject(Router);

  if (sessionStore.isAuthenticated()) {
    console.log("autenticado");
    return true;
  }

  return router.createUrlTree(["/auth/login"], {
    queryParams: {
      returnUrl: state.url,
    },
  });
};

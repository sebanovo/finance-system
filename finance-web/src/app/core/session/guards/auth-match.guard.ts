import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";

import { SessionStore } from "../store/session.store";

export const authMatchGuard: CanActivateFn = (_route, state) => {
  const sessionStore = inject(SessionStore);
  const router = inject(Router);

  if (sessionStore.isAuthenticated() && sessionStore.isInitialized()) {
    return true;
  }
  console.log("authMatchGuard");

  return router.createUrlTree(["/auth/login"], {
    queryParams: {
      returnUrl: state.url,
    },
  });
};

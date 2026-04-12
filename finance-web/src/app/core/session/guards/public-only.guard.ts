import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";

import { SessionStore } from "../store/session.store";

export const publicOnlyGuard: CanActivateFn = () => {
  const sessionStore = inject(SessionStore);
  const router = inject(Router);

  if (sessionStore.isAuthenticated() && sessionStore.isInitialized()) {
    console.log("publicOnlyGuard");
    return router.createUrlTree(["/app"]);
  }

  console.log("publicOnlyGuard true");
  return true;
};

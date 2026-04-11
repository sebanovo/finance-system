import { inject } from "@angular/core";
import { CanMatchFn, Router } from "@angular/router";

import { SessionStore } from "../store/session.store";

export const publicOnlyGuard: CanMatchFn = () => {
  const sessionStore = inject(SessionStore);
  const router = inject(Router);

  if (sessionStore.isAuthenticated()) {
    return router.createUrlTree(["/app"]);
  }

  return true;
};

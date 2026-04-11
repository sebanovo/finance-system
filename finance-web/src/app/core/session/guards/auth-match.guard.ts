import { inject } from "@angular/core";
import { CanMatchFn, Router } from "@angular/router";

import { SessionStore } from "../store/session.store";

export const authMatchGuard: CanMatchFn = (_route, segments) => {
  const sessionStore = inject(SessionStore);
  const router = inject(Router);

  if (sessionStore.isAuthenticated()) {
    return true;
  }

  /**
   * Build the URL the user originally attempted to access.
   * This is used to redirect them back after successful login.
   */
  const attemptedUrl = `/${segments.map(segment => segment.path).join("/")}`;

  return router.createUrlTree(["/auth/login"], {
    queryParams: {
      returnUrl: attemptedUrl || "/app", //come back to the same plae
    },
  });
};

import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";

export const subscriptionsFallbackGuard: CanActivateFn = () => {
  const router = inject(Router);

  return router.createUrlTree(["/app/dashboard"]);
};

import { inject } from "@angular/core";
import { toObservable } from "@angular/core/rxjs-interop";
import { CanActivateFn, Router, UrlTree } from "@angular/router";
import { filter, map, take } from "rxjs";
import { SessionFacade } from "../../session/state/session.facade";

/**
 * Guard for guest-only routes such as login.
 *
 * Authenticated users should not access guest pages again.
 */
export const guestGuard: CanActivateFn = () => {
  const sessionFacade = inject(SessionFacade);
  const router = inject(Router);
  console.log("guestGuard", sessionFacade.status);

  return toObservable(sessionFacade.status).pipe(
    filter(status => status !== "unknown" && status !== "checking"),
    take(1),
    map((status): boolean | UrlTree => {
      if (status === "authenticated") {
        console.log("Autenticado");
        return router.createUrlTree(["/"]);
      }

      return true;
    })
  );
};

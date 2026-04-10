import { inject } from "@angular/core";
import { toObservable } from "@angular/core/rxjs-interop";
import { CanActivateFn, Router, UrlTree } from "@angular/router";
import { filter, map, take } from "rxjs";
import { SessionFacade } from "../../session/state/session.facade";

/**
 * Guard for protected routes.
 *
 * It waits until session bootstrap finishes before deciding whether
 * the current navigation should continue or redirect to login.
 */
export const authGuard: CanActivateFn = () => {
  const sessionFacade = inject(SessionFacade);
  const router = inject(Router);

  console.log("authGuard", sessionFacade.status);

  return toObservable(sessionFacade.status).pipe(
    filter(status => status !== "unknown" && status !== "checking"),
    take(1),
    map((status): boolean | UrlTree => {
      if (status === "authenticated") {
        console.log("no autenticado");
        return true;
      }

      return router.createUrlTree(["/auth/login"]);
    })
  );
};

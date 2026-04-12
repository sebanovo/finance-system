import { HttpErrorResponse, HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { catchError, switchMap, throwError } from "rxjs";

import { SessionRefreshCoordinatorService } from "../../session/services/session-refresh-coordinator.service";
import {
  HAS_RETRIED,
  SKIP_AUTH_REFRESH,
} from "../context/skip-auth-refresh.token";

/**
 * Returns true when the request must bypass the refresh flow.
 */
function shouldSkipRefresh(url: string | null): boolean {
  if (!url) {
    return false;
  }

  return (
    url.includes("/api/auth/login") ||
    url.includes("/api/auth/logout") ||
    url.includes("/api/auth/refresh")
  );
}

export const authRefreshInterceptor: HttpInterceptorFn = (req, next) => {
  const refreshCoordinator = inject(SessionRefreshCoordinatorService);

  if (req.context.get(SKIP_AUTH_REFRESH) || shouldSkipRefresh(req.url)) {
    return next(req);
  }

  return next(req).pipe(
    catchError((error: unknown) => {
      if (!(error instanceof HttpErrorResponse)) {
        return throwError(() => error);
      }

      if (error.status !== 401) {
        return throwError(() => error);
      }

      if (req.context.get(HAS_RETRIED)) {
        return throwError(() => error);
      }

      return refreshCoordinator.refresh().pipe(
        switchMap(() => {
          const retryRequest = req.clone({
            context: req.context.set(HAS_RETRIED, true),
          });

          return next(retryRequest);
        }),
        catchError((refreshError: unknown) => {
          return throwError(() => refreshError);
        })
      );
    })
  );
};

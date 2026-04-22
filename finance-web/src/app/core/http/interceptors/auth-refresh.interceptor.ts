import { HttpErrorResponse, HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { catchError, switchMap, throwError } from "rxjs";
import { SessionRefreshCoordinatorService } from "../../session/services/session-refresh-coordinator.service";
import {
  HAS_RETRIED_AUTH,
  SKIP_AUTH_REFRESH,
} from "../context/skip-auth-refresh.context";

/**
 * Determines whether the given URL should bypass the auth refresh flow.
 * This typically includes authentication-related endpoints to prevent infinite loops.
 * @param url The URL of the HTTP request.
 * @returns True if the URL should skip the auth refresh flow, false otherwise.
 */
function shouldSkipRefresh(url: string | null): boolean {
  if (!url) {
    return false;
  }

  return (
    url.includes("/api/auth/login") ||
    url.includes("/api/auth/login-with-tenant") ||
    url.includes("/api/auth/logout") ||
    url.includes("/api/auth/refresh")
  );
}

/**
 * Intercepts HTTP requests and refreshes authentication tokens when necessary.
 * @param req The HTTP request to intercept.
 * @param next The next interceptor in the chain.
 * @returns An observable that emits the HTTP response.
 */
export const authRefreshInterceptor: HttpInterceptorFn = (req, next) => {
  const sessionRefreshCoordinator = inject(SessionRefreshCoordinatorService);

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

      if (req.context.get(HAS_RETRIED_AUTH)) {
        return throwError(() => error);
      }

      return sessionRefreshCoordinator.refresh().pipe(
        switchMap(() => {
          const retryRequest = req.clone({
            context: req.context.set(HAS_RETRIED_AUTH, true),
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

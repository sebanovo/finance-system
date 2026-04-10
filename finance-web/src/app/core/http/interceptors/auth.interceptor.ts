import {
  HttpErrorResponse,
  HttpInterceptorFn,
  HttpRequest,
} from "@angular/common/http";
import { inject } from "@angular/core";
import { catchError, from, switchMap, throwError } from "rxjs";
import { AuthApi } from "../../../features/auth/api/auth.api";
import { SessionFacade } from "../../../session/state/session.facade";
import { HAS_RETRIED, SKIP_REFRESH } from "../context/http-context.tokens";

/**
 * Shared refresh promise to prevent multiple simultaneous refresh calls.
 */
let refreshPromise: Promise<void> | null = null;

/**
 * Executes refresh only once while concurrent unauthorized requests wait for it.
 */
async function ensureRefresh(authApi: AuthApi): Promise<void> {
  if (!refreshPromise) {
    refreshPromise = (async () => {
      await new Promise<void>((resolve, reject) => {
        authApi.refresh().subscribe({
          next: () => resolve(),
          error: (error: unknown) => reject(error),
        });
      });
    })().finally(() => {
      refreshPromise = null;
    });
  }

  return refreshPromise;
}

/**
 * Clones the request with credentials enabled.
 */
function withCredentials(req: HttpRequest<unknown>): HttpRequest<unknown> {
  return req.clone({
    withCredentials: true,
  });
}

/**
 * Returns true when the request should skip refresh handling.
 */
function shouldSkipRefresh(req: HttpRequest<unknown>): boolean {
  return req.context.get(SKIP_REFRESH);
}

/**
 * Returns true when the request has already been retried once.
 */
function hasBeenRetried(req: HttpRequest<unknown>): boolean {
  return req.context.get(HAS_RETRIED);
}

/**
 * Returns true when the response error is unauthorized.
 */
function isUnauthorizedError(error: unknown): error is HttpErrorResponse {
  return error instanceof HttpErrorResponse && error.status === 401;
}

/**
 * Marks a request as already retried.
 */
function markAsRetried(req: HttpRequest<unknown>): HttpRequest<unknown> {
  return req.clone({
    withCredentials: true,
    context: req.context.set(HAS_RETRIED, true),
  });
}

/**
 * Authentication interceptor.
 *
 * Adds credentials to every request and performs a single refresh-retry cycle
 * when a request fails with 401.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authApi = inject(AuthApi);
  const sessionFacade = inject(SessionFacade);

  const requestWithCredentials = withCredentials(req);

  return next(requestWithCredentials).pipe(
    catchError((error: unknown) => {
      if (!isUnauthorizedError(error)) {
        return throwError(() => error);
      }

      if (shouldSkipRefresh(requestWithCredentials)) {
        return throwError(() => error);
      }

      if (hasBeenRetried(requestWithCredentials)) {
        sessionFacade.setUnauthenticated();
        return throwError(() => error);
      }

      return from(ensureRefresh(authApi)).pipe(
        switchMap(() => {
          const retriedRequest = markAsRetried(requestWithCredentials);
          return next(retriedRequest);
        }),
        catchError((refreshError: unknown) => {
          sessionFacade.setUnauthenticated();
          return throwError(() => refreshError);
        })
      );
    })
  );
};

import { HttpInterceptorFn } from "@angular/common/http";
import { catchError, throwError } from "rxjs";

import { toAppHttpError } from "../mappers/to-app-http-error";

/**
 * Normalizes all HTTP errors into the application error format.
 *
 * This interceptor should be placed after auth recovery logic so the final
 * propagated error is already the normalized one.
 */
export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: unknown) => {
      return throwError(() => toAppHttpError(error));
    })
  );
};

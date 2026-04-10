import { HttpInterceptorFn } from "@angular/common/http";
import { catchError, throwError } from "rxjs";
import { toAppHttpError } from "../mappers/http-error.mapper";

/**
 * Normalizes all HTTP errors into the application error format.
 *
 * This interceptor should be placed after auth recovery logic so the final
 * propagated error is already the normalized one.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: unknown) => {
      return throwError(() => toAppHttpError(error));
    })
  );
};

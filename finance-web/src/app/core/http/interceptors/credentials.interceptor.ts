import { HttpInterceptorFn } from "@angular/common/http";

/**
 * Intercepts HTTP requests and sets the 'withCredentials' flag to true.
 * @param req The HTTP request to intercept.
 * @param next The next interceptor in the chain.
 * @returns An observable that emits the HTTP response.
 */
export const credentialsInterceptor: HttpInterceptorFn = (req, next) => {
  const requestWithCredentials = req.clone({
    withCredentials: true,
  });

  return next(requestWithCredentials);
};

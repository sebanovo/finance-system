import { HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";

import { AccessTokenService } from "../services/access-token.service";
import { HeaderTenantService } from "../services/header-tenant.service";

/**
 * Intercepts HTTP requests and adds authentication tokens to the request headers.
 * @param req The HTTP request to intercept.
 * @param next The next interceptor in the chain.
 * @returns An observable that emits the HTTP response.
 */
export const authTokenInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenService = inject(AccessTokenService);
  const headerTenantService = inject(HeaderTenantService);

  const session = tokenService.getTokens();
  const tenant = headerTenantService.getTenant();

  const headers: Record<string, string> = {};

  if (tenant) {
    headers["X-Tenant-Slug"] = tenant;
  }

  if (session?.accessToken && session?.tokenType) {
    headers["Authorization"] = `${session.tokenType} ${session.accessToken}`;
  }

  if (Object.keys(headers).length === 0) {
    return next(req);
  }

  return next(
    req.clone({
      setHeaders: headers,
    })
  );
};

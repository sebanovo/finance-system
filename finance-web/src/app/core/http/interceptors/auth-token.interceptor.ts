import { HttpInterceptorFn } from "@angular/common/http";

export const authTokenInterceptor: HttpInterceptorFn = (req, next) => {
  let session;
  const rawSession = localStorage.getItem("session");
  try {
    session = rawSession ? JSON.parse(rawSession) : null;
  } catch {
    session = null;
  }

  const tenant = localStorage.getItem("tenant");

  const headers: Record<string, string> = {};

  if (tenant) {
    headers["X-Tenant-Slug"] = tenant;
  }

  if (session?.accessToken) {
    headers["Authorization"] = `Bearer ${session.accessToken}`;
  }

  return next(
    req.clone({
      setHeaders: headers,
    })
  );
};

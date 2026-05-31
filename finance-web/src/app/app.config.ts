import { provideHttpClient, withFetch, withInterceptors } from "@angular/common/http";
import { ApplicationConfig, inject, provideAppInitializer, provideBrowserGlobalErrorListeners } from "@angular/core";
import { provideRouter } from "@angular/router";
import { routes } from "./app.routes";
import { authRefreshInterceptor } from "./core/http/interceptors/auth-refresh.interceptor";
import { authTokenInterceptor } from "./core/http/interceptors/auth-token.interceptor";
import { httpErrorInterceptor } from "./core/http/interceptors/http-error.interceptor";
import { SessionBootstrapService } from "./core/session/services/session-bootstrap.service";

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(
      withFetch(),
      withInterceptors([
        authTokenInterceptor, 
        // credentialsInterceptor,
        authRefreshInterceptor,
        httpErrorInterceptor,
      ])
    ),
    provideAppInitializer(() => {
      const sessionBootstrapService = inject(SessionBootstrapService);
      return sessionBootstrapService.runBootstrap();
    }),
  ],
};
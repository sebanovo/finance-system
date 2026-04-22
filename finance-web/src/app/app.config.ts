import {
  provideHttpClient,
  withFetch,
  withInterceptors,
} from "@angular/common/http";
import {
  APP_INITIALIZER,
  ApplicationConfig,
  inject,
  provideBrowserGlobalErrorListeners,
} from "@angular/core";
import { provideRouter } from "@angular/router";

import { routes } from "./app.routes";
import { tenantContextInterceptor } from "./core/http/interceptors/tenant-context.interceptor";
import { authRefreshInterceptor } from "./core/http/interceptors/auth-refresh.interceptor";
import { authTokenInterceptor } from "./core/http/interceptors/auth-token.interceptor";
// import { credentialsInterceptor } from "./core/http/interceptors/credentials.interceptor";
import { httpErrorInterceptor } from "./core/http/interceptors/http-error.interceptor";
import { SessionBootstrapService } from "./core/session/services/session-bootstrap.service";

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(), // catch global errors
    provideRouter(routes),

    provideHttpClient(
      withFetch(),
      withInterceptors([
        tenantContextInterceptor,
        authTokenInterceptor,
        // credentialsInterceptor,
        authRefreshInterceptor,
        httpErrorInterceptor,
      ])
    ),
    {
      provide: APP_INITIALIZER,
      useFactory: () => {
        const sessionBootstrap = inject(SessionBootstrapService);
        return () => sessionBootstrap.runBootstrap();
      },
      multi: true,
    },
  ],
};

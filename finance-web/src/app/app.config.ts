import {
  provideHttpClient,
  withFetch,
  withInterceptors,
} from "@angular/common/http";
import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
} from "@angular/core";
import { provideRouter } from "@angular/router";

import { routes } from "./app.routes";
import { authRefreshInterceptor } from "./core/http/interceptors/auth-refresh.interceptor";
import { credentialsInterceptor } from "./core/http/interceptors/credentials.interceptor";
import { httpErrorInterceptor } from "./core/http/interceptors/http-error.interceptor";

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(), // catch global errors
    provideRouter(routes),

    provideHttpClient(
      withFetch(),
      withInterceptors([
        credentialsInterceptor,
        authRefreshInterceptor,
        httpErrorInterceptor,
      ])
    ),
  ],
};

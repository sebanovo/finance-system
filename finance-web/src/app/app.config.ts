import { provideHttpClient, withInterceptors } from "@angular/common/http";
import {
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
} from "@angular/core";
import { provideRouter } from "@angular/router";

import { authInterceptor } from "./core/http/interceptors/auth.interceptor";
import { errorInterceptor } from "./core/http/interceptors/error.interceptor";
import { SessionBootstrap } from "./session/state/session.bootstrap";

import { routes } from "./app.routes";

/**
 * Runs session bootstrap before the application becomes fully available.
 */
function initializeSession(): Promise<void> {
  return inject(SessionBootstrap).runSafely();
}

export const appConfig: ApplicationConfig = {
  providers: [
    // provideHttpClient(),
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),

    provideHttpClient(withInterceptors([authInterceptor, errorInterceptor])),
    provideAppInitializer(initializeSession),
  ],
};

import { inject, Injectable } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { toAppHttpError } from "../../core/http/mappers/http-error.mapper";
import { AuthApi } from "../../features/auth/api/auth.api";
import { SessionFacade } from "./session.facade";

/**
 * Handles the initial session bootstrap when the application starts.
 */
@Injectable({
  providedIn: "root",
})
export class SessionBootstrap {
  private readonly authApi = inject(AuthApi);

  private readonly sessionFacade = inject(SessionFacade);

  /**
   * Resolves the current session state.
   *
   * Flow:
   * 1. Try /auth/me
   * 2. If unauthorized, try /auth/refresh
   * 3. Retry /auth/me
   * 4. If it still fails, mark session as unauthenticated
   */
  async run(): Promise<void> {
    this.sessionFacade.setChecking();

    try {
      const meResponse = await firstValueFrom(this.authApi.getMe());
      this.sessionFacade.setAuthenticated(meResponse.user);
      return;
    } catch (error: unknown) {
      const appError = toAppHttpError(error);
      console.log("Error /auth/me", appError);

      if (appError.code !== "UNAUTHORIZED") {
        this.sessionFacade.setUnauthenticated();
        throw appError;
      }
    }

    try {
      await firstValueFrom(this.authApi.refresh());

      const meResponse = await firstValueFrom(this.authApi.getMe());
      this.sessionFacade.setAuthenticated(meResponse.user);
    } catch (error: unknown) {
      console.log("set unauthenticated");
      this.sessionFacade.setUnauthenticated();
      throw toAppHttpError(error);
    }
  }

  /**
   * Safe bootstrap variant for app startup.
   *
   * Useful when the app should not crash during initialization.
   */
  async runSafely(): Promise<void> {
    try {
      await this.run();
    } catch {
      this.sessionFacade.setUnauthenticated();
    }
  }
}

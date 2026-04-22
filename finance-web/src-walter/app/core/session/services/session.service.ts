import { inject, Injectable } from "@angular/core";
import { catchError, firstValueFrom, map, Observable, of } from "rxjs";
import { AuthMeData } from "../../../features/auth/models/auth-request.type";
import { AuthService } from "../../../features/auth/services/auth.service";
import { AppHttpError } from "../../http/models/app-http-error.model";
import { SessionBootstrapResult } from "../model/session-result.type";

@Injectable({
  providedIn: "root",
})
export class SessionService {
  private readonly authService = inject(AuthService);

  /**
   * Bootstraps the user's session by checking for existing authentication tokens
   * and validating them against the current authenticated user endpoint.
   */
  bootstrapSession(): Observable<SessionBootstrapResult> {
    if (!this.authService.hasTokens()) {
      return of({
        status: "unauthenticated",
        errorMessage: null,
      });
    }

    return this.authService.getMe().pipe(
      map(user => ({
        status: "authenticated" as const,
        user,
      })),
      catchError((error: unknown) => {
        void firstValueFrom(this.authService.logout()).catch(() => undefined);

        return of({
          status: "unauthenticated" as const,
          errorMessage: this.toErrorMessage(error),
        });
      })
    );
  }

  /**
   * Loads the current authenticated user.
   */
  loadCurrentUser(): Observable<AuthMeData> {
    return this.authService.getMe();
  }

  /**
   * Clears the current session on the server side.
   */
  async clearSession(): Promise<void> {
    try {
      await firstValueFrom(this.authService.logout());
    } catch {
      // Ignore logout errors during local cleanup flows.
    }
  }

  hasSession(): boolean {
    return this.authService.hasTokens();
  }

  private toErrorMessage(error: unknown): string | null {
    const appError = error as AppHttpError | null;

    if (!appError) {
      return null;
    }

    const message =
      typeof appError.message === "string" ? appError.message.trim() : "";

    return message.length > 0 ? message : null;
  }

  /**
   * Bootstraps the user's session by checking for existing authentication tokens and validating them.
   * @returns An observable that emits the bootstrap result.
   */
  // bootstrapSession(): Observable<SessionBootstrapResult> {
  //   if (!this.authService.hasTokens()) {
  //     return of({
  //       status: "unauthenticated",
  //       errorMessage: null,
  //     });
  //   }

  //   return this.authService.getMe().pipe(
  //     map(user => ({
  //       status: "authenticated" as const,
  //       user: this.toSessionUser(user),
  //     })),
  //     catchError((error: unknown) => {
  //       this.authService.logout();

  //       return of({
  //         status: "unauthenticated" as const,
  //         errorMessage: this.toErrorMessage(error),
  //       });
  //     })
  //   );
  // }

  /**
   * Loads the current user's session information.
   * @returns An observable that emits the session user.
   */
  // loadCurrentUser(): Observable<SessionUser> {
  //   return this.authService.getMe().pipe(map(user => this.toSessionUser(user)));
  // }

  // clearSession(): void {
  //   this.authService.logout();
  // }

  // hasSession(): boolean {
  //   return this.authService.hasTokens();
  // }

  /**
   * Converts an AuthMeData object to a SessionUser object.
   * @param user The authenticated user's information to convert to a SessionUser object.
   * @returns A SessionUser object containing the user's session information.
   */
  // private toSessionUser(user: AuthMeData | AuthMeAdminData): SessionUser {
  //   return {
  //     id: user.id,
  //     email: user.email,
  //     firstName: user.firstName,
  //     lastName: user.lastName,
  //     active: user.active,

  //     status: "status" in user ? user.status : undefined,
  //     tenantSlug: "tenantSlug" in user ? user.tenantSlug : undefined,
  //     roles: user.roles,
  //     createdAt: "createdAt" in user ? user.createdAt : undefined,
  //     updatedAt: "updatedAt" in user ? user.updatedAt : undefined,
  //   };
  // }

  /**
   * Converts an error object to a user-friendly error message.
   * @param error The error object to convert.
   * @returns A user-friendly error message, or null if no message is available.
   */
  // private toErrorMessage(error: unknown): string | null {
  //   const appError = error as AppHttpError | null;

  //   if (!appError) {
  //     return null;
  //   }

  //   const message =
  //     typeof appError.message === "string" ? appError.message.trim() : "";

  //   return message.length > 0 ? message : null;
  // }
}

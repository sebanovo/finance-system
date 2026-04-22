import { inject, Injectable } from "@angular/core";
import { catchError, map, Observable, of } from "rxjs";
import { AuthMeData } from "../../../features/auth/models/auth-request.type";
import { AuthService } from "../../../features/auth/services/auth.service";
import { AppHttpError } from "../../http/models/app-http-error.model";
import { SessionBootstrapResult } from "../model/session-result.type";
import { SessionUser } from "../model/session-user.type";

@Injectable({
  providedIn: "root",
})
export class SessionService {
  private readonly authService = inject(AuthService);

  /**
   * Bootstraps the user's session by checking for existing authentication tokens and validating them.
   * @returns An observable that emits the bootstrap result.
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
        user: this.toSessionUser(user),
      })),
      catchError((error: unknown) => {
        this.authService.logout();
        return of({
          status: "unauthenticated" as const,
          errorMessage: this.toErrorMessage(error),
        });
      })
    );
  }

  /**
   * Loads the current user's session information.
   * @returns An observable that emits the user's session data.
   */
  loadCurrentUser(): Observable<SessionUser> {
    return this.authService.getMe().pipe(map(user => this.toSessionUser(user)));
  }

  clearSession(): void {
    this.authService.logout();
  }

  hasSession(): boolean {
    return this.authService.hasTokens();
  }

  getTenant(): string | null {
    return this.authService.getStoredTenant();
  }

  private toSessionUser(user: AuthMeData): SessionUser {
    return {
      id: user.id,
      email: user.email,
      firstName: user.firstName,
      lastName: user.lastName,
      active: user.active,
      status: user.status,
      tenantSlug: user.tenantSlug,
      roles: user.roles,
    };
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
}

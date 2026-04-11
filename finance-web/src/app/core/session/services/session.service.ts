import { inject, Injectable } from "@angular/core";
import { catchError, Observable, of, switchMap, tap, throwError } from "rxjs";
import { SessionApi } from "../api/session.api";
import { AuthUser, LoginRequest } from "../model/auth-user.type";
import { SessionStore } from "../store/session.store";

@Injectable({
  providedIn: "root",
})
export class SessionService {
  private readonly sessionApi = inject(SessionApi);
  private readonly sessionStore = inject(SessionStore);

  /**
   * Authenticates the user, then loads the current session user.
   */
  login(payload: LoginRequest): Observable<AuthUser> {
    this.sessionStore.setLoading();

    return this.sessionApi.login(payload).pipe(
      switchMap(() => this.sessionApi.getMe()),
      tap(user => {
        this.sessionStore.setAuthenticated(user);
      }),
      catchError((error: unknown) => {
        this.sessionStore.setUnauthenticated();
        return throwError(() => error);
      })
    );
  }

  /**
   * Loads the currently authenticated user and stores it.
   */
  loadMe(): Observable<AuthUser> {
    return this.sessionApi.getMe().pipe(
      tap(user => {
        this.sessionStore.setAuthenticated(user);
      })
    );
  }

  /**
   * Rebuilds session state on application startup.
   */
  bootstrap(): Observable<AuthUser | null> {
    if (this.sessionStore.isBootstrapping()) {
      return of(this.sessionStore.user());
    }

    if (this.sessionStore.isInitialized()) {
      return of(this.sessionStore.user());
    }

    this.sessionStore.setBootstrapStarted();

    return this.sessionApi.getMe().pipe(
      tap(user => {
        this.sessionStore.setAuthenticated(user);
      }),
      catchError(() => {
        this.sessionStore.setUnauthenticated();
        return of(null);
      })
    );
  }

  /**
   * Logs out the current user and clears local session state.
   */
  logout(): Observable<void> {
    this.sessionStore.setLoading();

    return this.sessionApi.logout().pipe(
      tap(() => {
        this.sessionStore.clearSession();
      })
    );
  }

  /**
   * Terminates the current session.
   * It tries backend logout, but local cleanup always happens.
   */
  terminateSession(): Observable<void> {
    return this.sessionApi.logout().pipe(
      tap(() => {
        this.sessionStore.clearSession();
      }),
      catchError(() => {
        this.sessionStore.clearSession();
        return of(void 0);
      })
    );
  }

  /**
   * Clears only local session state.
   */
  clearSession(): void {
    this.sessionStore.clearSession();
  }
}

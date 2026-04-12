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
      tap(loginResponse => {
        // Login logs
        console.log("Login response: ", loginResponse);
        this.sessionStore.setTokens(loginResponse.data);
      }),
      switchMap(() => this.sessionApi.getMe()),
      tap(user => {
        // getMe() logs
        console.log("getMe response: ", user);
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
        console.log("loadMe response", user);
        this.sessionStore.setAuthenticated(user);
      }),
      catchError(err => {
        this.sessionStore.clearSession();
        return throwError(() => err);
      })
    );
  }

  /**
   * Rebuilds session state on application startup.
   */
  bootstrap(): Observable<AuthUser | null> {
    this.sessionStore.setBootstrapStarted();
    console.log("bootstraping");
    // temporal
    const session = this.sessionStore.getSessionFromStorage();

    if (!session?.accessToken) {
      this.sessionStore.clearSession();
      this.removeTenant();
      return of(null);
    }

    this.sessionStore.setTokens({
      accessToken: session.accessToken,
      refreshToken: session.refreshToken,
      accessExpiresInMs: session.expiresAt - Date.now(),
    });

    // if (this.sessionStore.isBootstrapping()) {
    //   return of(this.sessionStore.user());
    // }
    // if (this.sessionStore.isInitialized()) {
    //   return of(this.sessionStore.user());
    // }
    // this.sessionStore.setBootstrapStarted();

    return this.sessionApi.getMe().pipe(
      tap(user => {
        // logs
        console.log("bootstrap getMe response", user);
        this.sessionStore.setAuthenticated(user);
        console.log(
          "usuario cargado getMe",
          this.sessionStore.isAuthenticated()
        );
      }),
      catchError(() => {
        console.log("bootstrap clear session");
        this.sessionStore.clearSession();
        this.removeTenant();
        // this.sessionStore.setUnauthenticated();
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
        console.log("Logout.");
        this.sessionStore.clearSession();
        this.removeTenant();
      }),
      catchError(() => {
        this.sessionStore.clearSession();
        this.removeTenant();
        return of(void 0);
      })
    );
  }

  /**
   * Terminates the current session.
   * It tries backend logout, but local cleanup always happens.
   */
  terminateSession(): Observable<void> {
    console.log("terminate session");
    return this.sessionApi.logout().pipe(
      tap(() => {
        this.sessionStore.clearSession();
        this.removeTenant();
      }),
      catchError(() => {
        this.sessionStore.clearSession();
        this.removeTenant();
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

  setTenant(tenant: string): void {
    localStorage.setItem("tenant", tenant);
  }

  getTenant(): string | null {
    return localStorage.getItem("tenant");
  }

  removeTenant(): void {
    localStorage.removeItem("tenant");
  }
}

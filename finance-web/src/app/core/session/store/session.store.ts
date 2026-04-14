import { computed, inject, Injectable, signal } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { SessionBootstrapResult } from "../model/session-result.type";
import { SessionState } from "../model/session-state.type";
import { SessionUser } from "../model/session-user.type";
import { SessionService } from "../services/session.service";

@Injectable({
  providedIn: "root",
})
export class SessionStore {
  private readonly sessionService = inject(SessionService);

  private readonly stateSignal = signal<SessionState>({
    status: "idle",
    initialized: false,
    user: null,
    errorMessage: null,
  });

  readonly state = this.stateSignal.asReadonly();

  readonly status = computed(() => this.stateSignal().status);
  readonly initialized = computed(() => this.stateSignal().initialized);
  readonly user = computed(() => this.stateSignal().user);
  readonly errorMessage = computed(() => this.stateSignal().errorMessage);

  readonly isIdle = computed(() => this.stateSignal().status === "idle");

  readonly isBootstrapping = computed(
    () => this.stateSignal().status === "bootstrapping"
  );

  readonly isAuthenticated = computed(
    () => this.stateSignal().status === "authenticated"
  );

  readonly isUnauthenticated = computed(
    () => this.stateSignal().status === "unauthenticated"
  );

  readonly isReady = computed(() => this.stateSignal().initialized);

  /**
   * Computes the full name of the authenticated user.
   * This is a convenience computed property that combines the first and last name of the user.
   * It returns null if there is no authenticated user.
   */
  readonly userFullName = computed(() => {
    const user = this.stateSignal().user;

    if (!user) {
      return null;
    }

    return `${user.firstName} ${user.lastName}`.trim();
  });

  readonly roles = computed(() => this.stateSignal().user?.roles ?? []);

  /**
   * Bootstraps the session on application startup.
   * This will check if there is a valid session and load the current user if authenticated.
   */
  async bootstrap(): Promise<void> {
    this.startBootstrap();

    try {
      const result = await firstValueFrom(
        this.sessionService.bootstrapSession()
      );
      this.applyBootstrapResult(result);
    } catch {
      this.setUnauthenticated("Unexpected session bootstrap error.");
    }
  }

  /**
   * Loads the current user data.
   * This can be used to refresh the user data after login or when the app is already running.
   */
  async loadUser(): Promise<void> {
    try {
      const user = await firstValueFrom(this.sessionService.loadCurrentUser());
      this.setAuthenticated(user);
    } catch {
      this.setUnauthenticated();
    }
  }

  /**
   * Sets the session as authenticated.
   * @param user The authenticated user data.
   */
  setAuthenticated(user: SessionUser): void {
    this.patchState({
      status: "authenticated",
      initialized: true,
      user,
      errorMessage: null,
    });
  }

  /**
   * Sets the session as unauthenticated.
   * @param errorMessage Optional error message to set when the session is unauthenticated.
   * This can be used to provide feedback on why the session is not authenticated
   * (e.g. session expired, invalid credentials, etc.).
   */
  setUnauthenticated(errorMessage: string | null = null): void {
    this.patchState({
      status: "unauthenticated",
      initialized: true,
      user: null,
      errorMessage,
    });
  }

  /**
   * Clears the current session and sets the state to unauthenticated.
   * This should be called on logout to clear the session data and update the state accordingly.
   */
  clearSession(): void {
    this.sessionService.clearSession();

    this.patchState({
      status: "unauthenticated",
      initialized: true,
      user: null,
      errorMessage: null,
    });
  }

  /**
   * Resets the session state to its initial values.
   * This can be used for testing purposes or to completely reset the session state in the application.
   */
  reset(): void {
    this.stateSignal.set({
      status: "idle",
      initialized: false,
      user: null,
      errorMessage: null,
    });
  }

  private startBootstrap(): void {
    this.patchState({
      status: "bootstrapping",
      initialized: false,
      errorMessage: null,
    });
  }

  private applyBootstrapResult(result: SessionBootstrapResult): void {
    if (result.status === "authenticated") {
      this.setAuthenticated(result.user);
      return;
    }

    this.setUnauthenticated(result.errorMessage);
  }

  private patchState(patch: Partial<SessionState>): void {
    this.stateSignal.update(currentState => ({
      ...currentState,
      ...patch,
    }));
  }
}

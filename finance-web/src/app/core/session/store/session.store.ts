import { computed, inject, Injectable, signal } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { AuthMeData } from "../../../features/auth/models/auth-request.type";
import { SessionBootstrapResult } from "../model/session-result.type";
import { SessionState } from "../model/session-state.type";
import { SessionService } from "../services/session.service";

@Injectable({
  providedIn: "root",
})
export class SessionStore {
  private readonly sessionService = inject(SessionService);

  private readonly initialState: SessionState = {
    status: "idle",
    initialized: false,
    user: null,
    errorMessage: null,
  };

  private readonly stateSignal = signal<SessionState>(this.initialState);

  readonly state = this.stateSignal.asReadonly();

  readonly status = computed(() => this.stateSignal().status);
  readonly initialized = computed(() => this.stateSignal().initialized);
  readonly user = computed(() => this.stateSignal().user);
  readonly errorMessage = computed(() => this.stateSignal().errorMessage);

  readonly isIdle = computed(() => this.status() === "idle");
  readonly isBootstrapping = computed(() => this.status() === "bootstrapping");
  readonly isAuthenticated = computed(() => this.status() === "authenticated");
  readonly isUnauthenticated = computed(
    () => this.status() === "unauthenticated"
  );
  readonly isReady = computed(() => this.initialized());

  readonly isAdmin = computed(() => {
    const user = this.user();
    return (
      ["SUPERADMIN", "ADMIN"].some(role => user?.roles?.includes(role)) || false
    );
  });

  readonly userFullName = computed(() => {
    const user = this.user();

    if (!user) {
      return null;
    }

    return `${user.firstName} ${user.lastName}`.trim();
  });

  readonly roles = computed(() => this.user()?.roles ?? []);

  /**
   * Bootstraps the session on application startup.
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
   * Reloads the current authenticated user.
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
   * Marks the session as authenticated with the provided user.
   */
  setAuthenticated(user: AuthMeData): void {
    this.patchState({
      status: "authenticated",
      initialized: true,
      user,
      errorMessage: null,
    });
  }

  /**
   * Marks the session as unauthenticated.
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
   * Clears only the local session state.
   * Remote logout should be handled by the auth flow before calling this.
   */
  clearSession(): void {
    this.patchState({
      status: "unauthenticated",
      initialized: true,
      user: null,
      errorMessage: null,
    });
  }

  /**
   * Resets the store to its initial state.
   */
  reset(): void {
    this.stateSignal.set(this.initialState);
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

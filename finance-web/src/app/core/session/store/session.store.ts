import { computed, Injectable, signal } from "@angular/core";

import { AuthUser } from "../model/auth-user.type";
import { SessionState } from "../model/session-state.type";

const initialState: SessionState = {
  user: null,
  status: "idle",
  isBootstrapping: false,
  isInitialized: false,
  bootstrapError: null,
};

@Injectable({
  providedIn: "root",
})
export class SessionStore {
  private readonly state = signal<SessionState>(initialState);

  readonly user = computed(() => this.state().user);
  readonly status = computed(() => this.state().status);
  readonly isBootstrapping = computed(() => this.state().isBootstrapping);
  readonly isInitialized = computed(() => this.state().isInitialized);
  readonly bootstrapError = computed(() => this.state().bootstrapError);

  readonly isAuthenticated = computed(
    () => this.state().status === "authenticated"
  );

  readonly isUnauthenticated = computed(
    () => this.state().status === "unauthenticated"
  );

  readonly isLoading = computed(
    () => this.state().status === "loading" || this.state().isBootstrapping
  );

  readonly hasSession = computed(() => this.state().user !== null);

  setLoading(): void {
    this.state.update(currentState => ({
      ...currentState,
      status: "loading",
      bootstrapError: null,
    }));
  }

  setBootstrapStarted(): void {
    this.state.update(currentState => ({
      ...currentState,
      status: "loading",
      isBootstrapping: true,
      bootstrapError: null,
    }));
  }

  markInitialized(): void {
    this.state.update(currentState => ({
      ...currentState,
      isInitialized: true,
      isBootstrapping: false,
    }));
  }

  setAuthenticated(user: AuthUser): void {
    this.state.update(currentState => ({
      ...currentState,
      user,
      status: "authenticated",
      isBootstrapping: false,
      isInitialized: true,
      bootstrapError: null,
    }));
  }

  setUnauthenticated(): void {
    this.state.update(currentState => ({
      ...currentState,
      user: null,
      status: "unauthenticated",
      isBootstrapping: false,
      isInitialized: true,
      bootstrapError: null,
    }));
  }

  setBootstrapError(message: string | null): void {
    this.state.update(currentState => ({
      ...currentState,
      bootstrapError: message,
      isBootstrapping: false,
      isInitialized: true,
    }));
  }

  clearSession(): void {
    this.state.set({
      user: null,
      status: "unauthenticated",
      isBootstrapping: false,
      isInitialized: true,
      bootstrapError: null,
    });
  }

  reset(): void {
    this.state.set(initialState);
  }
}

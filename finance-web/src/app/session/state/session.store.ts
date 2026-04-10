import { computed, Injectable, signal } from "@angular/core";
import type {
  SessionState,
  SessionStatus,
  SessionUser,
} from "../models/session.types";

/**
 * Initial session state.
 */
const initialSessionState: SessionState = {
  status: "unknown",
  user: null,
  initialized: false,
};

/**
 * Global session store.
 *
 * Keeps the authenticated user and the current session lifecycle state.
 */
@Injectable({
  providedIn: "root",
})
export class SessionStore {
  private readonly state = signal<SessionState>(initialSessionState);

  /**
   * Full session state as readonly computed signal.
   */
  readonly sessionState = computed(() => this.state());

  /**
   * Current session status.
   */
  readonly status = computed(() => this.state().status);

  /**
   * Current authenticated user.
   */
  readonly user = computed(() => this.state().user);

  /**
   * Indicates whether session bootstrap has already completed at least once.
   */
  readonly initialized = computed(() => this.state().initialized);

  /**
   * True when the user is authenticated.
   */
  readonly isAuthenticated = computed(
    () => this.state().status === "authenticated"
  );

  /**
   * True when the app is currently checking the session.
   */
  readonly isChecking = computed(() => this.state().status === "checking");

  /**
   * Returns the current user roles.
   */
  readonly roles = computed(() => this.state().user?.roles ?? []);

  /**
   * Returns the current user permissions.
   */
  readonly permissions = computed(() => this.state().user?.permissions ?? []);

  /**
   * Sets session status.
   */
  setStatus(status: SessionStatus): void {
    this.state.update(current => ({
      ...current,
      status,
    }));
  }

  /**
   * Sets state to checking before bootstrap or protected requests.
   */
  setChecking(): void {
    this.state.update(current => ({
      ...current,
      status: "checking",
    }));
  }

  /**
   * Stores the authenticated user and marks the session as initialized.
   */
  setAuthenticated(user: SessionUser): void {
    this.state.set({
      status: "authenticated",
      user,
      initialized: true,
    });
  }

  /**
   * Clears the current user and marks the session as initialized.
   */
  setUnauthenticated(): void {
    this.state.set({
      status: "unauthenticated",
      user: null,
      initialized: true,
    });
  }

  /**
   * Resets the store to its initial state.
   */
  reset(): void {
    this.state.set(initialSessionState);
  }

  /**
   * Checks whether the current user has the specified role.
   */
  hasRole(role: string): boolean {
    return this.roles().includes(role);
  }

  /**
   * Checks whether the current user has the specified permission.
   */
  hasPermission(permission: string): boolean {
    return this.permissions().includes(permission);
  }
}

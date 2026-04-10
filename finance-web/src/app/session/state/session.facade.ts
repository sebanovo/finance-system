import { computed, inject, Injectable } from "@angular/core";
import type { SessionStatus, SessionUser } from "../models/session.types";
import { SessionStore } from "./session.store";

/**
 * Public session facade used by the rest of the application.
 *
 * This keeps consumers decoupled from the internal store implementation.
 */
@Injectable({
  providedIn: "root",
})
export class SessionFacade {
  /**
   * Use `inject()` to obtain the instance of `SessionStore`
   */
  private readonly sessionStore = inject(SessionStore);

  /**
   * Full readonly session state.
   */
  readonly sessionState = this.sessionStore.sessionState;

  /**
   * Current session status.
   */
  readonly status = this.sessionStore.status;

  /**
   * Current authenticated user.
   */
  readonly user = this.sessionStore.user;

  /**
   * Whether the session bootstrap already ran.
   */
  readonly initialized = this.sessionStore.initialized;

  /**
   * Whether the current session is authenticated.
   */
  readonly isAuthenticated = this.sessionStore.isAuthenticated;

  /**
   * Whether the app is currently checking the session.
   */
  readonly isChecking = this.sessionStore.isChecking;

  /**
   * Current user roles.
   */
  readonly roles = this.sessionStore.roles;

  /**
   * Current user permissions.
   */
  readonly permissions = this.sessionStore.permissions;

  /**
   * Whether there is no authenticated session.
   */
  readonly isUnauthenticated = computed(
    () => this.status() === "unauthenticated"
  );

  /**
   * Whether the session state is still unknown.
   */
  readonly isUnknown = computed(() => this.status() === "unknown");

  /**
   * Updates the current session status.
   */
  setStatus(status: SessionStatus): void {
    this.sessionStore.setStatus(status);
  }

  /**
   * Marks the session as being checked.
   */
  setChecking(): void {
    this.sessionStore.setChecking();
  }

  /**
   * Stores the authenticated user.
   */
  setAuthenticated(user: SessionUser): void {
    this.sessionStore.setAuthenticated(user);
  }

  /**
   * Clears the authenticated session.
   */
  setUnauthenticated(): void {
    this.sessionStore.setUnauthenticated();
  }

  /**
   * Resets the session store to its initial state.
   */
  reset(): void {
    this.sessionStore.reset();
  }

  /**
   * Checks whether the authenticated user has the specified role.
   */
  hasRole(role: string): boolean {
    return this.sessionStore.hasRole(role);
  }

  /**
   * Checks whether the authenticated user has the specified permission.
   */
  hasPermission(permission: string): boolean {
    return this.sessionStore.hasPermission(permission);
  }
}

import { computed, inject, Injectable, signal } from "@angular/core";
import { firstValueFrom } from "rxjs";

import { AppHttpError } from "../../../core/http/models/app-http-error.model";
import { SessionStore } from "../../../core/session/store/session.store";
import {
  AuthMeData,
  LoginRequest,
  LoginTenantRequest,
} from "../models/auth-request.type";
import { SignupRequest } from "../models/signup-request.type";
import { AuthService } from "../services/auth.service";

@Injectable({
  providedIn: "root",
})
export class AuthStore {
  private readonly authService = inject(AuthService);
  private readonly sessionStore = inject(SessionStore);

  private readonly loadingSignal = signal(false);
  private readonly errorSignal = signal<AppHttpError | null>(null);
  private readonly successSignal = signal(false);
  private readonly userSignal = signal<AuthMeData | null>(null);

  readonly loading = this.loadingSignal.asReadonly();
  readonly error = this.errorSignal.asReadonly();
  readonly success = this.successSignal.asReadonly();
  readonly user = this.userSignal.asReadonly();

  readonly hasError = computed(() => this.errorSignal() !== null);
  readonly isAuthenticated = computed(() => this.userSignal() !== null);
  readonly errorMessage = computed(() => this.errorSignal()?.message ?? null);

  /**
   * Registers a new user and creates a tenant.
   * @param payload The signup credentials (company and admin info).
   * @returns A promise that resolves to a boolean indicating whether signup was successful.
   */
  async signup(payload: SignupRequest): Promise<boolean> {
    this.startRequest();

    try {
      await firstValueFrom(this.authService.signup(payload));

      const user = await firstValueFrom(this.authService.getMe());
      this.userSignal.set(user);

      // Update session store with authenticated user
      this.sessionStore.setAuthenticated(user);

      this.successSignal.set(true);
      return true;
    } catch (error: unknown) {
      this.errorSignal.set(error as AppHttpError);
      return false;
    } finally {
      this.loadingSignal.set(false);
    }
  }

  /**
   * Logs in a user with the provided credentials.
   * @param payload The login credentials for the user.
   * @returns A promise that resolves to a boolean indicating whether the login was successful.
   */
  async login(payload: LoginRequest): Promise<boolean> {
    this.startRequest();

    try {
      await firstValueFrom(this.authService.login(payload));

      const user = await firstValueFrom(this.authService.getMe());
      this.userSignal.set(user);

      // Update session store with authenticated user
      this.sessionStore.setAuthenticated(user);

      this.successSignal.set(true);
      return true;
    } catch (error: unknown) {
      this.errorSignal.set(error as AppHttpError);
      return false;
    } finally {
      this.loadingSignal.set(false);
    }
  }

  /**
   * Logs in a user with a specific tenant.
   * @param payload The login credentials along with the tenant slug.
   * @returns A promise that resolves to a boolean indicating whether the login was successful.
   */
  async loginWithTenant(payload: LoginTenantRequest): Promise<boolean> {
    this.startRequest();

    try {
      await firstValueFrom(this.authService.loginWithTenant(payload));

      const user = await firstValueFrom(this.authService.getMe());
      this.userSignal.set(user);

      // Update session store with authenticated user
      this.sessionStore.setAuthenticated(user);

      this.successSignal.set(true);
      return true;
    } catch (error: unknown) {
      this.errorSignal.set(error as AppHttpError);
      return false;
    } finally {
      this.loadingSignal.set(false);
    }
  }

  /**
   * Logs out the current user and resets the authentication state.
   */
  logout(): void {
    this.authService.logout();
    this.reset();
  }

  /**
   * Clears any existing authentication errors from the state.
   */
  clearError(): void {
    this.errorSignal.set(null);
  }

  /**
   * Resets the authentication state to its initial values, clearing any user data, errors, and success flags.
   */
  reset(): void {
    this.loadingSignal.set(false);
    this.errorSignal.set(null);
    this.successSignal.set(false);
    this.userSignal.set(null);
  }

  /**
   * Starts an authentication-related request by setting the loading state to true,
   */
  private startRequest(): void {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);
    this.successSignal.set(false);
  }
}

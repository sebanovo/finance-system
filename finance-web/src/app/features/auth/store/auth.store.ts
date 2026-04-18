import { computed, inject, Injectable, signal } from "@angular/core";
import { firstValueFrom } from "rxjs";

import { Router } from "@angular/router";
import { AppHttpError } from "../../../core/http/models/app-http-error.model";
import { AccessTokenService } from "../../../core/http/services/access-token.service";
import { AdminAccessTokenService } from "../../../core/http/services/admin-access-token.service";
import { SessionStore } from "../../../core/session/store/session.store";
import { toAuthMe } from "../adapters/auth.adapter";
import {
  AuthMeData,
  LoginRequest,
  LoginTenantRequest,
} from "../models/auth-request.type";
import { AuthService } from "../services/auth.service";

@Injectable({
  providedIn: "root",
})
export class AuthStore {
  private readonly authService = inject(AuthService);
  private readonly sessionStore = inject(SessionStore);
  private readonly adminService = inject(AdminAccessTokenService);
  private readonly accessService = inject(AccessTokenService);
  private readonly router = inject(Router);

  private readonly loadingSignal = signal(false);
  private readonly errorSignal = signal<AppHttpError | null>(null);
  private readonly userSignal = signal<AuthMeData | null>(null);
  private readonly adminSignal = signal<AuthMeData | null>(null);

  readonly loading = this.loadingSignal.asReadonly();
  readonly error = this.errorSignal.asReadonly();
  readonly user = this.userSignal.asReadonly();
  readonly admin = this.adminSignal.asReadonly();

  readonly hasError = computed(() => this.errorSignal() !== null);
  readonly errorMessage = computed(() => this.errorSignal()?.message ?? null);
  readonly currentUser = computed(
    () => this.adminSignal() ?? this.userSignal()
  );
  readonly isAuthenticated = computed(() => this.currentUser() !== null);
  readonly isAdminAuthenticated = computed(() => this.adminSignal() !== null);

  /**
   * Logs in a user with the provided credentials.
   * @param payload The login credentials for the user.
   * @returns A promise that resolves to a boolean indicating whether the login was successful.
   *
   * NOT USE YET
   */
  async login(payload: LoginRequest): Promise<boolean> {
    this.startRequest();

    try {
      await firstValueFrom(this.authService.login(payload));

      const user = await firstValueFrom(this.authService.getMeUser());
      this.userSignal.set(user);
      this.adminSignal.set(null);
      this.sessionStore.setAuthenticated(user);
      return true;
    } catch (error: unknown) {
      this.errorSignal.set(error as AppHttpError);
      return false;
    } finally {
      this.loadingSignal.set(false);
    }
  }

  async loginAdmin(payload: LoginRequest): Promise<boolean> {
    this.startRequest();

    try {
      await firstValueFrom(this.authService.loginWithAdmin(payload));

      const admin = await firstValueFrom(this.authService.getMeAdmin());

      this.adminSignal.set(toAuthMe(admin));
      this.userSignal.set(null);
      this.sessionStore.setAuthenticated(admin);
      return true;
    } catch (error: unknown) {
      console.log("mi error", error);
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

      const user = await firstValueFrom(this.authService.getMeUser());

      this.userSignal.set(toAuthMe(user));
      this.adminSignal.set(null);
      this.sessionStore.setAuthenticated(toAuthMe(user));
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
  async logout(): Promise<boolean> {
    this.startRequest();

    try {
      console.log("logging out");

      await firstValueFrom(this.authService.logout());

      // const isAdmin = !!this.adminService.getSuperAdmin();
      const isAdmin = this.adminService.getSuperAdmin() !== null;

      this.reset();
      this.sessionStore.clearSession();
      this.adminService.clearSuperAdmin();
      this.accessService.clearTokens();

      console.log("logging out, finalizado");

      await this.router.navigate([
        isAdmin ? "/platform/auth/login" : "/auth/login",
      ]);

      return true;
    } catch (error: unknown) {
      this.setRequestError(error);
      return false;
    } finally {
      this.loadingSignal.set(false);
    }
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
    this.userSignal.set(null);
    this.adminSignal.set(null);
  }

  /**
   * Starts an authentication-related request by setting the loading state to true,
   */
  private startRequest(): void {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);
  }

  /**
   * Normalizes and stores a request error.
   */
  private setRequestError(error: unknown): void {
    this.errorSignal.set(error as AppHttpError);
  }
}

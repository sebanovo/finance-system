import { HttpClient, HttpContext, HttpHeaders } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { map, Observable, tap, throwError } from "rxjs";
import { SKIP_AUTH_REFRESH } from "../../../core/http/context/skip-auth-refresh.context";
import { AccessTokenService } from "../../../core/http/services/access-token.service";
import { HeaderTenantService } from "../../../core/http/services/header-tenant.service";
import { LoginRequest, LoginTenantRequest } from "../models/auth-request.type";
import {
  AuthMeResponse,
  LoginData,
  LoginResponse,
} from "../models/auth-response.type";

@Injectable({
  providedIn: "root",
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly accessTokenService = inject(AccessTokenService);
  private readonly tenantService = inject(HeaderTenantService);

  private readonly authBasePath = "/api/auth";

  /**
   *  Logs in a user with the provided credentials.
   * @param payload The login credentials.
   * @returns An observable that emits the login data upon successful authentication.
   */
  login(payload: LoginRequest): Observable<LoginData> {
    return this.http
      .post<LoginResponse<LoginData>>(`${this.authBasePath}/login`, payload)
      .pipe(
        map(response => response.data),
        tap(session => {
          this.accessTokenService.setTokens(session);
          this.tenantService.clearTenant();
        })
      );
  }

  /**
   * Logs in a user with a specific tenant.
   * @param payload The login credentials along with the tenant slug.
   * @returns An observable that emits the login data upon successful authentication.
   */
  loginWithTenant(payload: LoginTenantRequest): Observable<LoginData> {
    const headers = new HttpHeaders({
      "X-Tenant-Slug": payload.tenantSlug.trim(),
    });

    const body = {
      email: payload.email,
      password: payload.password,
    };
    return this.http
      .post<
        LoginResponse<LoginData>
      >(`${this.authBasePath}/login`, body, { headers })
      .pipe(
        map(response => response.data),
        tap(session => {
          this.accessTokenService.setTokens(session);
          this.tenantService.setTenant(payload.tenantSlug);
        })
      );
  }

  /**
   * Refreshes the user's authentication tokens.
   * @returns An observable that emits the refreshed login data.
   */
  refresh(): Observable<LoginData> {
    const tokens = this.accessTokenService.getTokens();

    if (!tokens?.refreshToken) {
      return throwError(() => new Error("Refresh token not found."));
    }

    return this.http
      .post<LoginResponse<LoginData>>(
        `${this.authBasePath}/refresh`,
        {
          refreshToken: tokens.refreshToken,
        },
        {
          context: new HttpContext().set(SKIP_AUTH_REFRESH, true),
        }
      )
      .pipe(
        map(response => response.data),
        tap(nextTokens => {
          this.accessTokenService.setTokens(nextTokens);
        })
      );
  }

  /**
   * Logs out the current user by clearing stored tokens and tenant information.
   * This effectively ends the user's session on the client side.
   */
  logout(): void {
    this.accessTokenService.clearTokens();
    this.tenantService.clearTenant();
  }

  /**
   * Retrieves the current user's information.
   * @returns An observable that emits the user's information.
   */
  getMe() {
    return this.http
      .get<AuthMeResponse>(`${this.authBasePath}/me`)
      .pipe(map(response => response.data));
  }

  getStoreTokens(): LoginData | null {
    return this.accessTokenService.getTokens();
  }

  getStoredTenant(): string | null {
    return this.tenantService.getTenant();
  }

  hasStoredSession(): boolean {
    return this.getStoreTokens() !== null;
  }

  /**
   * Checks if valid authentication tokens are present in storage.
   * @returns A boolean indicating whether valid authentication tokens are present in storage.
   */
  hasTokens(): boolean {
    const tokens = this.accessTokenService.getTokens();

    if (!tokens) {
      return false;
    }

    const hasAccessToken =
      typeof tokens.accessToken === "string" &&
      tokens.accessToken.trim().length > 0;

    const hasRefreshToken =
      typeof tokens.refreshToken === "string" &&
      tokens.refreshToken.trim().length > 0;

    return hasAccessToken && hasRefreshToken;
  }
}

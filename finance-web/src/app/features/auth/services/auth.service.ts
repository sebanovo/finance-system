import { HttpClient, HttpContext, HttpHeaders } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { map, Observable, tap } from "rxjs";
import { SKIP_AUTH_REFRESH } from "../../../core/http/context/skip-auth-refresh.context";
import { AccessTokenService } from "../../../core/http/services/access-token.service";
import { AdminAccessTokenService } from "../../../core/http/services/admin-access-token.service";
import { HeaderTenantService } from "../../../core/http/services/header-tenant.service";
import {
  AuthMeData,
  LoginRequest,
  LoginTenantRequest,
  SignupRequest,
} from "../models/auth-request.type";
import {
  AuthMeResponse,
  LoginData,
  LoginResponse,
  SignupResponse,
  SignupResponseDataDto,
} from "../models/auth-response.type";

@Injectable({
  providedIn: "root",
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly accessTokenService = inject(AccessTokenService);
  private readonly tenantService = inject(HeaderTenantService);
  private readonly adminService = inject(AdminAccessTokenService);

  private readonly authBasePath = "/api/auth";
  private readonly authAdminBasePath = "/api/platform/auth";
  private readonly authPublicPath = "/api/public";

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
          this.adminService.clearSuperAdmin();
        })
      );
  }

  loginWithAdmin(payload: LoginRequest): Observable<LoginData> {
    return this.http
      .post<
        LoginResponse<LoginData>
      >(`${this.authAdminBasePath}/login`, payload)
      .pipe(
        map(response => response.data),
        tap(session => {
          this.accessTokenService.setTokens(session);
          this.adminService.setSuperAdmin();
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
          this.tenantService.setTenant(payload.tenantSlug.trim());
          this.adminService.clearSuperAdmin();
        })
      );
  }

  signup(payload: SignupRequest): Observable<SignupResponseDataDto> {
    return this.http
      .post<
        SignupResponse<SignupResponseDataDto>
      >(`${this.authPublicPath}/signup`, payload)
      .pipe(map(response => response.data));
  }

  /**
   * Retrieves the current user's information.
   * @returns An observable that emits the user's information.
   */
  getMeUser(): Observable<AuthMeData> {
    return this.http
      .get<AuthMeResponse<AuthMeData>>(`${this.authBasePath}/me`)
      .pipe(map(response => response.data));
  }

  getMeAdmin(): Observable<AuthMeData> {
    return this.http
      .get<AuthMeResponse<AuthMeData>>(`${this.authAdminBasePath}/me`)
      .pipe(map(response => response.data));
  }

  /**
   * Gets the current profile according to the current session type.
   */
  getMe(): Observable<AuthMeData> {
    return this.isAdmin() ? this.getMeAdmin() : this.getMeUser();
  }

  /**
   * Refreshes the user's authentication tokens.
   * @returns An observable that emits the refreshed login data.
   */
  refreshUser(): Observable<LoginData> {
    const tokens = this.getRequiredRefreshToken();

    return this.http
      .post<LoginResponse<LoginData>>(
        `${this.authBasePath}/refresh`,
        {
          refreshToken: tokens,
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
   * Refreshes the admin user's authentication tokens.
   * @returns An observable that emits the refreshed login data for the admin user.
   */
  refreshAdmin(): Observable<LoginData> {
    const tokens = this.getRequiredRefreshToken();

    return this.http
      .post<LoginResponse<LoginData>>(
        `${this.authAdminBasePath}/refresh`,
        {
          refreshToken: tokens,
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
   * Unifies the refresh logic for both regular users and admin users
   * by determining the user type and invoking the appropriate refresh method.
   * @returns
   */
  refresh(): Observable<LoginData> {
    return this.isAdmin() ? this.refreshAdmin() : this.refreshUser();
  }

  logoutUser(): Observable<{ status: string }> {
    return this.http
      .post<
        LoginResponse<{ status: string }>
      >(`${this.authBasePath}/logout`, null)
      .pipe(
        tap(() => console.log("hola user")),
        map(response => response.data)
      );
  }

  logoutAdmin(): Observable<{ status: string }> {
    return this.http
      .post<
        LoginResponse<{ status: string }>
      >(`${this.authAdminBasePath}/logout`, null)
      .pipe(map(response => response.data));
  }

  logout(): Observable<{ status: string }> {
    return this.isAdmin() ? this.logoutAdmin() : this.logoutUser();
  }

  /**
   * Returns whether the current session belongs to an admin.
   */
  isAdmin(): boolean {
    return this.adminService.getSuperAdmin() ? true : false;
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

  getTenant(): string | null {
    return this.tenantService.getTenant();
  }

  private getRequiredRefreshToken(): string {
    const refreshToken = this.accessTokenService.getTokens()?.refreshToken;

    if (typeof refreshToken !== "string" || refreshToken.trim().length === 0) {
      throw new Error("Refresh token not found.");
    }

    return refreshToken;
  }

  /**
   * Logs out the current user by clearing stored tokens and tenant information.
   * This effectively ends the user's session on the client side.
   */
  // logoutUser(): void {
  //   this.accessTokenService.clearTokens();
  //   this.tenantService.clearTenant();
  // }

  // logoutAdmin(): void {
  //   this.accessTokenService.clearTokens();
  //   this.adminService.clearSuperAdmin();
  //   this.tenantService.clearTenant();
  // }
}

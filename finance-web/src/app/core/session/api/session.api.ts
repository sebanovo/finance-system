import { HttpClient, HttpContext, HttpHeaders } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { map, Observable } from "rxjs";

import { SKIP_AUTH_REFRESH } from "../../../core/http/context/skip-auth-refresh.token";
import {
  AuthMeResponse,
  AuthUser,
  LoginRequest,
  LoginResponse,
  LogoutResponse,
  RefreshResponse,
} from "../model/auth-user.type";

@Injectable({
  providedIn: "root",
})
export class SessionApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = "/api/auth";

  /**
   * Authenticates the user.
   */
  // login(payload: LoginRequest): Observable<LoginResponse> {
  //   return this.http.post<LoginResponse>(`${this.baseUrl}/login`, payload, {
  //     context: new HttpContext().set(SKIP_AUTH_REFRESH, true),
  //   });
  // }

  /**
   * Authenticates the user.
   * Sends the tenant slug through the X-Tenant-Slug header.
   */
  login(payload: LoginRequest): Observable<LoginResponse> {
    const headers = new HttpHeaders({
      "X-Tenant-Slug": payload.tenantSlug,
    });

    const body = {
      email: payload.email,
      password: payload.password,
    };

    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, body, {
      headers,
      context: new HttpContext().set(SKIP_AUTH_REFRESH, true),
    });
  }

  /**
   * Returns the current authenticated user.
   */
  getMe(): Observable<AuthUser> {
    return this.http
      .get<AuthMeResponse>(`${this.baseUrl}/me`)
      .pipe(map(response => response.data));
  }

  /**
   * Logs out the current user.
   */
  logout(): Observable<void> {
    return this.http
      .post<LogoutResponse>(
        `${this.baseUrl}/logout`,
        {},
        {
          context: new HttpContext().set(SKIP_AUTH_REFRESH, true),
        }
      )
      .pipe(map(() => void 0));
  }

  /**
   * Requests a new access token.
   */
  refresh(): Observable<RefreshResponse> {
    console.log("refreshing!!!");
    return this.http.post<RefreshResponse>(
      `${this.baseUrl}/refresh`,
      {},
      {
        context: new HttpContext().set(SKIP_AUTH_REFRESH, true),
      }
    );
  }
}

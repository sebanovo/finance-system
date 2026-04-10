import { HttpClient, HttpContext } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { API_CONFIG, buildApiUrl } from "../../../core/config/api.config";
import {
  SKIP_AUTH_ERROR,
  SKIP_REFRESH,
} from "../../../core/http/context/http-context.tokens";
import type {
  AuthMessageResponse,
  LoginRequest,
  LoginResponse,
  MeResponse,
  RefreshResponse,
} from "./auth.types";

/**
 * Shared request options for auth endpoints that use cookies.
 */
type AuthRequestOptions = {
  withCredentials: true;
  context?: HttpContext;
};

/**
 * API gateway for authentication and session endpoints.
 */
@Injectable({
  providedIn: "root",
})
export class AuthApi {
  private readonly http = inject(HttpClient);

  /**
   * Sends credentials to the backend.
   *
   * The backend is expected to manage the session through httpOnly cookies.
   */
  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      buildApiUrl(API_CONFIG.auth.login),
      payload,
      {
        withCredentials: true,
        context: new HttpContext()
          .set(SKIP_REFRESH, true)
          .set(SKIP_AUTH_ERROR, true),
      }
    );
  }

  /**
   * Clears the current backend session.
   */
  logout(): Observable<AuthMessageResponse> {
    return this.http.post<AuthMessageResponse>(
      buildApiUrl(API_CONFIG.auth.logout),
      {},
      {
        withCredentials: true,
        context: new HttpContext().set(SKIP_REFRESH, true),
      }
    );
  }

  /**
   * Requests a refreshed session from the backend.
   *
   * This request must never trigger refresh logic again.
   */
  refresh(): Observable<RefreshResponse> {
    return this.http.post<RefreshResponse>(
      buildApiUrl(API_CONFIG.auth.refresh),
      {},
      {
        withCredentials: true,
        context: new HttpContext()
          .set(SKIP_REFRESH, true)
          .set(SKIP_AUTH_ERROR, true),
      }
    );
  }

  /**
   * Retrieves the currently authenticated user.
   */
  getMe(): Observable<MeResponse> {
    return this.http.get<MeResponse>(buildApiUrl(API_CONFIG.auth.me), {
      withCredentials: true,
    });
  }
}

import { HttpClient, HttpHeaders } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";

import {
  ChangePasswordRequest,
  ForgotPasswordRequest,
  PasswordActionData,
  PasswordResponse,
  ResetPasswordRequest,
} from "../model/password.type";

@Injectable({
  providedIn: "root",
})
export class PasswordApi {
  private readonly http = inject(HttpClient);

  forgotPassword(
    payload: ForgotPasswordRequest
  ): Observable<PasswordActionData> {
    const headers = new HttpHeaders({
      "X-Tenant-Slug": "thunder",
    });
    return this.http
      .post<
        PasswordResponse<PasswordActionData>
      >("/api/auth/forgot-password", payload, { headers })
      .pipe(map(response => response.data));
  }

  resetPassword(payload: ResetPasswordRequest): Observable<PasswordActionData> {
    const headers = new HttpHeaders({
      "X-Tenant-Slug": "thunder",
    });
    return this.http
      .post<
        PasswordResponse<PasswordActionData>
      >("/api/auth/reset-password", payload, { headers })
      .pipe(map(response => response.data));
  }

  changePassword(
    payload: ChangePasswordRequest
  ): Observable<PasswordActionData> {
    return this.http
      .post<
        PasswordResponse<PasswordActionData>
      >("/api/auth/change-password", payload)
      .pipe(map(response => response.data));
  }
}

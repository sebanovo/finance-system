import { HttpClient, HttpHeaders } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { map, Observable } from "rxjs";

import { ApiResponse } from "../models/api.response.model";
import { CreateUserRequest } from "../models/create-user-request.model";
import { User } from "../models/user.model";

@Injectable({
  providedIn: "root",
})
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = "/api/users";

  getUsers(): Observable<User[]> {
    return this.http
      .get<ApiResponse<User[]>>(this.baseUrl)
      .pipe(map(response => response.data));
  }

  createUser(payload: CreateUserRequest): Observable<User> {
    // Preparar el tanant en el header
    const headers = new HttpHeaders({ "X-Tenant-Slug": payload.tenantSlug });

    const body = {
      firstName: payload.firstName,
      lastName: payload.lastName,
      email: payload.email,
      roleId: payload.roleId,
    };

    return this.http
      .post<ApiResponse<User>>(this.baseUrl, body, { headers })
      .pipe(map(response => response.data));
  }
}

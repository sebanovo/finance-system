import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { CreateUserRequest } from "../models/user-request.type";
import { UserDto } from "../models/user.dto";
import { UpdateUserRequest } from "../models/user.model";
import { UserResponse } from "../models/user.response";

@Injectable({
  providedIn: "root",
})
export class UsersApi {
  private readonly http = inject(HttpClient);

  getUsers(): Observable<UserDto[]> {
    return this.http
      .get<UserResponse<UserDto[]>>("/api/users")
      .pipe(map(response => response.data));
  }
  createUser(payload: CreateUserRequest): Observable<UserDto> {
    return this.http
      .post<UserResponse<UserDto>>("/api/users", payload)
      .pipe(map(response => response.data));
  }

  getUserById(userId: string): Observable<UserDto> {
    return this.http
      .get<UserResponse<UserDto>>(`/api/users/${userId}`)
      .pipe(map(response => response.data));
  }

  updateUser(userId: string, payload: UpdateUserRequest): Observable<UserDto> {
    return this.http
      .put<UserResponse<UserDto>>(`/api/users/${userId}`, payload)
      .pipe(map(response => response.data));
  }

  activateUser(userId: string): Observable<UserDto> {
    return this.http
      .patch<UserResponse<UserDto>>(`/api/users/${userId}/activate`, {})
      .pipe(map(response => response.data));
  }

  deactivateUser(userId: string): Observable<UserDto> {
    return this.http
      .patch<UserResponse<UserDto>>(`/api/users/${userId}/deactivate`, {})
      .pipe(map(response => response.data));
  }
}

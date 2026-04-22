import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../../../core/models/api-response.model';

/**Matches: identity.users.application.dto.TenantUserResponse*/
export type User = {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  active: boolean;
  status: string;
  createdAt: string;
  updatedAt: string;
};

/**Matches: identity.users.application.dto.CreateTenantUserRequest*/
export type CreateUserRequest = {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
};

@Injectable({
  providedIn: 'root',
})
export class UsersService {
  private http = inject(HttpClient);
  private apiUrl = '/api/users';

  /**Obtiene la lista de usuarios del tenant actual
   * Consume: GET /api/users*/
  getUsers(): Observable<User[]> {
    return this.http
      .get<ApiResponse<User[]>>(this.apiUrl)
      .pipe(map((response) => response.data));
  }

  /**Crea un nuevo usuario
   * Consume: POST /api/users*/
  createUser(request: CreateUserRequest): Observable<User> {
    return this.http
      .post<ApiResponse<User>>(this.apiUrl, request)
      .pipe(map((response) => response.data));
  }

  /**Activa un usuario
   * Consume: PATCH /api/users/{id}/activate*/
  activateUser(userId: string): Observable<User> {
    return this.http
      .patch<ApiResponse<User>>(`${this.apiUrl}/${userId}/activate`, {})
      .pipe(map((response) => response.data));
  }

  /**Desactiva un usuario
   * Consume: PATCH /api/users/{id}/deactivate*/
  deactivateUser(userId: string): Observable<User> {
    return this.http
      .patch<ApiResponse<User>>(`${this.apiUrl}/${userId}/deactivate`, {})
      .pipe(map((response) => response.data));
  }
}
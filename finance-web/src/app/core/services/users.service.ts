import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_CONFIG, buildApiUrl } from '../config/api.config';
import {
  User,
  CreateUserRequest,
  UpdateUserRequest,
  UserListResponse,
  UserStatusResponse,
} from '../models/user.model';
import { ApiResponse } from '../models/api-response.model';

/**
 * Servicio para gestionar usuarios dentro del tenant.
 * Proporciona métodos para CRUD de usuarios y cambio de estado.
 * 
 * Todos estos endpoints requieren:
 * 1. Autenticación (token en Authorization header)
 * 2. Header X-Tenant-Slug (inyectado automáticamente por el interceptor)
 * 
 * Usa inject() para inyectar HttpClient (Angular 18+ standalone pattern)
 */
@Injectable({
    providedIn: 'root',
})
export class UsersService {
    private httpClient = inject(HttpClient);

    /**
     * Obtiene el listado de usuarios del tenant actual
     * GET /api/users
     * 
     * @param page Número de página (default: 0)
     * @param size Tamaño de la página (default: 20)
     * @returns Observable<UserListResponse> con lista paginada de usuarios
     */
    getUsers(page: number = 0, size: number = 20): Observable<ApiResponse<UserListResponse>> {
        const url = buildApiUrl(API_CONFIG.users.list);
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString());

        return this.httpClient.get<ApiResponse<UserListResponse>>(url, { params });
    }

    /**
     * Obtiene un usuario específico por ID
     * GET /api/users/{id}
     * 
     * @param userId ID del usuario a obtener
     * @returns Observable<User> con datos del usuario
     */
    getUserById(userId: string): Observable<ApiResponse<User>> {
        const url = buildApiUrl(API_CONFIG.users.detail(userId));
        return this.httpClient.get<ApiResponse<User>>(url);
    }

    /**
     * Crea un nuevo usuario en el tenant actual
     * POST /api/users
     * 
     * Solo usuarios con rol ADMIN o OWNER_ADMIN pueden crear usuarios.
     * 
     * @param userData Datos del nuevo usuario
     * @returns Observable<User> con el usuario creado
     */
    createUser(userData: CreateUserRequest): Observable<ApiResponse<User>> {
        const url = buildApiUrl(API_CONFIG.users.create);
        return this.httpClient.post<ApiResponse<User>>(url, userData);
    }

    /**
     * Actualiza los datos de un usuario
     * PUT /api/users/{id}
     * 
     * Se pueden actualizar: email, firstName, lastName, role
     * 
     * @param userId ID del usuario a actualizar
     * @param updateData Datos a actualizar
     * @returns Observable<User> con el usuario actualizado
     */
    updateUser(userId: string, updateData: UpdateUserRequest): Observable<ApiResponse<User>> {
        const url = buildApiUrl(API_CONFIG.users.update(userId));
        return this.httpClient.put<ApiResponse<User>>(url, updateData);
    }

    /**
     * Activa un usuario (empleado)
     * PATCH /api/users/{id}/activate
     * 
     * Esto permite que el usuario pueda volver a acceder al sistema
     * 
     * @param userId ID del usuario a activar
     * @returns Observable<UserStatusResponse> confirmación del cambio
     */
    activateUser(userId: string): Observable<ApiResponse<UserStatusResponse>> {
        const url = buildApiUrl(API_CONFIG.users.activate(userId));
        return this.httpClient.patch<ApiResponse<UserStatusResponse>>(url, {});
    }

    /**
     * Desactiva un usuario (empleado)
     * PATCH /api/users/{id}/deactivate
     * 
     * Esto suspende el acceso del usuario al sistema
     * 
     * @param userId ID del usuario a desactivar
     * @returns Observable<UserStatusResponse> confirmación del cambio
     */
    deactivateUser(userId: string): Observable<ApiResponse<UserStatusResponse>> {
        const url = buildApiUrl(API_CONFIG.users.deactivate(userId));
        return this.httpClient.patch<ApiResponse<UserStatusResponse>>(url, {});
    }
}

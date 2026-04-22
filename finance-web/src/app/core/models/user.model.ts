/**
 * Modelo para representar un usuario del sistema
 */
export interface User {
    id: string;
    email: string;
    firstName: string;
    lastName: string;
    role: 'ADMIN' | 'OWNER_ADMIN' | 'USER' | 'VIEWER';
    isActive: boolean;
    createdAt: string;
    updatedAt: string;
    lastLogin?: string | null;
    tenantId?: string;
}

/**
 * DTO para crear un nuevo usuario
 */
export interface CreateUserRequest {
    email: string;
    firstName: string;
    lastName: string;
    role: 'ADMIN' | 'OWNER_ADMIN' | 'USER' | 'VIEWER';
    password: string;
}

/**
 * DTO para actualizar un usuario
 */
export interface UpdateUserRequest {
    email?: string;
    firstName?: string;
    lastName?: string;
    role?: 'ADMIN' | 'OWNER_ADMIN' | 'USER' | 'VIEWER';
}

/**
 * Respuesta al obtener usuarios (con paginación)
 */
export interface UserListResponse {
    content: User[];
    totalElements: number;
    totalPages: number;
    currentPage: number;
    pageSize: number;
    hasNext: boolean;
    hasPrevious: boolean;
}

/**
 * Modelo para la respuesta de activar/desactivar usuario
 */
export interface UserStatusResponse {
    id: string;
    email: string;
    isActive: boolean;
    updatedAt: string;
}

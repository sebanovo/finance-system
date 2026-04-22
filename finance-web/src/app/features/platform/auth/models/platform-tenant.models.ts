// DTO de Envío (Request)
export interface PlatformLoginRequest {
  email: string;
  password: string;
}

// DTO de Recepción (Response de la API en la propiedad 'data')
export interface AuthTokenResponse {
  tokenType: string;
  accessToken: string;
  refreshToken: string;
  accessExpiresInMs: number;
}

// DTO para el perfil del Superadmin (Respuesta de /me)
export interface PlatformUserResponse {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
}
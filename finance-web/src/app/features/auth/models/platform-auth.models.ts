/**Models for Platform Authentication feature*/

export interface PlatformLoginRequest {
  email: string;
  password: string;
}

export interface PlatformAuthTokenResponse {
  tokenType: string;
  accessToken: string;
  refreshToken: string;
  accessExpiresInMs: number;
}

export interface PlatformUserInfo {
  id: string;
  email: string;
  name: string;
  permissions: string[];
}

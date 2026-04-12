export type AuthUser = {
  id: string;
  email: string;
  firstName: string;
  lastName: string;

  active: boolean;
  status: string;

  tenantSlug: string;

  roles: string[];
};

/**
 * Login type
 */
export type LoginRequest = {
  email: string;
  password: string;
  tenantSlug: string;
};

export type LoginData = {
  tokenType: string;
  accessToken: string;
  refreshToken: string;
  accessExpiresInMs: number;
};

export type LoginResponse = {
  success: true;
  message: string;
  data: LoginData;
  timestamp: string;
};

/**
 * AuthMe type
 */
export type AuthMeResponse = {
  success: true;
  message: string;
  data: AuthUser;
  timestamp: string;
};

/**
 * Logout type
 */
export type LogoutResponse = {
  success: true;
  message: string;
  data: null;
  timestamp: string;
};

/**
 * Refresh type
 */
export type RefreshData = {
  tokenType: string;
  accessToken: string;
  refreshToken: string;
  accessExpiresInMs: number;
};

export type RefreshResponse = {
  success: true;
  message: string;
  data: RefreshData;
  timestamp: string;
};

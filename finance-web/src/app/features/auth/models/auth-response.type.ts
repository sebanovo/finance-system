export type LoginData = {
  tokenType: string;
  accessToken: string;
  refreshToken: string;
  accessExpiresInMs: number;
};

export type LoginResponse<T> = {
  success: true;
  message: string;
  data: T;
  timestamp: string;
};

/**
 * AuthMe type
 */
export type AuthMeResponse<T> = {
  success: true;
  message: string;
  data: T;
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

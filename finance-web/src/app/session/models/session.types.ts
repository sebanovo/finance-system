/**
 * Session status lifecycle.
 *
 * unknown: app has not checked the current session yet
 * checking: app is validating the current session
 * authenticated: valid user session exists
 * unauthenticated: no valid user session exists
 */
export type SessionStatus =
  | "unknown"
  | "checking"
  | "authenticated"
  | "unauthenticated";

/**
 * Basic user settings loaded during session bootstrap.
 */
export type SessionUserSettings = {
  theme?: "light" | "dark" | "system";
  language?: string;
  [key: string]: unknown;
};

/**
 * Authenticated user shape stored in the frontend session state.
 */
export type SessionUser = {
  id: string;
  email: string;
  fullName: string;
  roles: string[];
  permissions: string[];
  settings?: SessionUserSettings;
};

/**
 * Global session state used by the application.
 */
export type SessionState = {
  status: SessionStatus;
  user: SessionUser | null;
  initialized: boolean;
};

/**
 * Centralized API configuration.
 *
 * Keeps all auth-related endpoints in one place so the rest of the app
 * does not depend on hardcoded strings.
 */
export const API_CONFIG = {
  baseUrl: "/api",
  auth: {
    login: "/auth/login",
    logout: "/auth/logout",
    refresh: "/auth/refresh",
    me: "/auth/me",
  },
  users: {
    list: "/users",
    create: "/users",
    detail: (id: string) => `/users/${id}`,
    update: (id: string) => `/users/${id}`,
    activate: (id: string) => `/users/${id}/activate`,
    deactivate: (id: string) => `/users/${id}/deactivate`,
  },
} as const;

/**
 * Builds a full API URL from the configured base URL.
 */
export function buildApiUrl(path: string): string {
  return `${API_CONFIG.baseUrl}${path}`;
}

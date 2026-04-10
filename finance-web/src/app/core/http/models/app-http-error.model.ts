/**
 * Stable application-level error codes.
 *
 * The UI should rely on these codes instead of backend-specific formats.
 */
export type AppHttpErrorCode =
  | "BAD_REQUEST"
  | "UNAUTHORIZED"
  | "FORBIDDEN"
  | "NOT_FOUND"
  | "CONFLICT"
  | "VALIDATION"
  | "RATE_LIMITED"
  | "SERVER_ERROR"
  | "NETWORK_ERROR"
  | "TIMEOUT"
  | "UNKNOWN_ERROR";

/**
 * Normalized HTTP error shape used across the application.
 */
export type AppHttpError = {
  status: number;
  code: AppHttpErrorCode;
  message: string;
  details?: unknown;
  originalError?: unknown;
};

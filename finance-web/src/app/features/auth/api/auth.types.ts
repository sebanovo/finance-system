import type { SessionUser } from "../../../session/models/session.types";

/**
 * Payload sent to login endpoint.
 */
export type LoginRequest = {
  email: string;
  password: string;
};

/**
 * Generic auth message response.
 */
export type AuthMessageResponse = {
  message: string;
};

/**
 * Login response.
 *
 * Since authentication is cookie-based, this can be minimal.
 */
export type LoginResponse = {
  message: string;
};

/**
 * Current authenticated user response.
 */
export type MeResponse = {
  user: SessionUser;
};

/**
 * Refresh response.
 */
export type RefreshResponse = {
  message: string;
};

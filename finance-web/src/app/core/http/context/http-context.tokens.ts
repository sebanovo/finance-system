import { HttpContextToken } from "@angular/common/http";

/**
 * Skips auth refresh logic for a request.
 */
export const SKIP_REFRESH = new HttpContextToken<boolean>(() => false);

/**
 * Skips global auth error handling for a request.
 */
export const SKIP_AUTH_ERROR = new HttpContextToken<boolean>(() => false);

/**
 * Marks a request as silent.
 */
export const SILENT_ERROR = new HttpContextToken<boolean>(() => false);

/**
 * Marks that the request has already been retried after a refresh.
 */
export const HAS_RETRIED = new HttpContextToken<boolean>(() => false);

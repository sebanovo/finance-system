import { HttpContextToken } from "@angular/common/http";

/**
 * Marks requests that must bypass the auth refresh flow.
 */
export const SKIP_AUTH_REFRESH = new HttpContextToken<boolean>(() => false);

/**
 * Marks whether the current request has already been retried
 * after a successful refresh.
 */
export const HAS_RETRIED = new HttpContextToken<boolean>(() => false);

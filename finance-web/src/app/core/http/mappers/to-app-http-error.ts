import { HttpErrorResponse } from "@angular/common/http";

import { ApiErrorResponse } from "../models/api-error-response.model";
import {
  AppFieldError,
  AppHttpError,
  AppHttpErrorCode,
} from "../models/app-http-error.model";

/**
 * Gets the application-specific HTTP error code based on the HTTP status.
 * @param status The HTTP status.
 * @returns The application-specific HTTP error code.
 */
function getErrorCode(status: number): AppHttpErrorCode {
  switch (status) {
    case 400:
      return "BAD_REQUEST";
    case 401:
      return "UNAUTHORIZED";
    case 403:
      return "FORBIDDEN";
    case 404:
      return "NOT_FOUND";
    case 409:
      return "CONFLICT";
    case 422:
      return "UNPROCESSABLE_ENTITY";
    default:
      return status >= 500 ? "SERVER" : "UNKNOWN";
  }
}

/**
 * Converts API field errors to application-specific field errors.
 * @param payload The API error response payload.
 * @returns The converted application-specific field errors.
 */
function toFieldErrors(payload: ApiErrorResponse | null): AppFieldError[] {
  if (!payload?.errors?.length) {
    return [];
  }

  return payload.errors.map(item => ({
    field: item.field ?? "",
    message: item.message,
  }));
}

/**
 * Determines whether the given value is an API error response.
 * @param value The value to check.
 * @returns True if the value is an API error response, false otherwise.
 */
function isApiErrorResponse(value: unknown): value is ApiErrorResponse {
  if (!value || typeof value !== "object") {
    return false;
  }

  const candidate = value as Record<string, unknown>;

  return (
    candidate["success"] === false &&
    typeof candidate["message"] === "string" &&
    Array.isArray(candidate["errors"]) &&
    typeof candidate["timestamp"] === "string"
  );
}

/**
 * Converts an HTTP error to an application-specific HTTP error.
 * @param error The HTTP error to convert.
 * @returns The converted application-specific HTTP error.
 */
export function toAppHttpError(error: unknown): AppHttpError {
  if (!(error instanceof HttpErrorResponse)) {
    return {
      code: "UNKNOWN",
      status: null,
      message: "Unexpected error",
      fieldErrors: [],
      timestamp: null,
      raw: error,
    };
  }

  if (error.status === 0) {
    return {
      code: "NETWORK",
      status: 0,
      message: "Could not connect to the server",
      fieldErrors: [],
      timestamp: null,
      raw: error,
    };
  }

  const payload = isApiErrorResponse(error.error) ? error.error : null;

  if (payload) {
    return {
      code: getErrorCode(error.status),
      status: error.status,
      message: payload.message,
      fieldErrors: toFieldErrors(payload),
      timestamp: payload.timestamp,
      raw: error,
    };
  }

  return {
    code: getErrorCode(error.status),
    status: error.status || null,
    message: error.message || "Request failed",
    fieldErrors: [],
    timestamp: null,
    raw: error,
  };
}

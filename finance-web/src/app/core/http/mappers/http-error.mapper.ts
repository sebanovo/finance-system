import { HttpErrorResponse } from "@angular/common/http";
import type {
  AppHttpError,
  AppHttpErrorCode,
} from "../models/app-http-error.model";

/**
 * Extracts a human-readable error message from any backend payload.
 */
function extractMessage(payload: unknown): string | undefined {
  if (!payload || typeof payload !== "object") {
    return undefined;
  }

  const record = payload as Record<string, unknown>;

  if (typeof record["message"] === "string" && record["message"].trim()) {
    return record["message"];
  }

  if (typeof record["error"] === "string" && record["error"].trim()) {
    return record["error"];
  }

  if (
    record["details"] &&
    typeof record["details"] === "object" &&
    typeof (record["details"] as Record<string, unknown>)["message"] ===
      "string"
  ) {
    return (record["details"] as Record<string, string>)["message"];
  }

  return undefined;
}

/**
 * Extracts backend details if present.
 */
function extractDetails(payload: unknown): unknown {
  if (!payload || typeof payload !== "object") {
    return undefined;
  }

  const record = payload as Record<string, unknown>;

  if ("details" in record) {
    return record["details"];
  }

  if ("errors" in record) {
    return record["errors"];
  }

  return undefined;
}

/**
 * Maps an HTTP status code to an application-level error code.
 */
function mapStatusToCode(status: number): AppHttpErrorCode {
  switch (status) {
    case 0:
      return "NETWORK_ERROR";
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
      return "VALIDATION";
    case 429:
      return "RATE_LIMITED";
    default:
      if (status >= 500) {
        return "SERVER_ERROR";
      }

      return "UNKNOWN_ERROR";
  }
}

/**
 * Checks whether the received value looks like a timeout error.
 */
function isTimeoutError(error: unknown): boolean {
  return (
    error instanceof Error &&
    (error.name === "TimeoutError" ||
      error.message.toLowerCase().includes("timeout"))
  );
}

/**
 * Converts any request error into a stable application error.
 */
export function toAppHttpError(error: unknown): AppHttpError {
  if (isTimeoutError(error)) {
    return {
      status: 0,
      code: "TIMEOUT",
      message: "The request took too long to complete.",
      originalError: error,
    };
  }

  if (error instanceof HttpErrorResponse) {
    const backendPayload = error.error;
    const backendMessage = extractMessage(backendPayload);
    const backendDetails = extractDetails(backendPayload);

    return {
      status: error.status,
      code: mapStatusToCode(error.status),
      message:
        backendMessage ??
        error.message ??
        "An unexpected error occurred while processing the request.",
      details: backendDetails,
      originalError: error,
    };
  }

  if (error instanceof Error) {
    return {
      status: 0,
      code: "UNKNOWN_ERROR",
      message: error.message || "An unexpected error occurred.",
      originalError: error,
    };
  }

  return {
    status: 0,
    code: "UNKNOWN_ERROR",
    message: "An unexpected error occurred.",
    originalError: error,
  };
}

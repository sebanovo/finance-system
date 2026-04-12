export type AppHttpErrorCode =
  | "BAD_REQUEST"
  | "UNAUTHORIZED"
  | "FORBIDDEN"
  | "NOT_FOUND"
  | "CONFLICT"
  | "UNPROCESSABLE_ENTITY"
  | "NETWORK"
  | "TIMEOUT"
  | "SERVER"
  | "UNKNOWN";

export type AppFieldError = {
  field: string;
  message: string;
};

export type AppHttpError = {
  code: AppHttpErrorCode;
  status: number | null;
  message: string;
  fieldErrors: AppFieldError[];
  timestamp: string | null;
  raw: unknown;
};

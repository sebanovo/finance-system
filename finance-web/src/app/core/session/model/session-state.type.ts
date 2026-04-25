import type { AuthMeData } from "../../../features/auth/models/auth-request.type";

export type SessionStatus =
  | "idle"
  | "bootstrapping"
  | "authenticated"
  | "unauthenticated";

export type SessionState = {
  status: SessionStatus;
  initialized: boolean;
  user: AuthMeData | null;
  errorMessage: string | null;
};

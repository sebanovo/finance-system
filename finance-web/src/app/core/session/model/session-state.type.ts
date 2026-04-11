import { AuthUser } from "./auth-user.type";

export type SessionStatus =
  | "idle"
  | "loading"
  | "authenticated"
  | "unauthenticated";

export type SessionState = {
  user: AuthUser | null;
  status: SessionStatus;
  isBootstrapping: boolean;
  isInitialized: boolean;
  bootstrapError: string | null;
};

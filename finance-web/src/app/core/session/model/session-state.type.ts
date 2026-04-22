import { SessionUser } from "./session-user.type";

export type SessionStatus =
  | "idle"
  | "bootstrapping"
  | "authenticated"
  | "unauthenticated";

export type SessionState = {
  status: SessionStatus;
  initialized: boolean;
  user: SessionUser | null;
  errorMessage: string | null;
};

import { SessionUser } from "./session-user.type";

export type SessionBootstrapResult =
  | {
      status: "authenticated";
      user: SessionUser;
    }
  | {
      status: "unauthenticated";
      errorMessage: string | null;
    };

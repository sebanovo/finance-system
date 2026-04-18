import type { AuthMeData } from "../../../features/auth/models/auth-request.type";

export type SessionBootstrapResult =
  | {
      status: "authenticated";
      user: AuthMeData;
    }
  | {
      status: "unauthenticated";
      errorMessage: string | null;
    };

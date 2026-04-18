import { inject } from "@angular/core";
import { CanMatchFn } from "@angular/router";
import { AuthService } from "../../../features/auth/services/auth.service";

export const userSubscriptionsMatchGuard: CanMatchFn = () => {
  const authService = inject(AuthService);

  return !authService.isAdmin();
};

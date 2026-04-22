import { ChangeDetectionStrategy, Component, inject, signal, WritableSignal } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";

import { SessionStore } from "../../../../core/session/store/session.store";
import { LoginForm } from "../../components/login-form/login-form";
import {
  LoginRequest,
  LoginTenantRequest,
} from "../../models/auth-request.type";
import { AuthStore } from "../../store/auth.store";

@Component({
  selector: "app-login-page",
  imports: [LoginForm],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: "./login-page.html",
})
export class LoginPage {
  private readonly sessionStore = inject(SessionStore);
  private readonly authStore = inject(AuthStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly isSubmitting = this.authStore.loading;
  readonly errorMessage = this.authStore.errorMessage;

  async onSubmit(payload: LoginRequest): Promise<void> {
    console.log("login payload", payload);
    const success = await this.authStore.login(payload);
    if (!success) {
      return;
    }
    await this.navigateToReturnUrl();
  }

  async onSubmitWithTenant(payload: LoginTenantRequest): Promise<void> {
    const success = await this.authStore.loginWithTenant(payload);
    if (!success) {
      return;
    }
    console.log("Login successful,", this.sessionStore.isAuthenticated());
    await this.navigateToReturnUrl();
  }
  onFormEdited(): void {
    this.authStore.clearError();
  }

  private async navigateToReturnUrl(): Promise<void> {
    let returnUrl = this.route.snapshot.queryParamMap.get("returnUrl");
    
    // If no returnUrl or it is generic, fallback to /app/dashboard
    if (!returnUrl || returnUrl === "/" || returnUrl === "/app") {
      returnUrl = "/app/dashboard";
    }

    await this.router.navigateByUrl(returnUrl);
  }
}

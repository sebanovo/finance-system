import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";

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
  private readonly authStore = inject(AuthStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly isSubmitting = this.authStore.loading;
  readonly errorMessage = this.authStore.errorMessage;

  async onSubmit(payload: LoginRequest): Promise<void> {
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

    await this.navigateToReturnUrl();
  }

  onFormEdited(): void {
    this.authStore.clearError();
  }

  private async navigateToReturnUrl(): Promise<void> {
    const returnUrl =
      this.route.snapshot.queryParamMap.get("returnUrl") || "/app";

    await this.router.navigateByUrl(returnUrl);
  }
}

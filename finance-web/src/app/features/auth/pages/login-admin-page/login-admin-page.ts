import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { LoginAdminForm } from "../../components/login-admin-form/login-admin-form";
import { LoginRequest } from "../../models/auth-request.type";
import { AuthStore } from "../../store/auth.store";

@Component({
  selector: "app-login-admin-page",
  imports: [LoginAdminForm],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: "./login-admin-page.html",
  styleUrl: "./login-admin-page.css",
})
export class LoginAdminPage {
  private readonly authStore = inject(AuthStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly isSubmitting = this.authStore.loading;
  readonly errorMessage = this.authStore.errorMessage;

  async onSubmitWithAdmin(payload: LoginRequest): Promise<void> {
    const success = await this.authStore.loginAdmin(payload);

    if (!success) {
      return;
    }

    this.router.navigate(["/app"]);
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

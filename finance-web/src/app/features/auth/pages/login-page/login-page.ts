import {
  ChangeDetectionStrategy,
  Component,
  inject,
  signal,
} from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";

import { finalize } from "rxjs";
import { AppHttpError } from "../../../../core/http/models/app-http-error.model";
import { LoginRequest } from "../../../../core/session/model/auth-user.type";
import { SessionService } from "../../../../core/session/services/session.service";
import { LoginForm } from "../../components/login-form/login-form";

@Component({
  selector: "app-login-page",
  imports: [LoginForm],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: "./login-page.html",
})
export class LoginPage {
  private readonly sessionService = inject(SessionService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);

  onSubmit(payload: LoginRequest): void {
    console.log("login payload", payload);
    localStorage.setItem("tenant", payload.tenantSlug);
    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    this.sessionService
      .login(payload)
      .pipe(finalize(() => this.isSubmitting.set(false)))
      .subscribe({
        next: () => {
          console.log("login success, navigating...");
          const returnUrl =
            this.route.snapshot.queryParamMap.get("returnUrl") || "/app";
          // console.log("redirect to: ", returnUrl);
          void this.router.navigateByUrl(returnUrl);
        },
        error: (error: AppHttpError) => {
          console.log("login form error", error);
          this.errorMessage.set(error.message);
          this.isSubmitting.set(false);
        },
      });
  }

  onFormEdited(): void {
    if (this.errorMessage()) {
      this.errorMessage.set(null);
    }
  }
}

import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  inject,
  input,
  output,
} from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { RouterLink } from "@angular/router";

import { NgIcon, provideIcons } from "@ng-icons/core";
import { remixGithubFill } from "@ng-icons/remixicon";

import { HlmButtonImports } from "@shared/ui/button";
import { HlmFieldImports } from "@shared/ui/field";
import { HlmInputImports } from "@shared/ui/input";
import {
  LoginRequest,
  LoginTenantRequest,
} from "../../models/auth-request.type";

@Component({
  selector: "app-login-form",
  imports: [
    ReactiveFormsModule,
    RouterLink,
    HlmFieldImports,
    HlmInputImports,
    HlmButtonImports,
    NgIcon,
  ],
  providers: [provideIcons({ remixGithubFill })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: "./login-form.html",
})
export class LoginForm {
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);

  readonly isSubmitting = input(false);
  readonly errorMessage = input<string | null>(null);

  readonly submitLogin = output<LoginRequest>();
  readonly submitLoginWithTenant = output<LoginTenantRequest>();
  readonly formEdited = output<void>();

  readonly form = this.fb.nonNullable.group({
    email: ["", [Validators.required, Validators.email]],
    password: ["", [Validators.required, Validators.minLength(8)]],
    tenantSlug: ["", [Validators.minLength(2)]],
  });

  constructor() {
    this.form.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        if (this.errorMessage()) {
          this.formEdited.emit();
        }
      });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { email, password, tenantSlug } = this.form.getRawValue();
    const normalizedEmail = email.trim();
    const normalizedTenantSlug = tenantSlug.trim();

    if (normalizedTenantSlug) {
      this.submitLoginWithTenant.emit({
        email: normalizedEmail,
        password,
        tenantSlug: normalizedTenantSlug,
      });
      return;
    }

    this.submitLogin.emit({
      email: normalizedEmail,
      password,
    });
  }

  // private readonly fb = inject(FormBuilder);
  // private readonly destroyRef = inject(DestroyRef);

  // readonly isSubmitting = input(false);
  // readonly errorMessage = input<string | null>(null);

  // readonly submitLogin = output<LoginRequest>();
  // readonly submitLoginWithTenant = output<LoginTenantRequest>();
  // readonly formEdited = output<void>();

  // readonly form = this.fb.nonNullable.group({
  //   email: ["", [Validators.required, Validators.email]],
  //   password: ["", [Validators.required, Validators.minLength(8)]],
  //   tenantSlug: ["", [Validators.minLength(2)]],
  // });

  // constructor() {
  //   this.form.valueChanges
  //     .pipe(takeUntilDestroyed(this.destroyRef))
  //     .subscribe(() => {
  //       this.formEdited.emit();
  //     });
  // }

  // submit(): void {
  //   if (this.form.invalid) {
  //     this.form.markAllAsTouched();
  //     return;
  //   }

  //   const rawValue = this.form.getRawValue();

  //   const email = rawValue.email.trim();
  //   const password = rawValue.password;
  //   const tenantSlug = rawValue.tenantSlug.trim();

  //   if (tenantSlug) {
  //     this.submitLoginWithTenant.emit({
  //       email,
  //       password,
  //       tenantSlug: tenantSlug,
  //     });

  //     return;
  //   }

  //   this.submitLogin.emit({
  //     email,
  //     password,
  //   });
  // }
}

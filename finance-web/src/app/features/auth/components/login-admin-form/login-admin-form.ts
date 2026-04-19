import { Component, DestroyRef, inject, input, output } from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { remixGithubFill } from "@ng-icons/remixicon";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmFieldImports } from "@shared/ui/field";
import { HlmInputImports } from "@shared/ui/input";
import { LoginRequest } from "../../models/auth-request.type";

@Component({
  selector: "app-login-admin-form",
  imports: [
    ReactiveFormsModule,
    RouterLink,
    HlmFieldImports,
    HlmInputImports,
    HlmButtonImports,
    NgIcon,
  ],
  providers: [provideIcons({ remixGithubFill })],
  templateUrl: "./login-admin-form.html",
  styleUrl: "./login-admin-form.css",
})
export class LoginAdminForm {
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);

  readonly isSubmitting = input(false);
  readonly errorMessage = input<string | null>(null);

  readonly submitLoginWithAdmin = output<LoginRequest>();
  readonly formEdited = output<void>();

  readonly form = this.fb.nonNullable.group({
    email: ["", [Validators.required, Validators.email]],
    password: ["", [Validators.required, Validators.minLength(8)]],
  });

  /**
   * Subscribes to form value changes and emits a formEdited event
   * whenever the form is modified.
   */
  constructor() {
    this.form.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        if (this.errorMessage()) {
          this.formEdited.emit();
        }
      });
  }

  /**
   *  Submits the login form. If the form is invalid, it marks all controls as touched to trigger validation messages.
   * @returns void
   */
  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { email, password } = this.form.getRawValue();

    this.submitLoginWithAdmin.emit({
      email: email.trim(),
      password,
    });
  }
}
